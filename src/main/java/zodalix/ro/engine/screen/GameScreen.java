package zodalix.ro.engine.screen;

import org.joml.Matrix4f;
import zodalix.ro.engine.Tickable;

public interface GameScreen extends Tickable {

    void draw(Matrix4f projectionMatrix, float deltaTime);

    /**
     * @return if true, the screen renderer will keep rendering the screen underneath this one as background.
     */
    default boolean isOverlayScreen() { return false; }

    @Override
    default void tick(float deltaTime) {}
}
