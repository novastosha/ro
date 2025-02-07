package zodalix.ro.engine.entity;

import org.jetbrains.annotations.Nullable;

public interface ControllableEntity<C extends EntityController> extends Entity {

    boolean isControlled();
    @Nullable C controller();


}
