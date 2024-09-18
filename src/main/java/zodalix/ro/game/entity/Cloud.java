package zodalix.ro.game.base.objects;

import org.joml.Matrix4f;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.game.asset.GameTexture;
import zodalix.ro.game.utils.BoundingBox;
import zodalix.ro.game.base.entity.DrawableEntity;
import zodalix.ro.game.utils.NamespacedKey;
import zodalix.ro.game.renderer.DrawProperty;
import zodalix.ro.game.utils.position.Position;

import java.util.UUID;

public class Cloud implements DrawableEntity {
    private static final float CLOUD_SCALE = .01f;

    private final GameTexture texture;
    private final Position position;

    public Cloud(Position position,int textureVariant, boolean moodyCloud) {
        this.texture = RoguesOdyssey.instance().assetManager
                .getTexture(NamespacedKey.getDefault("textures/clouds"+(moodyCloud ? "/moody" : "")+"/Cloud_"+textureVariant));

        this.position = position;
    }

    @Override
    public void tick() {

    }

    @Override
    public void draw(Matrix4f projectionMatrix, float deltaTime) {
        final var am = RoguesOdyssey.instance().assetManager;

        this.texture.drawDefault(
                am,
                position.toPoint2D(),
                projectionMatrix,
                DrawProperty.scale(CLOUD_SCALE)
        );
    }

    @Override
    public UUID uuid() {
        return null;
    }

    @Override
    public BoundingBox boundingBox() {
        return BoundingBox.rectangle(
                (texture.getWidth() / 2f) * CLOUD_SCALE,
                (texture.getHeight() / 2f) * CLOUD_SCALE);
    }

    @Override
    public Position position() {
        return position;
    }
}