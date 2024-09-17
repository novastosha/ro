package zodalix.ro.game.renderer;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.game.utils.position.Point2D;
import zodalix.ro.game.screen.GameScreen;
import zodalix.ro.game.screen.InputListeningGameScreen;
import zodalix.ro.game.screen.ui.elements.text.Text;
import zodalix.ro.game.screen.ui.elements.text.TextComponent;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class GameRenderer {

    private Text debugText, ramText,frameInfoText;
    private boolean showDebugInfo = false;


    private final Matrix4f projectionMatrix;
    private boolean shouldOverlay;
    private GameScreen currentScreen, overlayingScreen;

    private volatile int lastKnownWindowWidth, lastKnownWindowHeight;

    public int getLastKnownWindowWidth() {
        return lastKnownWindowWidth;
    }

    public int getLastKnownWindowHeight() {
        return lastKnownWindowHeight;
    }

    public GameRenderer(RoguesOdyssey instance) {
        this.currentScreen = null;
        this.shouldOverlay = false;

        this.projectionMatrix = new Matrix4f()
                .setOrtho(-10.0f, 10.0f, -10.0f, 10.0f, -0.1f, 0.1f);
    }

    public void postInit() {
        this.debugText = new Text(-9.5f,9.5f, .5f, TextComponent.text("No data on last frame."));
        this.debugText.setDrawStyle(Text.DrawStyle.ABSOLUTE);

        this.frameInfoText = new Text(-9.5f,7f, .37f, TextComponent.text("No data on last frame."));
        this.frameInfoText.setDrawStyle(Text.DrawStyle.ABSOLUTE);

        this.ramText = new Text(-9.5f,8f, .5f, TextComponent.text("RAM usage should be here"));
        this.ramText.setDrawStyle(Text.DrawStyle.ABSOLUTE);
    }

    public GameScreen getCurrentScreen() {
        return currentScreen;
    }

    public void setCurrentScreen(GameScreen newScreen) {
        //noinspection AssignmentUsedAsCondition
        if ((this.shouldOverlay = newScreen.isOverlayScreen() && currentScreen != null)) {
            this.overlayingScreen = this.currentScreen;
        }

        this.currentScreen = newScreen;
    }

    public void render(float deltaTime) {
        if (this.currentScreen == null) return;

        //TODO: Maybe overlay screens should continue to render the underlying screen but just make it non-interactive.
        if (shouldOverlay) overlayingScreen.draw(projectionMatrix, deltaTime);
        this.currentScreen.draw(projectionMatrix,deltaTime);

        if(showDebugInfo) {
            this.debugText.draw(null, 0f, 0f, projectionMatrix,deltaTime);
            this.frameInfoText.draw(null, 0f, 0f, projectionMatrix, deltaTime);
            this.ramText.draw(null, 0f, 0f, projectionMatrix, deltaTime);
        }

    }

    /**
     * Renderer callback that is called when the game window is resized. Readjusts the viewport and the {@link GameRenderer#projectionMatrix}
     * <p>
     * Should only be invoked by the GL callback: {@link org.lwjgl.glfw.GLFW#glfwSetWindowSizeCallback(long, GLFWWindowSizeCallbackI)}
     *
     * @param newWidth  the new window width.
     * @param newHeight the new window height.
     */
    public void windowResized(int newWidth, int newHeight) {
        this.lastKnownWindowWidth = newWidth;
        this.lastKnownWindowHeight = newHeight;

        final float ratio = newWidth / (float) newHeight;

        glViewport(0, 0, newWidth, newHeight);
        this.projectionMatrix.setOrtho(-10f * ratio, 10f * ratio, -10f, 10f, -10, 10);
    }

    public void handleMouseInput(int mouseButton, int modifiers, int action) {
        if (!(this.currentScreen instanceof InputListeningGameScreen ilGameScreen)) return;

        float mouseX, mouseY;
        {
            double[] mouseXA = new double[1], mouseYA = new double[1];
            glfwGetCursorPos(RoguesOdyssey.instance().windowHandle, mouseXA, mouseYA);

            int width = this.getLastKnownWindowWidth(), height = this.getLastKnownWindowHeight();

            float ndcX = (float) (2.0 * mouseXA[0] / width - 1.0);
            float ndcY = (float) (1.0 - 2.0 * mouseYA[0] / height);

            var copyPM = new Matrix4f(projectionMatrix);
            copyPM.invert();

            var ndcPos = new Vector4f(ndcX, ndcY, -.1f, .1f);
            var relativeCoordinates = copyPM.transform(ndcPos);

            mouseX = relativeCoordinates.x;
            mouseY = relativeCoordinates.y;
        }

        ilGameScreen.mouseClicked(mouseButton, modifiers, action, this.projectionMatrix, new Point2D(mouseX,mouseY));
    }

    public void handleKeyboardInput(int keycode, int modifiers, int action) {
        System.out.println(Thread.currentThread().getName());
        this.showDebugInfo = action != GLFW_RELEASE && keycode == GLFW_KEY_F1;

        if (!(this.currentScreen instanceof InputListeningGameScreen ilGameScreen)) return;
        ilGameScreen.keyboardInput(keycode, modifiers, action);
    }

    public void displayFPS(long fps, long frametime, long renderDiff) {
        debugText.setText(TextComponent.composedText("<cyan shadow>{} FPS", fps));
        frameInfoText.setText(TextComponent.composedText("<white shadow>Frame-time: <cyan shadow>{}ms <white shadow>Rendering took: <pink bold>{}ms",frametime,renderDiff));

        {
            Runtime runtime = Runtime.getRuntime();

            long totalMemory = runtime.totalMemory();
            long usedMemory = totalMemory - runtime.freeMemory();

            try {
                var offHeapMemory = ((Long) java.lang.management.ManagementFactory.getPlatformMBeanServer().getAttribute(new javax.management.ObjectName("java.nio:type=BufferPool,name=direct"), "MemoryUsed")) / (1024 * 1024);
                ramText.setText(TextComponent.composedText("<shadow>On-heap: <orange shadow bold>{}MB<break>  <shadow>Off-heap: <orange shadow bold>{}MB",(usedMemory/1024/1024),offHeapMemory));
            } catch (Throwable _) {}
        }
    }
}
