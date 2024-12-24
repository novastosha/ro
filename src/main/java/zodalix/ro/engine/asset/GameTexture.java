package zodalix.ro.engine.asset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import org.lwjgl.BufferUtils;
import zodalix.ro.engine.asset.provider.AssetProvider;
import zodalix.ro.engine.utils.BoundingBox;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.engine.utils.position.Point2D;
import zodalix.ro.engine.utils.NamespacedKey;
import zodalix.ro.engine.renderer.DrawProperty;
import zodalix.ro.engine.utils.RenderingUtils;

import java.awt.Color;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;
import static zodalix.ro.engine.utils.RenderingUtils.normalizeRGBA;

public class GameTexture {
    private static final Logger logger = LogManager.getLogger("Assets");

    private final NamespacedKey key;
    private transient final int glTextureId;
    private final int width, height;

    GameTexture(NamespacedKey key) {
        this.key = key;

        var resource = RoguesOdyssey.instance().assetManager.provideAsset("/assets/" + key.toString().replaceAll(":", "/") + ".png");
        if (resource == null) throw AssetProvider.RESOURCE_NOT_FOUND;

        byte[] imageBytes;
        try (resource) {
            imageBytes = resource.readAllBytes();
        } catch (IOException e) {
            logger.fatal("Encountered an I/O exception whilst loading asset {}", getKey());
            throw new RuntimeException(e);
        }

        var buffer = ByteBuffer.allocateDirect(imageBytes.length);
        {
            buffer.put(imageBytes);
            buffer.position(0);
        }

        this.glTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, glTextureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        var width = BufferUtils.createIntBuffer(1);
        var height = BufferUtils.createIntBuffer(1);
        var channels = BufferUtils.createIntBuffer(1);

        var image = stbi_load_from_memory(buffer, width, height, channels, 4);
        if (image == null) throw new IllegalStateException("Couldn't load asset image.");

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);


        this.width = width.get(0);
        this.height = height.get(0);

