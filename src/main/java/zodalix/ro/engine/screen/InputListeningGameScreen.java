package zodalix.ro.engine.screen;

import org.joml.Matrix4f;
import zodalix.ro.engine.utils.position.Point2D;

public interface InputListeningGameScreen extends GameScreen {

    void mouseClicked(int button, int mods, int action, Matrix4f projectionMatrix, Point2D mousePosition);

    void keyboardInput(int key, int mods, int action);

}
