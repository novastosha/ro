package zodalix.ro.game.base.entity;

import zodalix.ro.game.base.Tickable;
import zodalix.ro.game.utils.BoundingBox;
import zodalix.ro.game.utils.position.Position;

import java.util.UUID;

public interface Entity extends Tickable {

    UUID uuid();

    BoundingBox boundingBox();

    Position position();

}
