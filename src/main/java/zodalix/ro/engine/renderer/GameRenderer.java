package zodalix.ro.engine.renderer;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.system.MemoryUtil;
import zodalix.ro.engine.Tickable;
import zodalix.ro.engine.screen.CameraAwareGameScreen;
import zodalix.ro.engine.screen.TabScreen;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.engine.utils.position.Point2D;
import zodalix.ro.engine.screen.GameScreen;
import zodalix.ro.engine.screen.InputListeningGameScreen;
import zodalix.ro.engine.screen.ui.elements.text.Text;
import zodalix.ro.engine.screen.ui.elements.text.TextComponent;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * The {@code GameRenderer} class is responsible for rendering the game content using OpenGL and managing the
 * rendering pipeline. It handles different game screens and responds to window resizing events.
 * <p>
 * It maintains the projection matrix for transforming the game's 2D content and renders both the active
 * game screen and any overlays.
 *
 * @see GameScreen
 * @see RoguesOdyssey
 * @see org.lwjgl.opengl.GL33
 */
public class GameRenderer implements Tickable {

    private boolean isFullScreen;

    private Text debugText, ramText, frameInfoText;
    private boolean showDebugInfo = false;

    private final Matrix4f projectionMatrix, viewMatrix, renderMatrix;
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

        this.isFullScreen = false;

        this.projectionMatrix = new Matrix4f()
                .setOrtho(-10.0f, 10.0f, -10.0f, 10.0f, -1f, 1f);

