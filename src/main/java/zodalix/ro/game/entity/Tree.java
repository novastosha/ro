package zodalix.ro.game.base.objects;

import org.joml.Matrix4f;
import zodalix.ro.game.utils.BoundingBox;
import zodalix.ro.game.base.entity.DrawableEntity;
import zodalix.ro.game.base.entity.InteractiveEntity;
import zodalix.ro.game.utils.position.Position;

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
