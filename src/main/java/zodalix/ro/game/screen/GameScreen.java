package zodalix.ro.game.screen;

import org.joml.Matrix4f;

public interface GameScreen {

    void draw(Matrix4f projectionMatrix, float deltaTime);

    /**
     * @return if true, the screen renderer will keep rendering the screen underneath this one as background.
     */
    default boolean isOverlayScreen() { return false; }
}
