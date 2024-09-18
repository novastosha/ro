package zodalix.ro.engine.utils.position;

public class Point2D {

    public static final Point2D ZERO = new Point2D(0,0);

    private final boolean isAbsolute;
    public final float x, y;

    public Point2D(boolean isAbsolute, float x, float y) {
        this.isAbsolute = isAbsolute;
        this.x = x;
        this.y = y;
    }

    public Point2D(float x, float y) {
        this(false, x, y);
    }

    public Point2D sub(float subX, float subY) {
        return new Point2D(x - subX, y - subY);
    }

    public float toGLX() {
        return isAbsolute ? -1f : x;
    }

    public float toGLY() {
        return isAbsolute ? 1f : y;
    }

}
