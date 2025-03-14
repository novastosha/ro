package zodalix.ro.engine.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import zodalix.ro.engine.utils.position.Point2D;
import zodalix.ro.engine.renderer.GameRenderer;

/**
 * A utility static class that provides functions to perform various operations related to vectors, matrix transformation.
 *
 * @see RenderingUtils#transformPoint(float, GameRenderer)
 * @see RenderingUtils#transformCoordinates(Vector2f, GameRenderer)
 */
public final class RenderingUtils {

    private RenderingUtils() { throw new IllegalStateException(RenderingUtils.class.getCanonicalName()+" cannot be instanced!"); }

    public static float normalizeRGBA(int val) {
        return val / 255.0f;
    }

    @NotNull
    @Contract("_, _ -> new")
    public static Vector2f transformCoordinates(Vector2f original, GameRenderer renderer) {
        var ratio = renderer.getLastKnownWindowWidth() / (float) renderer.getLastKnownWindowHeight();
        return new Vector2f(original.x * ratio,original.y);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static Point2D transformCoordinates(Point2D position, GameRenderer renderer) {
        var ratio = renderer.getLastKnownWindowWidth() / (float) renderer.getLastKnownWindowHeight();
        return new Point2D(position.x * ratio, position.y);
    }

    public static float transformPoint(float x, GameRenderer renderer) {
        var ratio = renderer.getLastKnownWindowWidth() / (float) renderer.getLastKnownWindowHeight();
        return x * ratio;
    }
}
