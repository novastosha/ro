package zodalix.ro.engine.utils.position;

import org.jetbrains.annotations.Contract;

public non-sealed class MutablePosition implements Position {
    private float x,y;

    public MutablePosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Contract(mutates = "this")
    public void setTo(Point2D point) {
        this.x = point.x;
        this.y = point.y;
    }

    @Contract(mutates = "this")
    public void setTo(Position other) {
        this.x = other.x();
        this.y = other.y();
    }
}
