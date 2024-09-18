package zodalix.ro.game.entity;

import org.joml.Matrix4f;
import zodalix.ro.engine.utils.BoundingBox;
import zodalix.ro.engine.base.entity.DrawableEntity;
import zodalix.ro.engine.base.entity.InteractiveEntity;
import zodalix.ro.engine.utils.position.Position;

import java.util.UUID;

public class Tree implements DrawableEntity, InteractiveEntity {
    private final int logCount;
    private int animationFrame;

    private final Position position;

    public Tree(Position position,int logCount) {
        this.logCount = logCount;
        this.position = position;
    }

    @Override
    public void tick() {

    }

    @Override
    public void draw(Matrix4f projectionMatrix, float deltaTime) {

    }

    @Override
    public UUID uuid() {
        return null;
    }

    @Override
    public BoundingBox boundingBox() {
        return null;
    }

    @Override
    public Position position() {
        return position;
    }
}
