package zodalix.ro.engine.utils.position;

public sealed interface Position permits MutablePosition, ImmutablePosition {

    Position ZERO = new ImmutablePosition(0,0);

    float x();
    float y();

    default Point2D toPoint2D() {
        return new Point2D(x(),y());
    }

    /**
     * @throws ClassCastException if this isn't a mutable position.
     * @return a mutable position cast.
     */
    default MutablePosition asMutable() {
        return (MutablePosition) this;
    }
}
