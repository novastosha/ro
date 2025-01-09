package zodalix.ro.engine.entity;

import org.joml.Matrix4f;

public interface DrawableEntity extends Entity {

    void draw(Matrix4f projectionMatrix, float deltaTime);

}
