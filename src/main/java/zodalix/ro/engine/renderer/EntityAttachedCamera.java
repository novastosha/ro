package zodalix.ro.engine.renderer;

import org.jetbrains.annotations.Nullable;
import zodalix.ro.engine.base.entity.Entity;
import zodalix.ro.engine.utils.Subscription;
import zodalix.ro.engine.utils.position.MutablePosition;
import zodalix.ro.engine.utils.position.Position;

public non-sealed class EntityAttachedCamera extends Camera {

    private final Entity attachedEntity;
    private final MutablePosition lastKnownPosition;

    private final @Nullable Subscription<Position> subscription;

    public <E extends Entity & Entity.MovementSubscribable> EntityAttachedCamera(E entity) { // Perhaps use an entity reference system instead? What if the entity dies? o_O
        this.attachedEntity = entity;
        this.lastKnownPosition = new MutablePosition(
          this.attachedEntity.position().x(),
          this.attachedEntity.position().y()
        );

        this.subscription = entity.movementSubscribe(this::positionUpdated, () -> {});
    }

    private void positionUpdated(Position position) {
        this.lastKnownPosition.setTo(this.attachedEntity.position()); // Would it really make a performance difference if we added a notification based system into Entity?
        this.notifyChanged();
    }


    @Override
    public float getFOV() {
        return 90f;
    }

    @Override
    public Position getPosition() {
        return this.lastKnownPosition;
    }

    @Override
    public void tick(float deltaTime) {

    }
}