        stbi_image_free(image); // Free allocated memory
    }

    public NamespacedKey getKey() {
        return key;
    }

    public int getGlTextureId() {
        return glTextureId;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void drawDefault(AssetManager am, Point2D position, Matrix4f projectionMatrix, @Nullable BoundingBox bb, DrawProperty... drawProperties) {
        float width = getWidth(), height = getHeight();
        float scale = 1f, rotation = 0;
        Color colorTransform = null;

        for (final var property: drawProperties)
            switch (property) {
                case DrawProperty.Scale val -> scale = val.value();
                case DrawProperty.Rotation val -> rotation = val.degrees();
                case DrawProperty.ColorTransform val -> colorTransform = val.color();
                case DrawProperty.Dimensions dimensions -> {
                    width = dimensions.width();
                    height = dimensions.height();
                }
            }

        drawDefault0(am, position, 0, 0, getWidth(), getHeight(), width, height, scale, projectionMatrix, colorTransform, rotation,bb);
    }

    protected void drawDefault0(AssetManager am, Point2D position, float beginWidth, float beginHeight, float endWidth, float endHeight, float width, float height, float scale, Matrix4f projectionMatrix, Color colorTransform, float rotationDegrees, @Nullable BoundingBox bb) {
        if(bb != null && !bb.isScreenVisible(position.x, position.y, projectionMatrix))
            return;

        position = RenderingUtils.transformCoordinates(position, RoguesOdyssey.instance().renderer);

        if (colorTransform == null) colorTransform = Color.white;

        var shaderProgram = am.getShader(NamespacedKey.getDefault("shader/textured")).glShaderProgram;

        glUseProgram(shaderProgram);

        int textureHandle = glGetUniformLocation(shaderProgram, "uTexture");
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.getGlTextureId());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);


        glUniform1i(textureHandle, 0);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        int positionHandle = glGetAttribLocation(shaderProgram, "aPosition"),
                texCoordHandle = glGetAttribLocation(shaderProgram, "aTexCoord"),
                colorHandle = glGetAttribLocation(shaderProgram, "aColor");

        {
            Matrix4f scratch = new Matrix4f(), modelMatrix = new Matrix4f(), mvpMatrix = new Matrix4f(), viewMatrix = new Matrix4f();

            modelMatrix.identity();
            viewMatrix.identity();

            projectionMatrix.mul(viewMatrix, mvpMatrix);

            modelMatrix.translate(position.x, position.y, 0f);
            mvpMatrix.mul(modelMatrix, scratch);

            int mvpMatrixId = glGetUniformLocation(shaderProgram, "uMVPMatrix");
            int rotationHandle = glGetUniformLocation(shaderProgram, "uRotation");

            glUniformMatrix4fv(mvpMatrixId, false, scratch.get(new float[16]));
            glUniform1f(rotationHandle, (float) Math.toRadians(rotationDegrees));

            glEnableVertexAttribArray(positionHandle);
            glEnableVertexAttribArray(texCoordHandle);
            glEnableVertexAttribArray(colorHandle);
            try (var arena = Arena.ofConfined()) {

                {
                    var verticesSegment = arena.allocate(ValueLayout.JAVA_FLOAT, 8);
                    for (int j = 0; j < 8; j++) {
                        var baseValue = (j % 2 == 0 ? width : height) * scale;
                        if (j < 2 || (j == 3 || j == 4)) baseValue = -baseValue;

                        verticesSegment.set(ValueLayout.OfFloat.JAVA_FLOAT, j * 4, baseValue);
                    }
                    glVertexAttribPointer(positionHandle, 2, GL_FLOAT, false, 0, verticesSegment.address());
                }

                {
                    float textureLeft = beginHeight / this.getHeight(),
                            textureRight = endHeight / this.getHeight(),
                            textureBottom = beginWidth / this.getWidth(),
                            textureTop = endWidth / this.getWidth();

                    var textureCoordinatesSegment = arena.allocate(ValueLayout.OfFloat.JAVA_FLOAT, 8);
                    for (int j = 0; j < 8; j++) {
                        float value = 0;
                        if (j == 0 || j == 4) value = textureLeft;
                        else if (j == 2 || j == 6) value = textureRight;
                        if (j % 2 != 0) {
                            if (j < 4) value = textureTop;
                            else value = textureBottom;
                        }

                        textureCoordinatesSegment.set(ValueLayout.OfFloat.JAVA_FLOAT, j * 4, value);
                    }

                    glVertexAttribPointer(texCoordHandle, 2, GL_FLOAT, false, 0, textureCoordinatesSegment.address());
                }

                {
                    var segmentLength = 4 * 4;
                    var colorSegment = arena.allocate(ValueLayout.OfFloat.JAVA_FLOAT, segmentLength);

                    for (int j = 0; j < segmentLength; j += 4) {

                        colorSegment.set(ValueLayout.JAVA_FLOAT, j * 4L, normalizeRGBA(colorTransform.getRed()));
                        colorSegment.set(ValueLayout.JAVA_FLOAT, (j + 1) * 4L, normalizeRGBA(colorTransform.getGreen()));
                        colorSegment.set(ValueLayout.JAVA_FLOAT, (j + 2) * 4L, normalizeRGBA(colorTransform.getBlue()));
                        colorSegment.set(ValueLayout.JAVA_FLOAT, (j + 3) * 4L, normalizeRGBA(255)); // TODO: Figure out alpha channel
                    }

                    glVertexAttribPointer(colorHandle, 4, GL_FLOAT, false, 0, colorSegment.address());
                }

                // Draw the text
                glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

            }
            glDisableVertexAttribArray(positionHandle);
            glDisableVertexAttribArray(texCoordHandle);
            glDisableVertexAttribArray(colorHandle);
        }

        glUseProgram(0);
        glBindTexture(GL_TEXTURE_2D, 0);
        glDisable(GL_BLEND);
    }
}
