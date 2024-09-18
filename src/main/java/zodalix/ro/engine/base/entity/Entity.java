package zodalix.ro.engine.base.entity;

import zodalix.ro.engine.base.Tickable;
import zodalix.ro.engine.utils.BoundingBox;
import zodalix.ro.engine.utils.position.Position;

import java.util.UUID;

public interface Entity extends Tickable {

    UUID uuid();

    BoundingBox boundingBox();

    Position position();

}
