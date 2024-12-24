package zodalix.ro.engine.renderer;

import zodalix.ro.engine.utils.position.Position;

public non-sealed class FreeCamera extends Camera {
    @Override
    public float getFOV() {
        return 0;
    }

    @Override
    public Position getPosition() {
        return null;
    }

    @Override
    public void tick(float deltaTime) {

    }
}
