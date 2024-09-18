package zodalix.ro.game.screen;

import org.joml.Matrix4f;
import zodalix.ro.game.utils.position.Point2D;

public interface InputListeningGameScreen extends GameScreen {

    void mouseClicked(int button, int mods, int action, Matrix4f projectionMatrix, Point2D mousePosition);

    void keyboardInput(int key, int mods, int action);

}
