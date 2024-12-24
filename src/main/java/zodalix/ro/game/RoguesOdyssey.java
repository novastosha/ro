package zodalix.ro.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import zodalix.ro.engine.asset.AssetManager;
import zodalix.ro.engine.input.GameInputHandler;
import zodalix.ro.engine.renderer.GameRenderer;
import zodalix.ro.game.dungeon.Dungeon;
import zodalix.ro.game.gui.TitleScreen;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * The main entry point for the "Rogue's Odyssey" game. This class manages the game's lifecycle, including
 * initializing game resources, setting up window and input handlers, rendering, and starting the main game loop.
 * It uses {@link GLFW} for window management and input handling, and OpenGL for rendering.
 * <p>
 * This is a singleton class, ensuring that only one instance of the game exists at any given time.
 *
 * @see GameRenderer
 * @see AssetManager
 * @see GameInputHandler
 * @see <a href="https://www.glfw.org/">GLFW Official Documentation</a>
 */
public final class RoguesOdyssey {

    private static RoguesOdyssey instance;

    public final long windowHandle;
    public final AssetManager assetManager;
    public final GameRenderer renderer;
    public final GameInputHandler inputHandler;

    private volatile Dungeon dungeon;

    /**
     * Private constructor to initialize the game instance. This method sets up the GLFW window, the OpenGL context,
     * and the main components such as the {@link GameRenderer}, {@link AssetManager}, and {@link GameInputHandler}.
     */
    private RoguesOdyssey() {

        this.assetManager = new AssetManager(this);
        this.inputHandler = new GameInputHandler(this);

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        this.windowHandle = glfwCreateWindow(800, 600, this.getWindowTitle(), NULL, NULL);
        if (windowHandle == NULL)
            throw new RuntimeException("Couldn't create a GLFW window");

        try (var stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowHandle, pWidth, pHeight);

            var vidmode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()), "Video mode is null (no monitor?)");
            glfwSetWindowPos(
                    windowHandle,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(windowHandle); // Attaches the calling thread to the GLFW context.

        this.renderer = new GameRenderer(this);
    }

    private String getWindowTitle() {
        return "Rogue's Odyssey";
    }

    /**
     * @return an instance to the game object, this method can never return null.
     * @throws IllegalStateException If this method is called before the game is initialized.
     */
    @NotNull
    public static RoguesOdyssey instance() {
        if (instance == null) throw new IllegalStateException("Game not initialized yet! (How did we get here?)");
        return instance;
    }


    /**
     * Creates a new instance of {@link RoguesOdyssey} and assigns it.
     * <p>
     * Should only be invoked once by the {@link zodalix.ro.game.bootstrap.GameBootstrap}
     *
     * @throws IllegalStateException if the method was called after already initializing
     * @see RoguesOdyssey#instance()
     */
    public static void init() {
        if (RoguesOdyssey.instance != null) throw new IllegalStateException("Game has already initialized");
        RoguesOdyssey.instance = new RoguesOdyssey();
    }

    /**
     * Starts the game by creating OpenGL capabilities, setting up the main game loop, and configuring GLFW callbacks.
     * This method continuously updates the game state, renders the game, and handles input.
     */
    public void startGame() {
        GL.createCapabilities();

        glfwSwapInterval(0);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glfwShowWindow(windowHandle);

        this.renderer.postInit();
        this.renderer.windowResized(800, 600);

        {
            this.inputHandler.registerKeybinding(
                    this.inputHandler.defaultKeymap(),
                    GameInputHandler.MOVE_LEFT,
                    GameInputHandler.MOVE_RIGHT,
                    GameInputHandler.JUMP,
                    GameInputHandler.ESCAPE
            );
        }

        // Set the game callbacks.
        {
            glfwSetWindowSizeCallback(windowHandle, (_, argWidth, argHeight) -> RoguesOdyssey.this.renderer.windowResized(argWidth, argHeight));
            glfwSetMouseButtonCallback(windowHandle, (_, button, action, mods) -> RoguesOdyssey.this.inputHandler.mouseInputReceived(button, mods, action));
            glfwSetKeyCallback(windowHandle, (_, key, _, action, mods) -> RoguesOdyssey.this.inputHandler.keyboardInputReceived(key, mods, action));
        }

        renderer.setCurrentScreen(new TitleScreen());

        int frames = 0;
        long fpsLastTime = System.currentTimeMillis();

        long previousTime = System.nanoTime();

        while (!glfwWindowShouldClose(windowHandle)) {
            long timeStart = System.currentTimeMillis();

            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - previousTime) / 1_000_000_000.0f; // Convert to seconds
            previousTime = currentTime;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            this.inputHandler.checkForClickHold();


            this.renderer.tick(deltaTime);
            if (this.dungeon != null) this.dungeon.tick(deltaTime);

            long renderDiff;
            {
                long renderStart = System.currentTimeMillis();

                this.renderer.render(deltaTime); // Pass deltaTime to the render method
                renderDiff = System.currentTimeMillis() - renderStart;
            }

            glfwSwapBuffers(windowHandle);
            glfwPollEvents();

            frames++;

            long timeEnd = System.currentTimeMillis();
            long difference = timeEnd - timeStart;

            if (timeEnd - fpsLastTime >= 1000) {
                this.renderer.displayFPS(frames, difference, renderDiff);
                frames = 0;
                fpsLastTime += 1000;
            }
        }
    }

    @Nullable
    public Dungeon getCurrentDungeon() {
        return this.dungeon;
    }

    /**
     * @param newDungeon the new dungeon to set.
     * @return {@code this.dungeon}
     * @implNote The rest of the implementation is left to the caller, such as switching screens, etc...
     */
    public Dungeon setDungeon(@Nullable Dungeon newDungeon) {
        return this.dungeon = newDungeon;
    }
}
