package zodalix.ro.engine.input;

import org.lwjgl.glfw.GLFW;
import zodalix.ro.engine.utils.NamespacedKey;
import zodalix.ro.game.RoguesOdyssey;

import java.util.Optional;

public class Keybinding extends NamespacedKey {
    public Keybinding(NamespacedKey base) {
        super(base);
    }

    public boolean isDown() {
        Optional<Integer> optionalKey = RoguesOdyssey.instance().inputHandler.action(this);
        return optionalKey.filter
                (integer -> GLFW.glfwGetKey(RoguesOdyssey.instance().windowHandle, integer) == GLFW.GLFW_PRESS)
                .isPresent();

    }

    public boolean wasDown(int releasedKey) {
        Optional<Integer> optionalKey = RoguesOdyssey.instance().inputHandler.action(this);
        return optionalKey.filter(integer -> integer == releasedKey).isPresent();
    }
}
