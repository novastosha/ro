package zodalix.ro.engine.base.entity;

public class EntityController {
    private final Entity entity;

    public EntityController(Entity entity) {
        this.entity = entity;
    }

    public Entity getControlledEntity() {
        return this.entity;
    }

    public void checkInput(int releasedKey, float deltaTime) {
    }
}
