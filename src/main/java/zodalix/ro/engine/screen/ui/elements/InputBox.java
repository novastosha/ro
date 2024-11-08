package zodalix.ro.engine.screen.ui.elements;

import org.joml.Matrix4f;
import zodalix.ro.engine.renderer.DrawProperty;
import zodalix.ro.engine.screen.ui.GUIScreen;
import zodalix.ro.engine.utils.BoundingBox;
import zodalix.ro.engine.utils.position.MutablePosition;
import zodalix.ro.engine.utils.position.Point2D;

import java.util.Objects;

public class InputBox implements GUIElement {
    private final MutablePosition position;
    private final BoundingBox bb;

    private boolean isHoveredOver = false;

    public InputBox(float x, float y, DrawProperty.Dimensions dimensions) {
        this.position = new MutablePosition(x,y);
        this.bb = BoundingBox
                    .rectangle( dimensions.width() / 2f, dimensions.height() /2f);
    }

    @Override
    public boolean isHoveredOver() {
        return isHoveredOver;
    }

    @Override
    public float x() {
        return this.position.x();
    }

    @Override
    public float y() {
        return this.position.y();
    }

    @Override
    public void changePosition(Point2D point) {
        this.position.setTo(point);
    }

    @Override
    public BoundingBox boundingBox() {
        return this.bb;
    }

    @Override
    public void draw(GUIScreen screen, float cursorX, float cursorY, Matrix4f projectionMatrix, float deltaTime) {
        
    }

    @Override
    public void onElementEvent(Event event, float cursorX, float cursorY) {
        if (Objects.requireNonNull(event) instanceof HoverEvent hoverEvent) this.isHoveredOver = !hoverEvent.isHoverEnd();
    }
}
