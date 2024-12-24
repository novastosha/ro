package zodalix.ro.game.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import zodalix.ro.engine.asset.GameTexture;
import zodalix.ro.engine.base.entity.ControllableEntity;
import zodalix.ro.engine.base.entity.DrawableEntity;
import zodalix.ro.engine.base.entity.Entity;
import zodalix.ro.engine.renderer.DrawProperty;
import zodalix.ro.engine.utils.BoundingBox;
import zodalix.ro.engine.utils.NamespacedKey;
import zodalix.ro.engine.utils.Subscription;
import zodalix.ro.engine.utils.position.MutablePosition;
import zodalix.ro.engine.utils.position.Position;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.game.entity.controller.PlayerController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Player implements ControllableEntity<PlayerController>, DrawableEntity, Entity.MovementSubscribable{

    private final UUID uuid;
    private final BoundingBox bb;

    private final PlayerController controller;

    private final MutablePosition position;

    private final GameTexture placeholderTexture;
    private final float scale;

    private @NotNull List<Subscription<Position>> movementSubscribers;

    {
        this.uuid = UUID.randomUUID();
        this.bb = BoundingBox.rectangle(1, 2.5F);

        this.controller = new PlayerController(this);
        this.position = new MutablePosition(0, 0);

        this.placeholderTexture = RoguesOdyssey.instance().assetManager
                .getTexture(NamespacedKey.getDefault("textures/player/placeholder_player"));

        this.scale = 1 / 10f;

        this.movementSubscribers = new ArrayList<>();
    }

    @Override
    public void tick(float deltaTime) {
        this.controller.checkInput(-1, deltaTime); // No released key was recorded.
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
    public MutablePosition position() {
        return position;
    }

    @Override
    public boolean isControlled() {
        return this.controller != null;
    }

    @Override
    public @Nullable PlayerController controller() {
        return this.controller;
    }

    @Override
    public void draw(Matrix4f projectionMatrix, float deltaTime) {
        this.placeholderTexture.drawDefault(
                RoguesOdyssey.instance().assetManager,
                this.position.toPoint2D(),
                projectionMatrix,
                this.boundingBox(),
                DrawProperty.scale(this.scale)
        );
    }

    public void move(PlayerController.Movement movement, float factor) {
        movement.mutate(this.position, factor);
        for (var subscriber : this.movementSubscribers) {
            subscriber.update(this.position);
        }
    }


    @Override
    public @NotNull List<Subscription<Position>> movementSubscribers() {
        return this.movementSubscribers;
    }

    @Override
    public @Nullable Subscription<Position> movementSubscribe(Consumer<Position> updateFunction, Runnable cancelledCallback) {
        var sub = new Subscription<>(
                updateFunction,
                cancelledCallback,
                (instance) -> this.movementSubscribers.remove(instance)
        );

        this.movementSubscribers.add(sub);
        return sub;
    }

    @Override
    public void cancelMovementSubscription(Subscription<Position> tSubscription, boolean callback) {
        this.movementSubscribers.remove(tSubscription);
        if(callback) tSubscription.getCancelledCallback().run();
    }

}
