package zodalix.ro.game.gui;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import zodalix.ro.engine.renderer.Camera;
import zodalix.ro.engine.renderer.EntityAttachedCamera;
import zodalix.ro.engine.screen.CameraAwareGameScreen;
import zodalix.ro.engine.screen.InputListeningGameScreen;
import zodalix.ro.engine.screen.ui.elements.text.StringTextComponent;
import zodalix.ro.engine.screen.ui.elements.text.Text;
import zodalix.ro.engine.utils.position.Point2D;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.game.dungeon.Dungeon;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.opengl.GL33.*;

import static zodalix.ro.engine.input.GameInputHandler.ESCAPE;

public class DungeonScreen implements InputListeningGameScreen, CameraAwareGameScreen {
    private final Dungeon dungeon;
    private final EntityAttachedCamera camera;

    private final Text testText;

    public DungeonScreen(@NotNull final Dungeon dungeon) {
        this.dungeon = dungeon;
        this.camera = new EntityAttachedCamera(this.dungeon.getPlayer());

        this.testText = new Text(0, 0, 1, new StringTextComponent("This text should stay here"));
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    @Override
    public void draw(Matrix4f projectionMatrix, float deltaTime) {
        // Draw health bar and any other HUD.
    }

    @Override
    public void drawCamera(Matrix4f projectionMatrix, float deltaTime) {


        testText.draw(null, 0, 0, projectionMatrix, deltaTime);

        // Draw player model, draw dungeon walls, draw monsters, draw other objectives.
        var player = this.dungeon.getPlayer();
        player.draw(projectionMatrix, deltaTime);

    }

    @Override
    public void mouseClicked(int button, int mods, int action, Matrix4f projectionMatrix, Point2D mousePosition) {

    }

    @Override
    public void keyboardInput(int key, int mods, int action) {
        if (ESCAPE.isDown()) {
            RoguesOdyssey.instance().renderer.setCurrentScreen(new TitleScreen());
            return;
        }

        // Check for released keys.
        if (action != GLFW_RELEASE) return;
        var player = this.dungeon.getPlayer();
        if (player.isControlled())
            // Perhaps there is an annotation similar to @Contract to achieve this?
            //noinspection DataFlowIssue (ControllableEntity#isControlled() = true: guarentees that ControllableEntity#controller() isn't null)
            player.controller().checkInput(key, 0);
    }

    @Override
    public @NotNull Camera camera() {
        return this.camera;
    }
}
