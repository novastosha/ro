package zodalix.ro.engine.input;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import zodalix.ro.engine.utils.NamespacedKey;
import zodalix.ro.game.RoguesOdyssey;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class GameInputHandler {
    public static final Keybinding MOVE_RIGHT = new Keybinding(NamespacedKey.getDefault("key/move_right"));
    public static final Keybinding MOVE_LEFT = new Keybinding(NamespacedKey.getDefault("key/move_left"));
    public static final Keybinding JUMP = new Keybinding(NamespacedKey.getDefault("key/jump"));

    public static final Keybinding ESCAPE = new Keybinding(NamespacedKey.getDefault("key/escape"));

    private int lastKnownMouseMods = 0;
    private final Set<Integer> mouseButtonsHeld = new HashSet<>();
    private final Map<Keybinding, Integer> keybindings;

    private static final Logger log = LogManager.getLogger(GameInputHandler.class);
    private static final Gson gson = new Gson();

    public GameInputHandler(RoguesOdyssey instance) {
        this.keybindings = new HashMap<>();
    }

    /**
     * Do NOT use this method for player input or anything that requires multiple keys.
     *
     * @param keycode the keycode received
     * @param modifiers any modifiers that were also pressed like CTRL or SHIFT
     * @param action GLFW_RELEASE or GLFW_PRESS
     */
    public void keyboardInputReceived(int keycode, int modifiers, int action) {
        if (RoguesOdyssey.instance().renderer.handleKeyboardInput(keycode, modifiers, action)) return;
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

    public Optional<Integer> action(Keybinding keybinding) {
        if (keybindings.getOrDefault(keybinding, -1) == -1) return Optional.empty();
        return Optional.of(keybindings.get(keybinding));
    }

    private String getKeyboardLayout() {
        return "qwerty"; // FIXME: Look it up!
    }

    public NamespacedKey defaultKeymap() {
        return NamespacedKey.getDefault("keymaps/default_" + getKeyboardLayout() + ".json");
    }

    public void registerKeybinding(@Nullable NamespacedKey keymapPath, Keybinding... keybindings) {
        if(keybindings.length == 0) return;

        JsonObject keybindingsJson = null;
        if (keymapPath != null) {
            try (var inputStream = RoguesOdyssey.instance().assetManager.getInputStream(keymapPath)) {
                if (inputStream == null) throw new IllegalStateException(keymapPath + " keymap doesn't exist.");

                var reader = gson.newJsonReader(new InputStreamReader(inputStream));


                keybindingsJson = gson.fromJson(reader, JsonObject.class);
                reader.close();
            } catch (IOException e) {
                log.fatal("Encountered an I/O exception whilst loading keymap!");
                throw new RuntimeException(e);
            }
        }

        for (var keybinding : keybindings) {
            if(this.keybindings.containsKey(keybinding)) continue;

            int defaultBinding = -1;
            if(keybindingsJson != null  && keybindingsJson.has(keybinding.toString()))
                defaultBinding = keybindingsJson.get(keybinding.toString()).getAsInt();

            this.keybindings.put(keybinding, defaultBinding);
        }
    }
}
