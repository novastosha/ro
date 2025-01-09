package zodalix.ro.engine.screen.ui;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import zodalix.ro.engine.screen.TabScreen;
import zodalix.ro.engine.screen.ui.elements.SelectableGUIElement;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.engine.asset.GameShader;
import zodalix.ro.engine.utils.position.Point2D;
import zodalix.ro.engine.utils.NamespacedKey;
import zodalix.ro.engine.screen.InputListeningGameScreen;
import zodalix.ro.engine.screen.ui.elements.GUIElement;
import zodalix.ro.engine.utils.RenderingUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents a game interface with common controls such as buttons, sliders, etc...
 */
public class GUIScreen implements InputListeningGameScreen, TabScreen {
    public static final int MOD_TAB_ACTION = 128;

    private final List<GUIElement> elements;
    private final GameShader defaultShader;

    protected int lastTabbedElement = -1;

    {
        this.elements = new ArrayList<>();
        this.defaultShader = RoguesOdyssey.instance().assetManager
                .getShader(NamespacedKey.getDefault("shader/default"));
    }

    public void addElement(GUIElement element, GUIElement... elements) {
        this.elements.add(element);
        this.elements.addAll(Arrays.asList(elements));
    }

    @Override
    public void draw(Matrix4f projectionMatrix, float deltaTime) {
        var window = RoguesOdyssey.instance().windowHandle;
        var gameRenderer = RoguesOdyssey.instance().renderer;

        float mouseX, mouseY;
        {
            double[] mouseXA = new double[1], mouseYA = new double[1];
            glfwGetCursorPos(window, mouseXA, mouseYA);

            int width = gameRenderer.getLastKnownWindowWidth(), height = gameRenderer.getLastKnownWindowHeight();

            float ndcX = (float) (2.0 * mouseXA[0] / width - 1.0);
            float ndcY = (float) (1.0 - 2.0 * mouseYA[0] / height);

            var copyPM = new Matrix4f(projectionMatrix);
            copyPM.invert();

            var ndcPos = new Vector4f(ndcX, ndcY, -.1f, .1f);
            var relativeCoordinates = copyPM.transform(ndcPos);

            mouseX = relativeCoordinates.x;
            mouseY = relativeCoordinates.y;
        }

        elementEvents(mouseX, mouseY);
        for (var element : elements) element.draw(this, mouseX, mouseY, projectionMatrix, deltaTime);
    }

    private void elementEvents(float mouseX, float mouseY) {
        for (var element : elements) {
            int index = this.elements.indexOf(element);

            var gameRenderer = RoguesOdyssey.instance().renderer;

            if (element.boundingBox().containsPoint(RenderingUtils.transformPoint(element.x(), gameRenderer), element.y(), mouseX, mouseY))
                element.onElementEvent(new GUIElement.HoverEvent(false), mouseX, mouseY);
            else if (element.isHoveredOver() && index != lastTabbedElement) element.onElementEvent(new GUIElement.HoverEvent(true), mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(int button, int mods, int action, Matrix4f projectionMatrix, Point2D mousePosition) {
        float mouseX = mousePosition.x, mouseY = mousePosition.y;

        for (var element : elements) {
            if (element.boundingBox().containsPoint(element.x(), element.y(), mouseX, mouseY))
                element.onElementEvent(new GUIElement.ClickEvent(button, mods, action), mouseX, mouseY);
        }
    }

    @Override
    public void keyboardInput(int key, int mods, int action) {
        this.processTabulateInput(key, mods, action);
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Override this to omit tabulation's {@link GUIScreen#keyboardInput(int, int, int)} logic.
     */
    protected void processTabulateInput(int key, int mods, int action) {
        if(key != GLFW_KEY_ENTER) return;
        if(action == GLFW_PRESS) return;
        if(this.lastTabbedElement == -1) return;

        var rightClick = (mods & GLFW_MOD_ALT) != 0;

        var element = this.elements.get(this.lastTabbedElement);
        element.onElementEvent(
                new GUIElement.ClickEvent
                        (rightClick ? GLFW_MOUSE_BUTTON_2 : GLFW_MOUSE_BUTTON_1, mods | GUIScreen.MOD_TAB_ACTION, action)
                , element.x(), element.y());
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@code super} implements a basic tabulating logic, change it as you wish.
     *
     * @param modifiers keyboard modifiers alongside the Tab key. e.g. Ctrl+Tab
     */
    @Override
    public void tabulated(int modifiers) {
        if (!this.elements.isEmpty()) {
            if (this.lastTabbedElement + 1 < this.elements.size())
                for (int i = this.lastTabbedElement + 1; i < this.elements.size(); i++) {
                    if (!(this.elements.get(i) instanceof SelectableGUIElement)) continue;

                    this.lastTabbedElement = i;
                    break;
                }
            else {
                var element = this.elements.get(this.lastTabbedElement);
                element.onElementEvent(new GUIElement.HoverEvent(true), element.x(), element.y());

                lastTabbedElement = -1;
            }
        }

        if (lastTabbedElement > 0) {
            var element = this.elements.get(this.lastTabbedElement);
            element.onElementEvent(new GUIElement.HoverEvent(false), element.x(), element.y());
        }
    }
}
