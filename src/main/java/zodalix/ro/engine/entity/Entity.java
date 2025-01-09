package zodalix.ro.engine.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zodalix.ro.engine.Tickable;
import zodalix.ro.engine.utils.BoundingBox;
import zodalix.ro.engine.utils.Subscription;
import zodalix.ro.engine.utils.position.Position;


import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface Entity extends Tickable {

    interface MovementSubscribable {
        @NotNull List<Subscription<Position>> movementSubscribers();
        @Nullable Subscription<Position> movementSubscribe(Consumer<Position> updateFunction, Runnable cancelledCallback);

        void cancelMovementSubscription(Subscription<Position> tSubscription, boolean callback);
    }

    UUID uuid();

    BoundingBox boundingBox();

    Position position();

}
