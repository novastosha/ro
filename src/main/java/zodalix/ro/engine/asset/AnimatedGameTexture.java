package zodalix.ro.game.asset;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.joml.Matrix4f;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.game.utils.position.Point2D;
import zodalix.ro.game.utils.NamespacedKey;
import zodalix.ro.game.renderer.DrawProperty;

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
        assert resource != null : "Couldn't find resource.";

        try (resource; final var reader = new InputStreamReader(resource)) {
            var root = (JsonObject) GSON.fromJson(reader, JsonObject.class);

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

        public void draw(AssetManager am, Point2D position, Matrix4f projectionMatrix, DrawProperty... drawProperties) {
            float scale = 1f, rotation = 0;
            Color colorTransform = null;
            for (var property: drawProperties) {
                if(property instanceof DrawProperty.Scale scale1) scale = scale1.value();
                if(property instanceof DrawProperty.Rotation rotation1) rotation = rotation1.degrees();
                if(property instanceof DrawProperty.ColorMultiply colorMultiply) colorTransform = colorMultiply.color();
            }

            float frameHeight = getHeight() / (float) totalFrames;
            AnimatedGameTexture.super.drawDefault0(am,position,0,frameHeight *currentFrame,getWidth(), frameHeight * ((float) currentFrame +1f),widthPerFrame,getHeight(),scale,projectionMatrix,colorTransform,rotation);
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
    public void drawDefault(AssetManager am, Point2D position, Matrix4f projectionMatrix, DrawProperty... drawProperties) {
        throw new UnsupportedOperationException("Animated Textures must be drawn using a DrawInstance");
    }

    public DrawInstance getDrawInstance() {
        return new DrawInstance();
    }
}
