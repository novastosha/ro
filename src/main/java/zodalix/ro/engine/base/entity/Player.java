package zodalix.ro.game.base.entity;

import zodalix.ro.game.utils.BoundingBox;
import zodalix.ro.game.utils.position.Position;

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
