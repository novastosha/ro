package zodalix.ro.engine.screen;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import zodalix.ro.engine.renderer.Camera;

public interface CameraAwareGameScreen extends GameScreen {

    @NotNull Camera camera();
    void drawCamera(Matrix4f projectionMatrix, float deltaTime);

    @Override
    default void tick(float deltaTime) {
        GameScreen.super.tick(deltaTime);
        this.camera().tick(deltaTime);
    }
}
