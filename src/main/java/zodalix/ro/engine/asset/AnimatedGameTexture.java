package zodalix.ro.engine.asset;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import zodalix.ro.engine.asset.provider.AssetProvider;
import zodalix.ro.engine.utils.BoundingBox;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.engine.utils.position.Point2D;
import zodalix.ro.engine.utils.NamespacedKey;
import zodalix.ro.engine.renderer.DrawProperty;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;

public class AnimatedGameTexture extends GameTexture {
    private static final Gson GSON = new Gson();

    private final int totalFrames, widthPerFrame;
    private final float frameDuration;

    AnimatedGameTexture(NamespacedKey key) {
        super(key);

        var resource = RoguesOdyssey.instance().assetManager.provideAsset("/assets/" + key.toString().replaceAll(":", "/") + ".json");
        if (resource == null) throw AssetProvider.RESOURCE_NOT_FOUND;

        try (resource; final var reader = new InputStreamReader(resource)) {
            var root = GSON.fromJson(reader, JsonObject.class);

            this.totalFrames = root.get("frames").getAsInt();
            this.widthPerFrame = root.get("wpf").getAsInt();
            this.frameDuration = root.get("duration").getAsInt() / 1000f;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A drawing instance for the animated texture
     * <p>
     * When grabbing a new {@link DrawInstance}, make sure to call {@link DrawInstance#tick(float)} alongside your draw call.
     */
    public final class DrawInstance {
        private int currentFrame = 0;
        private float elapsedTime = 0f;

        public void draw(AssetManager am, Point2D position, Matrix4f projectionMatrix, @Nullable BoundingBox bb, DrawProperty... drawProperties) {
            float xScale = 1f, yScale = 1f, rotation = 0;
            Color colorTransform = null;

            for (final var property: drawProperties)
                switch (property) {
                    case DrawProperty.Scale val -> {
                        xScale = val.value();
                        yScale = val.value(); // Uniform scaling.
                    }
                    case DrawProperty.Rotation val -> rotation = val.degrees();
                    case DrawProperty.ColorTransform val -> colorTransform = val.color();
                    case DrawProperty.Dimensions _ -> {}
                    case DrawProperty.StretchProperty _ -> {}
                }

            float frameHeight = getHeight() / (float) totalFrames;
            AnimatedGameTexture.super.drawDefault0(am, position, 0, frameHeight * currentFrame, getWidth(), frameHeight * ((float) currentFrame + 1f), widthPerFrame, getHeight(), xScale, yScale, projectionMatrix, colorTransform, rotation, bb);
        }

        public void tick(float deltaTime) {
            elapsedTime += deltaTime;

            if (elapsedTime >= frameDuration) {
                int framesToAdvance = (int) (elapsedTime / frameDuration);

                currentFrame = (currentFrame + framesToAdvance) % totalFrames;
                elapsedTime -= framesToAdvance * frameDuration;
            }
        }
    }

    @Override
    @Contract("_, _, _, _ -> fail")
    public void drawDefault(AssetManager am, Point2D position, Matrix4f projectionMatrix, @Nullable BoundingBox bb, DrawProperty... drawProperties) {
        throw new UnsupportedOperationException("Animated Textures must be drawn using a DrawInstance");
    }

    @Contract("-> new")
    public DrawInstance getDrawInstance() {
        return new DrawInstance();
    }
}
