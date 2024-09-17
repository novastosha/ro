package zodalix.ro.game.input;

import zodalix.ro.game.RoguesOdyssey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class GameInputHandler {

    private int lastKnownMouseMods = 0;
    private final Set<Integer> mouseButtonsHeld = new HashSet<>();

    public GameInputHandler(RoguesOdyssey instance) {

    }

    public void keyboardInputReceived(int keycode, int modifiers, int action) {
        RoguesOdyssey.instance().renderer.handleKeyboardInput(keycode, modifiers, action);
    }

    public void mouseInputReceived(int mouseButton, int modifiers, int action) {
        this.lastKnownMouseMods = modifiers;
        try {
            if (action == GLFW_RELEASE) mouseButtonsHeld.remove(mouseButton);
            else mouseButtonsHeld.add(mouseButton);
        } catch (Throwable _) {}

        RoguesOdyssey.instance().renderer.handleMouseInput(mouseButton, modifiers, action);
    }

    public void checkForClickHold() {
        mouseButtonsHeld.forEach(btn -> RoguesOdyssey.instance().renderer.handleMouseInput(btn, this.lastKnownMouseMods, GLFW_PRESS));
    }
}
