package zodalix.ro.game.gui;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import zodalix.ro.engine.base.Tickable;
import zodalix.ro.engine.screen.InputListeningGameScreen;
import zodalix.ro.engine.utils.position.Point2D;
import zodalix.ro.game.dungeon.Dungeon;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static zodalix.ro.engine.input.GameInputHandler.ESCAPE;

public class DungeonScreen implements InputListeningGameScreen, Tickable {
    private final Dungeon dungeon;

    public DungeonScreen(@NotNull final Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    @Override
    public void draw(Matrix4f projectionMatrix, float deltaTime) {
        // Draw player model, draw dungeon walls, draw monsters, draw other objectives.
        // Draw health bar and any other HUD.

        var player = this.dungeon.getPlayer();

        player.draw(projectionMatrix, deltaTime);
    }

    @Override
    public void mouseClicked(int button, int mods, int action, Matrix4f projectionMatrix, Point2D mousePosition) {

    }

    @Override
    public void keyboardInput(int key, int mods, int action) {
        if(ESCAPE.isDown()) {

            return;
        }

        // Check for released keys.
        if(action != GLFW_RELEASE) return;
        var player = this.dungeon.getPlayer();
        if(player.isControlled())
            // Perhaps there is an annotation similar to @Contract to achieve this?
            //noinspection DataFlowIssue (ControllableEntity#isControlled() = true: guarentees that ControllableEntity#controller() isn't null)
            player.controller().checkInput(key);

    }
}
