package zodalix.ro.engine.screen.ui.elements.text;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.engine.asset.GameShader;
import zodalix.ro.engine.asset.GameTexture;
import zodalix.ro.engine.utils.position.Point2D;
import zodalix.ro.engine.utils.BoundingBox;
import zodalix.ro.engine.utils.NamespacedKey;
import zodalix.ro.engine.screen.ui.GUIScreen;
import zodalix.ro.engine.screen.ui.elements.GUIElement;
import zodalix.ro.engine.utils.RenderingUtils;

import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import static org.lwjgl.opengl.GL33.*;
import static zodalix.ro.engine.screen.ui.elements.text.StringTextComponent.LOW_HANGING_CHARACTERS;
import static zodalix.ro.engine.screen.ui.elements.text.StringTextComponent.LOW_OFFSET_CHARACTERS;
import static zodalix.ro.engine.utils.RenderingUtils.normalizeRGBA;

public class Text implements GUIElement {


    private boolean isHoveredOver = false;

    public TextComponent getText() {
        return text;
    }

    public void setText(TextComponent text) {
        this.text = text;
        this.bb = this.calculateBoundingBox();
    }


    public enum DrawStyle {
        CENTERED,
        ABSOLUTE
    }

    private final GameTexture texture;

    private final GameShader shader;

    private TextComponent text;

    private float x, y;

    private DrawStyle drawStyle = DrawStyle.CENTERED;

    public void setDrawStyle(DrawStyle drawStyle) {
        this.drawStyle = drawStyle;
    }


    private BoundingBox bb;

    private final float scale;


    public Text(float x, float y, float scale, TextComponent text) {
        this.text = text;
        this.scale = scale;

        this.x = x;
        this.y = y;

        this.bb = calculateBoundingBox();
    }

    {
        this.texture = RoguesOdyssey.instance().assetManager
                .getTexture(NamespacedKey.getDefault("text/ascii"));

        this.shader = RoguesOdyssey.instance().assetManager
                .getShader(NamespacedKey.getDefault("shader/textured"));
    }

