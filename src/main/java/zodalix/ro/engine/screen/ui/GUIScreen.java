package zodalix.ro.game.screen.ui;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.game.asset.GameShader;
import zodalix.ro.game.utils.position.Point2D;
import zodalix.ro.game.utils.NamespacedKey;
import zodalix.ro.game.screen.InputListeningGameScreen;
import zodalix.ro.game.screen.ui.elements.GUIElement;
import zodalix.ro.game.utils.RenderingUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

/**
 * Represents a game interface with common controls such as buttons, sliders, etc...
 */
public class GUIScreen implements InputListeningGameScreen {
    private final List<GUIElement> elements;
    private final GameShader defaultShader;

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
            var gameRenderer = RoguesOdyssey.instance().renderer;

            if (element.boundingBox().containsPoint(RenderingUtils.transformPoint(element.x(), gameRenderer), element.y(), mouseX, mouseY))
                element.onElementEvent(new GUIElement.HoverEvent(false), mouseX, mouseY);
            else if (element.isHoveredOver()) element.onElementEvent(new GUIElement.HoverEvent(true), mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(int button, int mods, int action, Matrix4f projectionMatrix, Point2D mousePosition) {
        var window = RoguesOdyssey.instance().windowHandle;
        var gameRenderer = RoguesOdyssey.instance().renderer;

        float mouseX = mousePosition.x, mouseY = mousePosition.y;

        for (var element : elements) {
            if (element.boundingBox().containsPoint(element.x(), element.y(), mouseX, mouseY))
                element.onElementEvent(new GUIElement.ClickEvent(button, mods, action), mouseX, mouseY);
        }
    }

    @Override
    public void keyboardInput(int key, int mods, int action) {

    }
}
