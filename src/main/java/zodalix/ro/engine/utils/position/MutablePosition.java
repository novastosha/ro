package zodalix.ro.engine.utils.position;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position that)) return false;
        return Float.compare(x, that.x()) == 0 && Float.compare(y, that.y()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
