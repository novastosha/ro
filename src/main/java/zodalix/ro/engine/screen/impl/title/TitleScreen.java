package zodalix.ro.engine.screen.impl.title;

import org.joml.Matrix4f;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.game.entity.Cloud;
import zodalix.ro.engine.screen.GameScreen;
import zodalix.ro.engine.screen.ui.GUIScreen;
import zodalix.ro.engine.screen.ui.elements.Button;
import zodalix.ro.engine.screen.ui.elements.GUIElement;
import zodalix.ro.engine.screen.ui.elements.text.Text;
import zodalix.ro.engine.screen.ui.elements.text.TextComponent;
import zodalix.ro.engine.utils.RenderingUtils;
import zodalix.ro.engine.utils.position.MutablePosition;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL33.*;

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
        return new Button<>(
                new Button.Style.Default(TextComponent.parse("<shadow>New Game")),
                x,
                y
        );
    }

    private GUIElement continueGameButton(float x, float y) {
        return new Button<>(
                new Button.Style.Default(TextComponent.parse("<shadow>Continue")),
                x,
                y - (1.2125f * 1)
        );
    }

    private GUIElement optionsButton(float x, float y) {
        return new Button<>(
                new Button.Style.Default(TextComponent.parse("<shadow>Options")),
                x,
                y - (1.2125f * 2)
        );
    }

    public static class BackgroundScreen implements GameScreen {

        private final Map<Cloud, MutablePosition> clouds;

        private final SecureRandom random;

        {
            this.random = new SecureRandom();
            float x = RenderingUtils.transformPoint(-9.5f, RoguesOdyssey.instance().renderer);

            this.clouds = new HashMap<>();

            int cloudSelection = 1;
            for (int i = 0; i < 20; i++) {
                var position = new MutablePosition(x += 1f + (random.nextBoolean() ? +random.nextFloat() : -random.nextFloat()), 7.5f + (random.nextBoolean() ? -1.25f : +1.75f));
                this.clouds.put(new Cloud(position, cloudSelection, true), position);

                cloudSelection++;
                if (cloudSelection > 10) cloudSelection = 0;
            }
        }

        @Override
        public void draw(Matrix4f projectionMatrix, float deltaTime) {
            tickClouds(deltaTime);

            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_BLEND);

            clouds.keySet().forEach(cloud -> cloud.draw(projectionMatrix, deltaTime));
            glDisable(GL_BLEND);
        }

        private void tickClouds(float deltaTime) {
            final float x = RenderingUtils.transformPoint(-9.5f, RoguesOdyssey.instance().renderer);

            int index = 0;
            for (var entry : clouds.entrySet()) {
                var cloud = entry.getKey();
                var pos = entry.getValue();

                boolean isFast = index % 2 != 0;

                pos.setX(pos.x() + (0.25f * deltaTime * (isFast ? 1.75f : 1)));
                var renderer = RoguesOdyssey.instance().renderer;

                if (!cloud.boundingBox().isScreenVisible(pos.x(), pos.y(), renderer)) {
                    pos.setX(x + (.5f + (random.nextBoolean() ? +random.nextFloat() : -random.nextFloat())));
                    pos.setY(7.5f + (random.nextBoolean() ? -.75f : +1f));
                }
                index++;
            }
        }
    }
}
