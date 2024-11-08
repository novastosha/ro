package zodalix.ro.engine.screen.ui.elements;

import org.joml.Matrix4f;

import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.engine.asset.GameTexture;
import zodalix.ro.engine.utils.position.Point2D;
import zodalix.ro.engine.utils.BoundingBox;
import zodalix.ro.engine.utils.NamespacedKey;
import zodalix.ro.engine.renderer.DrawProperty;
import zodalix.ro.engine.screen.ui.GUIScreen;
import zodalix.ro.engine.screen.ui.elements.text.Text;
import zodalix.ro.engine.screen.ui.elements.text.TextComponent;

import java.util.function.Consumer;

public class Button implements GUIElement {

    private static final float BUTTON_SCALE = .5f; //.375f;

    private final Consumer<Event> eventConsumer;

    private final Style style;
    private float x, y;

    public Button(Style style, float x, float y) {
        this(style, x, y, null);
    }

    public Button(Style style, float x, float y, Consumer<Event> eventConsumer) {
        this.style = style;
        this.x = x;
        this.y = y;

        this.eventConsumer = eventConsumer;

        if (style instanceof Style.Default def) def.textElement.changePosition(new Point2D(x, y));
    }

    public Style getStyle() {
        return style;
    }

    @Override
    public boolean isHoveredOver() {
        return switch (style) {
            case Style.Default def -> def.textElement.isHoveredOver();
            case Style.Textured textured -> false;
        };
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    @Override
    public void changePosition(Point2D point) {
        this.x = point.x;
        this.y = point.y;

        if (style instanceof Style.Default def) def.textElement.changePosition(point);
    }

    @Override
    public BoundingBox boundingBox() {
        return switch (this.style) {
            case Style.Default def -> def.fitText ?
                    def.textElement.boundingBox() :
                    BoundingBox.rectangle(12f * BUTTON_SCALE, def.textElement.boundingBox().topY());
            case Style.Textured textured ->
                    BoundingBox.rectangle(textured.width * BUTTON_SCALE, textured.height * BUTTON_SCALE);
        };
    }

    @Override
    public void draw(GUIScreen screen, float cursorX, float cursorY, Matrix4f projectionMatrix, float deltaTime) {
        switch (this.style) {
            case Style.Default defaultStyle -> {
                this.drawDefaultButtonTexture(this.isHoveredOver(), projectionMatrix);
                defaultStyle.textElement.draw(screen, cursorX, cursorY, projectionMatrix, deltaTime);
            }

            case Style.Textured texturedStyle ->
                    texturedStyle.draw(x, y, this.isHoveredOver(), cursorX, cursorY, projectionMatrix);
        }
    }


    @Override
    public void onElementEvent(Event event, float cursorX, float cursorY) {
        if (this.style instanceof Style.Default def)
            def.textElement.onElementEvent(event, cursorX, cursorY);

        if(eventConsumer != null) eventConsumer.accept(event);
    }

    public sealed interface Style permits
            Style.Textured, Style.Default {


        non-sealed class Default implements Style {

            private final Text textElement;
            private final boolean fitText;

            public Default(TextComponent text, boolean fitText) {
                this.textElement = new Text(0, 0, BUTTON_SCALE, text);
                this.textElement.setDrawStyle(Text.DrawStyle.CENTERED);
                this.fitText = fitText;
            }

            public Default(TextComponent text) {
                this(text, false);
            }

            public TextComponent getText() {
                return textElement.getText();
            }

            public void setText(TextComponent text) {
                this.textElement.setText(text);
            }
        }

        non-sealed class Textured implements Style {
            private final GameTexture asset;
            private final float width, height;

            public Textured(GameTexture asset, float width, float height) {
                this.asset = asset;
                this.width = width;
                this.height = height;
            }

            public void draw(float x, float y, boolean hoveredOver, float cursorX, float cursorY, Matrix4f projectionMatrix) {
                final var am = RoguesOdyssey.instance().assetManager;
                asset.drawDefault(am, new Point2D(x, y), projectionMatrix, DrawProperty.dimensions(width, height));
            }
        }
    }

    /**
     * Draws the button texture based on its hovered state.
     *
     * @param hoveredOver      true if the button is hovered over by the mouse.
     * @param projectionMatrix the renderer's projection matrix
     */
    private void drawDefaultButtonTexture(boolean hoveredOver, Matrix4f projectionMatrix) {
        final var neededHeight = (boundingBox().topY()) / BUTTON_SCALE;
        final var neededWidth = ((Style.Default) this.style).fitText ?
                (boundingBox().rightX()) / BUTTON_SCALE + .25f :
                24 * BUTTON_SCALE; // Width

        final var am = RoguesOdyssey.instance().assetManager;

        var texture = hoveredOver ?
                am.getTexture(NamespacedKey.getDefault("textures/buttons/button_highlighted"))
                :
                am.getTexture(NamespacedKey.getDefault("textures/buttons/button"));

        texture.drawDefault(am, new Point2D(x, y), projectionMatrix, DrawProperty.scale(BUTTON_SCALE), DrawProperty.dimensions(neededWidth, neededHeight));
    }
}
