package zodalix.ro.game.gui;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import zodalix.ro.engine.asset.GameTexture;
import zodalix.ro.engine.renderer.Camera;
import zodalix.ro.engine.renderer.DrawProperty;
import zodalix.ro.engine.renderer.EntityAttachedCamera;
import zodalix.ro.engine.screen.CameraAwareGameScreen;
import zodalix.ro.engine.screen.InputListeningGameScreen;
import zodalix.ro.engine.screen.ui.elements.text.StringTextComponent;
import zodalix.ro.engine.screen.ui.elements.text.Text;
import zodalix.ro.engine.screen.ui.elements.text.TextComponent;
import zodalix.ro.engine.utils.NamespacedKey;
import zodalix.ro.engine.utils.position.Point2D;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.game.dungeon.Dungeon;

import java.util.Locale;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import static zodalix.ro.engine.input.GameInputHandler.ESCAPE;

public class DungeonScreen implements InputListeningGameScreen, CameraAwareGameScreen {
    private final Dungeon dungeon;
    private final EntityAttachedCamera camera;


    private final Text playerCoordinates;

    private final GameTexture fullHeartTexture, halfHeartTexture, emptyHeartTexture;

    public DungeonScreen(@NotNull final Dungeon dungeon) {
        this.dungeon = dungeon;
        this.camera = new EntityAttachedCamera(this.dungeon.getPlayer());

        this.playerCoordinates = new Text(7f,9.5f,.5f, TextComponent.text("X: {}  Y: {}"));

        this.dungeon.getPlayer().movementSubscribe((position -> {
            this.playerCoordinates.setText(TextComponent.composedText("<yellow>X: {} Y: {}",position.x(), position.y()));
        }),()->{});

        {
            this.fullHeartTexture = RoguesOdyssey.instance().assetManager.getTexture(
                    NamespacedKey.getDefault("textures/gui/full_heart")
            );

            this.halfHeartTexture = RoguesOdyssey.instance().assetManager.getTexture(
                    NamespacedKey.getDefault("textures/gui/half_heart")
            );

            this.emptyHeartTexture = RoguesOdyssey.instance().assetManager.getTexture(
                    NamespacedKey.getDefault("textures/gui/empty_heart")
            );
        }
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    @Override
    public void draw(Matrix4f projectionMatrix, float deltaTime) {
        playerCoordinates.draw(null,0,0,projectionMatrix,deltaTime);
        this.drawPlayerHealth(this.dungeon.getPlayer().health(), projectionMatrix);
    }

    @Override
    public void drawCamera(Matrix4f projectionMatrix, float deltaTime) {
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

    private void drawPlayerHealth(float health, Matrix4f projectionMatrix) {
        final float HEART_WIDTH = .05f;
        final float HEART_SPACING = .35f; // Space between hearts
        final float ROW_HEIGHT = 1f; // Height between rows
        final float START_X = -9.5f;
        final float START_Y = 9.5f;

        // Total rows and columns
        final int HEARTS_PER_ROW = 10;

        float remainingHealth = health;
        float x = START_X;
        float y = START_Y;

        while (remainingHealth > 0) {
            for (int i = 0; i < HEARTS_PER_ROW && remainingHealth > 0; i++) {
                GameTexture heartTexture;

                if (remainingHealth >= 2) {
                    heartTexture = this.fullHeartTexture;
                    remainingHealth -= 2;
                } else if (remainingHealth >= 1) {
                    heartTexture = this.halfHeartTexture;
                    remainingHealth -= 1;
                } else {
                    heartTexture = this.emptyHeartTexture;
                    remainingHealth = 0;
                }

                heartTexture.drawDefault(
                        RoguesOdyssey.instance().assetManager,
                        new Point2D(x, y),
                        projectionMatrix,
                        null,
                        DrawProperty.scale(HEART_WIDTH)
                );

                x += HEART_WIDTH + HEART_SPACING;
            }

            // Move to the next row
            x = START_X;
            y -= ROW_HEIGHT;
        }
    }
}
