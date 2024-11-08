package zodalix.ro.game.gui;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.engine.screen.ui.GUIScreen;
import zodalix.ro.engine.screen.ui.elements.Button;
import zodalix.ro.engine.screen.ui.elements.GUIElement;
import zodalix.ro.engine.screen.ui.elements.text.Text;
import zodalix.ro.engine.screen.ui.elements.text.TextComponent;
import zodalix.ro.game.dungeon.Dungeon;

public class TitleScreen extends GUIScreen {

    private final BackgroundScreen backgroundScreen;

    {
        {
            this.addElement(new Text(
                    0, 8.5f, 1.5f, TextComponent.parse("<pink bold shadow>Rogue's Odyssey")
            ));
            this.backgroundScreen = new BackgroundScreen();
        }

        float y = 1f;
        float x = 0f;

        this.addElement(
                newGameButton(x, y),
                continueGameButton(x, y),
                optionsButton(x, y)
        );
    }

    @Override
    public void draw(Matrix4f projectionMatrix, float deltaTime) {
        this.backgroundScreen.draw(projectionMatrix, deltaTime);
        super.draw(projectionMatrix, deltaTime);
    }


    private GUIElement newGameButton(float x, float y) {
        return new Button(
                new Button.Style.Default(TextComponent.parse("<shadow>New Game")),
                x,
                y,
                event -> {
                    if (!(event instanceof GUIElement.ClickEvent(int button, int _, int action))) return;
                    if (button != 0 && action != GLFW.GLFW_RELEASE) return;

                    var dung = Dungeon.dummyDungeon();
                    RoguesOdyssey.instance().setDungeon(dung);

                    RoguesOdyssey.instance()
                            .renderer
                            .setCurrentScreen(new DungeonScreen(dung)); // FIXME: REMOVE "dummyDungeon"
                }
        ); //TODO: Figure out a better way to handle button interactions. (or input in general)
           //TODO: Really need to figure out a better way for interactions this is bullshit.
    }

    private GUIElement continueGameButton(float x, float y) {
        return new Button(
                new Button.Style.Default(TextComponent.parse("<shadow>Continue")),
                x,
                y - (1.2125f * 1)
        );
    }

    private GUIElement optionsButton(float x, float y) {
        return new Button(
                new Button.Style.Default(TextComponent.parse("<shadow>Options")),
                x,
                y - (1.2125f * 2)
        );
    }
}
