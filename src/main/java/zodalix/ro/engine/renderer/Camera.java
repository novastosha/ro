package zodalix.ro.engine.renderer;

import zodalix.ro.engine.Tickable;
import zodalix.ro.engine.utils.position.Position;
import zodalix.ro.game.RoguesOdyssey;

public abstract sealed class Camera implements Tickable permits EntityAttachedCamera, FreeCamera {

    Camera() {

    }

    public abstract float getFOV();
    public abstract Position getPosition();

    protected final void notifyChanged() {
        RoguesOdyssey.instance().renderer.cameraChanged(this);
    }
}
