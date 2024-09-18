package zodalix.ro.engine.base.entity;

import zodalix.ro.engine.utils.BoundingBox;
import zodalix.ro.engine.utils.position.Position;

import java.util.UUID;

public class Player implements Entity {

    private final UUID uuid;
    private final BoundingBox bb;

    {
        this.uuid = UUID.randomUUID();
        this.bb = BoundingBox.rectangle(1,2.5F);
    }

    @Override
    public void tick() {

    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public BoundingBox boundingBox() {
        return bb;
    }

    @Override
    public Position position() {
        return null;
    }
}
