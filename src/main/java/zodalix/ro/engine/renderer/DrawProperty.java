package zodalix.ro.engine.renderer;

import zodalix.ro.engine.asset.GameTexture;

import java.awt.Color;

/**
 * Describes how to draw a {@link GameTexture}
 * <p>
 * Each property is an argument passed into a monolithic draw call in {@link GameTexture}
 *
 * @see ColorTransform
 * @see Rotation
 * @see Scale
 * @see Dimensions
 */
public sealed interface DrawProperty permits DrawProperty.ColorTransform, DrawProperty.Dimensions, DrawProperty.Rotation, DrawProperty.Scale {
    /**
     * Describes what color should be applied to the texture in the GPU shader call.
     * @param color the color to apply
     */
    record ColorTransform(Color color) implements DrawProperty { }

    /**
     * When passed into a draw call, the angle is converted into radians and passed into the shader to process. The texture is then rendered at the desired rotation.
     * @param degrees the angle to rotate with (in degrees)
     */
    record Rotation(float degrees) implements DrawProperty { }

    /**
     * Describes how large the drawn texture should be. When passed into a draw call, the vertices are multiplied by the passed factor.
     * @param value how large the texture should be. {@code value > 0]}
     */
    record Scale(float value) implements DrawProperty { }

    /**
     * When passed into a draw call, it forces the passed on width and height to the texture thus making it either stretch or shrink to fit the dimensions.
     *
     * @param width the needed width.
     * @param height the needed height.
     */
    record Dimensions(float width, float height) implements DrawProperty {}

    static DrawProperty rotate(float angleDegrees) {
        return new Rotation(angleDegrees);
    }

    static DrawProperty color(Color color) {
        return new ColorTransform(color);
    }

    static DrawProperty scale(float val) {
        return new Scale(val);
    }

    static DrawProperty dimensions(float width, float height) {
        return new Dimensions(width, height);
    }

    static DrawProperty dimensions(GameTexture texture) {
        return new Dimensions(texture.getWidth(), texture.getHeight());
    }

    static DrawProperty[] of(DrawProperty... properties) {
        return properties;
    }
}
