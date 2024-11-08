package zodalix.ro.game.entity;

import kotlin.Pair;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import zodalix.ro.engine.asset.GameTexture;
import zodalix.ro.engine.base.entity.ControllableEntity;
import zodalix.ro.engine.base.entity.DrawableEntity;
import zodalix.ro.engine.renderer.DrawProperty;
import zodalix.ro.engine.utils.BoundingBox;
import zodalix.ro.engine.utils.NamespacedKey;
import zodalix.ro.engine.utils.position.MutablePosition;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.game.entity.controller.PlayerController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Player implements ControllableEntity<PlayerController>, DrawableEntity {

    private final UUID uuid;
    private final BoundingBox bb;

    private final PlayerController controller;

    private final MutablePosition position;

    private final GameTexture placeholderTexture;
    private final float scale;

    private final List<Pair<PlayerController.Movement, Float>> movementQueue;

    {
        this.uuid = UUID.randomUUID();
        this.bb = BoundingBox.rectangle(1, 2.5F);

        this.controller = new PlayerController(this);
        this.position = new MutablePosition(0, 0);
        this.movementQueue = new ArrayList<>();

        this.placeholderTexture = RoguesOdyssey.instance().assetManager
                .getTexture(NamespacedKey.getDefault("textures/player/placeholder_player"));

        this.scale = 1 / 10f;
    }

    @Override
    public void tick(float deltaTime) {
        this.controller.checkInput(-1); // No released key was recorded.
        if(!movementQueue.isEmpty()) {
            var movement = movementQueue.removeFirst();

            var movementType = movement.getFirst();
            var factor = movement.getSecond() * deltaTime;

            movementType.mutate(this.position, factor);
        }
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
                DrawProperty.scale(this.scale)
        );
    }

    public void move(PlayerController.Movement movement, float factor) {
        this.movementQueue.add(new Pair<>(movement, factor));
    }
}