    @Override
    public boolean isHoveredOver() {
        return isHoveredOver;
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public void changePosition(Point2D point) {
        this.x = point.x;
        this.y = point.y;
    }

    @Override
    public BoundingBox boundingBox() {
        return bb;
    }

    private BoundingBox calculateBoundingBox() {
        return text.boundingBox().mulAll(this.scale);
    }


    @Override
    public void draw(GUIScreen screen, float cursorX, float cursorY, Matrix4f projectionMatrix, float deltaTime) {
        var transformedX = RenderingUtils.transformPoint(x, RoguesOdyssey.instance().renderer);
        var centeredStartDraw = (this.bb.leftX() + transformedX) + (this.scale * this.scale);

        AtomicReference<Float> drawX = new AtomicReference<>(
                drawStyle == DrawStyle.CENTERED ?
                centeredStartDraw : transformedX
        );

        loopChildrenAndDraw(drawX, this.text,projectionMatrix);
    }

    private void loopChildrenAndDraw(AtomicReference<Float> drawX, TextComponent parentComponent,Matrix4f projectionMatrix) {
        for (int m = 0; m < parentComponent.children().size()+1; m++) {
            TextComponent component = null;

            if (m == 0) { if (!parentComponent.getClass().equals(TextComponent.class)) component = parentComponent; }
            else component = parentComponent.children().get(m - 1);

            switch (component) {
                case StringTextComponent stringTextComponent ->
                        {
                            var offset = drawStringTextComponent(stringTextComponent, drawX, projectionMatrix);
                            drawX.set(drawX.get() + offset);
                        }

                case TextComponent textComponent when textComponent.children().size() > 0 -> loopChildrenAndDraw(drawX, textComponent, projectionMatrix);

                case null, default -> {}
            }
        }
    }

    @Override
    public void onElementEvent(Event event, float cursorX, float cursorY) {
        if (Objects.requireNonNull(event) instanceof HoverEvent hoverEvent) this.isHoveredOver = !hoverEvent.isHoverEnd();
    }

    private float drawStringTextComponent(StringTextComponent component, AtomicReference<Float> x, Matrix4f projectionMatrix) {
        glBindTexture(GL_TEXTURE_2D, texture.getGlTextureId());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Enable alpha blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        var shaderProgram = this.shader.glShaderProgram;
        glUseProgram(shaderProgram);

        int textureHandle = glGetUniformLocation(shaderProgram, "uTexture");
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.texture.getGlTextureId());
        glUniform1i(textureHandle, 0);

        // Get position handle
        int positionHandle = glGetAttribLocation(shaderProgram, "aPosition"),
                texCoordHandle = glGetAttribLocation(shaderProgram, "aTexCoord"),
                colorHandle = glGetAttribLocation(shaderProgram, "aColor");

        BiFunction<Vector2f, Float,Float> drawFunc = (pos, darknessFactor) -> {
            //pos = RenderingUtils.transformCoordinates(pos, RoguesOdyssey.instance().renderer);
            var text = component.getString();

            boolean lowOffsetPassed = false;

            float xOffset = 0;
            for (int i = 0; i < text.length(); i++) {
                char character = text.charAt(i);
                if (lowOffsetPassed) {
                    xOffset -= .025f * scale;
                }

                Matrix4f scratch = new Matrix4f(), modelMatrix = new Matrix4f(), mvpMatrix = new Matrix4f(), viewMatrix = new Matrix4f();

                modelMatrix.identity();
                viewMatrix.identity();

                projectionMatrix.mul(viewMatrix, mvpMatrix);
                float xPos = (xOffset + pos.x);

                modelMatrix.translate(xPos, pos.y, 0f);
                mvpMatrix.mul(modelMatrix, scratch);

                int mvpMatrixId = glGetUniformLocation(shaderProgram, "uMVPMatrix");

                glUniformMatrix4fv(mvpMatrixId, false, scratch.get(new float[16]));


                glEnableVertexAttribArray(positionHandle);
                glEnableVertexAttribArray(texCoordHandle);
                glEnableVertexAttribArray(colorHandle);
                try (var arena = Arena.ofConfined()) {
                    int     beginX = Math.max(((int) (Math.floor(character / 16f)) * 16), 0),
                            beginY = Math.max(((character % 16) * 16), 0);

                    int     endX = beginX + 15,
                            endY = beginY + 14;
                    if (LOW_HANGING_CHARACTERS.contains(character)) endX += 1f;


                    {
                        var verticesSegment = arena.allocate(ValueLayout.JAVA_FLOAT, 8);
                        for (int j = 0; j < 8; j++) {
                            var baseValue = (10 / 16f) * scale;
                            if (j < 2 || (j == 3 || j == 4)) baseValue = -baseValue;

                            verticesSegment.set(ValueLayout.OfFloat.JAVA_FLOAT, j * 4, baseValue);
                        }
                        glVertexAttribPointer(positionHandle, 2, GL_FLOAT, false, 0, verticesSegment.address());
                    }

                    {
                        float textureLeft = beginY / 256f,
                                textureRight = endY / 256f,
                                textureBottom = beginX / 256f,
                                textureTop = endX / 256f;

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
                        var color = component.color();

                        for (int j = 0; j < segmentLength; j += 4) {

                            colorSegment.set(ValueLayout.JAVA_FLOAT, j * 4L, normalizeRGBA((int) (color.getRed() / darknessFactor)));
                            colorSegment.set(ValueLayout.JAVA_FLOAT, (j + 1) * 4L, normalizeRGBA((int) (color.getGreen() / darknessFactor)));
                            colorSegment.set(ValueLayout.JAVA_FLOAT, (j + 2) * 4L, normalizeRGBA((int) (color.getBlue() / darknessFactor)));
                            colorSegment.set(ValueLayout.JAVA_FLOAT, (j + 3) * 4L, normalizeRGBA((int) (color.getAlpha() / darknessFactor)));
                        }

                        glVertexAttribPointer(colorHandle, 4, GL_FLOAT, false, 0, colorSegment.address());
                    }

                    // Draw the text
                    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

                }
                glDisableVertexAttribArray(positionHandle);
                glDisableVertexAttribArray(texCoordHandle);
                glDisableVertexAttribArray(colorHandle);


                    xOffset += 1.1f * scale;
                    if (LOW_OFFSET_CHARACTERS.contains(character)) {
                        xOffset -= (character == 'i' || character == '\'' || character == ' ' ? .4f : 0.2f) * (scale);
                        lowOffsetPassed = true;
                    } else lowOffsetPassed = false;
            }

            return xOffset;
        };

        float xNative = x.get();

        if(component.shouldDrawShadow()) drawFunc.apply(new Vector2f((xNative - (0.05f * scale)), y - (0.05f * scale)), 1.5f);

        float offset = 0;
        for (int i = 0; i < (component.isBold() ? 2 : 1); i++) {
            float boldOffset = i * .075f * scale;
            offset = drawFunc.apply(new Vector2f(xNative + boldOffset, y), 1f);
        }

        glDisable(GL_BLEND);
        glUseProgram(0);
        return offset;
    }
}