        this.renderMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
    }

    /**
     * Called after initialization to set up debug information on the screen.
     * This method creates and positions text elements to display frame and memory usage information.
     *
     * @see RoguesOdyssey#startGame()
     */
    public void postInit() {
        this.debugText = new Text(-9.5f, 9.5f, .5f, TextComponent.text("No data on last frame."));
        this.debugText.setDrawStyle(Text.DrawStyle.ABSOLUTE);

        this.frameInfoText = new Text(-9.5f, 7f, .37f, TextComponent.text("No data on last frame."));
        this.frameInfoText.setDrawStyle(Text.DrawStyle.ABSOLUTE);

        this.ramText = new Text(-9.5f, 8f, .5f, TextComponent.text("RAM usage should be here"));
        this.ramText.setDrawStyle(Text.DrawStyle.ABSOLUTE);
    }

    /**
     * @return the currently active {@link GameScreen}, or {@code null} if no screen is active.
     */
    public GameScreen getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Sets the current screen to the specified {@link GameScreen}.
     *
     * @param newScreen the new screen to be displayed.
     * @see GameRenderer#setCurrentScreen(GameScreen, float)
     */
    public void setCurrentScreen(GameScreen newScreen) {
        this.setCurrentScreen(newScreen, 0f);
    }

    /**
     * Sets the current screen to the specified {@link GameScreen} with an optional fade transition.
     * If the new screen is an overlay, it preserves the underlying screen for rendering.
     *
     * @param newScreen  the new screen to be displayed.
     * @param fadeFactor the transition factor for screen changes (currently not used).
     */
    public void setCurrentScreen(GameScreen newScreen, float fadeFactor) {


        //noinspection AssignmentUsedAsCondition
        if ((this.shouldOverlay = newScreen.isOverlayScreen() && currentScreen != null)) {
            this.overlayingScreen = this.currentScreen;
        }

        this.currentScreen = newScreen;
        if (this.currentScreen instanceof CameraAwareGameScreen camAware)
            this.recalculateMatrices(camAware.camera());

    }

    /**
     * Renders the current game screen. It also displays
     * debug information if enabled.
     */
    public void render(float deltaTime) {
        if (this.currentScreen == null) return;

        if (shouldOverlay) {
            overlayingScreen.draw(projectionMatrix, deltaTime);
            if (this.overlayingScreen instanceof CameraAwareGameScreen camAwareOverlay) {
                camAwareOverlay.drawCamera(renderMatrix, deltaTime);
            } // Overlaying might be removed soon.
        }

        this.currentScreen.draw(projectionMatrix, deltaTime);
        if (this.currentScreen instanceof CameraAwareGameScreen cameraAwareGameScreen) {
            cameraAwareGameScreen.drawCamera(renderMatrix, deltaTime);
        }

        if (showDebugInfo) {
            this.debugText.draw(null, 0f, 0f, projectionMatrix, deltaTime);
            this.frameInfoText.draw(null, 0f, 0f, projectionMatrix, deltaTime);
            this.ramText.draw(null, 0f, 0f, projectionMatrix, deltaTime);
        }
    }

    /**
     * Adjusts the rendering viewport and projection matrix when the game window is resized.
     * This method is triggered by the GLFW window size callback.
     *
     * @param newWidth  the new window width.
     * @param newHeight the new window height.
     * @see org.lwjgl.glfw.GLFW#glfwSetWindowSizeCallback(long, GLFWWindowSizeCallbackI)
     */
    public void windowResized(int newWidth, int newHeight) {
        this.lastKnownWindowWidth = newWidth;
        this.lastKnownWindowHeight = newHeight;

        glViewport(0, 0, newWidth, newHeight);
        this.recalculateMatrices(this.currentScreen instanceof CameraAwareGameScreen cA ? cA.camera() : null);
    }

    /**
     * Handles mouse input events and passes them to the active {@link InputListeningGameScreen}
     *
     * @param mouseButton the mouse button that was pressed or released.
     * @param modifiers   any modifier keys that were held during the event.
     * @param action      the type of action (press/release)
     */
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

        ilGameScreen.mouseClicked(mouseButton, modifiers, action, this.projectionMatrix, new Point2D(mouseX, mouseY));
    }

    /**
     * Handles keyboard input events and passes them to the active {@link InputListeningGameScreen}.
     * <p>
     * The {@link GameRenderer} overrides two function keys:
     * <ul>
     * <li>F11 to switch between windowed mode and full screen</li>
     * <li>F1  to display debug information</li>
     * </ul>
     * </p>
     *
     * @param keycode   the key that was pressed or released.
     * @param modifiers any modifier keys that were held during the event.
     * @param action    the type of action (press/release)
     * @return always {@code false}, a flag for the {@link zodalix.ro.engine.input.GameInputHandler} to not stop but pass the event to other listeners.
     */
    public boolean handleKeyboardInput(int keycode, int modifiers, int action) {
        if (action != GLFW_RELEASE && keycode == GLFW_KEY_F11) {
            this.switchScreenModes();
            return false;
        }

        this.showDebugInfo = action != GLFW_RELEASE && keycode == GLFW_KEY_F1;

        if (!(this.currentScreen instanceof InputListeningGameScreen ilGameScreen)) return false;
        ilGameScreen.keyboardInput(keycode, modifiers, action);

        return false;
    }

    private void switchScreenModes() {
        var vidmode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()), "Video mode is null (no monitor?)");

        if (this.isFullScreen) {
            // Go windowed
            var windowHandle = RoguesOdyssey.instance().windowHandle;
            final int width = 1280, height = 720;

            glfwSetWindowMonitor(
                    windowHandle, // Window handle
                    MemoryUtil.NULL,
                    0,
                    0,
                    width,
                    height,
                    GLFW_DONT_CARE
            );

            try (var stack = stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1);
                IntBuffer pHeight = stack.mallocInt(1);

                glfwGetWindowSize(windowHandle, pWidth, pHeight);

                glfwSetWindowPos(
                        windowHandle,
                        (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2
                );
            }
        } else {
            // Go fullscreen
            long monitor = glfwGetPrimaryMonitor();
            glfwSetWindowMonitor(
                    RoguesOdyssey.instance().windowHandle,
                    monitor,
                    0,
                    0,
                    vidmode.width(),
                    vidmode.height(),
                    vidmode.refreshRate()
            );
        }

        this.isFullScreen = !this.isFullScreen;
    }

    /**
     * Displays the FPS, frame-time, and render-time information on the screen.
     * This is part of the debug information displayed when debug mode is enabled.
     *
     * @param fps        the current frames per second.
     * @param frametime  the time taken to render the previous frame in milliseconds.
     * @param renderDiff the time taken to render the current frame in milliseconds.
     */
    public void displayFPS(long fps, long frametime, long renderDiff) {
        debugText.setText(TextComponent.composedText("<cyan shadow>{} FPS", fps));
        frameInfoText.setText(TextComponent.composedText("<white shadow>Frame-time: <cyan shadow>{}ms <white shadow>Rendering took: <pink bold>{}ms", frametime, renderDiff));

        {
            Runtime runtime = Runtime.getRuntime();

            long totalMemory = runtime.totalMemory();
            long usedMemory = totalMemory - runtime.freeMemory();

            try {
                var offHeapMemory = ((Long) java.lang.management.ManagementFactory.getPlatformMBeanServer().getAttribute(new javax.management.ObjectName("java.nio:type=BufferPool,name=direct"), "MemoryUsed")) / (1024 * 1024);
                ramText.setText(TextComponent.composedText("<shadow>On-heap: <orange shadow bold>{}MB<break>  <shadow>Off-heap: <orange shadow bold>{}MB", (usedMemory / 1024 / 1024), offHeapMemory));
            } catch (Throwable _) {
            }
        }
    }


    @Override
    public void tick(float deltaTime) {

        if (this.currentScreen == null) return;
        this.currentScreen.tick(deltaTime);

    }

    void cameraChanged(Camera camera) {
        if (!(this.currentScreen instanceof CameraAwareGameScreen camAware) || camAware.camera() != camera) return;

        this.recalculateMatrices(camera);
    }

    private void recalculateMatrices(@Nullable Camera camera) {
        float aspectRatio = lastKnownWindowWidth / (float) lastKnownWindowHeight;

        if (camera != null) {
            float scale = 10.0f / (float) Math.tan(Math.toRadians(camera.getFOV() / 2.0f));
            float left = -scale * aspectRatio;
            float right = scale * aspectRatio;
            float bottom = -scale;
            float top = scale;

            this.projectionMatrix.identity().setOrtho(left, right, bottom, top, -1.0f, 1.0f);
            this.viewMatrix.identity().translate(-camera.getPosition().x() * aspectRatio, -camera.getPosition().y(), 0.0f);

            this.renderMatrix.set(projectionMatrix).mul(viewMatrix);
        } else
            this.renderMatrix.setOrtho(-10f * aspectRatio, 10f * aspectRatio, -10f, 10f, -1, 1);

        this.projectionMatrix.setOrtho(-10f * aspectRatio, 10f * aspectRatio, -10f, 10f, -1, 1);    }

    public void tabulateRequest(int modifiers) {
        if(this.currentScreen instanceof TabScreen tabScreen)
            tabScreen.tabulated(modifiers);
    }
}
