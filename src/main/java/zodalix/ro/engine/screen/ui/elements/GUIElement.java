package zodalix.ro.engine.screen.ui.elements;

import org.joml.Matrix4f;
import zodalix.ro.engine.utils.position.Point2D;
import zodalix.ro.engine.utils.BoundingBox;
import zodalix.ro.engine.screen.ui.GUIScreen;

public interface GUIElement {
    boolean isHoveredOver();

    sealed interface Event permits HoverEvent, ClickEvent {}

    float x();
    float y();

    void changePosition(Point2D point);

    BoundingBox boundingBox();

    void draw(GUIScreen screen, float cursorX, float cursorY, Matrix4f projectionMatrix, float deltaTime);
    void onElementEvent(Event event, float cursorX, float cursorY);


    record HoverEvent(boolean isHoverEnd) implements Event {}

    record ClickEvent(int button, int modifiers, int action) implements Event {}

}
