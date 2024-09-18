package zodalix.ro.game.utils.position;

public non-sealed class ImmutablePosition implements Position {
    private final float x,y;

    public ImmutablePosition(float x, float y) {
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
}
