package zodalix.ro.engine.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import zodalix.ro.engine.renderer.GameRenderer;
import org.joml.Matrix4f;

/**
 * A record representing an axis-aligned bounding box (AABB) in a 2D space, defined by its left, right, top,
 * and bottom boundaries. Primarily used for spatial queries such as point containment, overlap detection, and
 * visibility checks.
 * <p>
 * The coordinates define the boundaries relative to an origin (selfX, selfY), making the bounding box flexible for
 * dynamic placement. This class offers methods for scaling the bounding box and detecting intersections
 * with other {@link BoundingBox} instances.
 * </p>
 *
 * @see zodalix.ro.engine.renderer.GameRenderer
 */
public record BoundingBox(float leftX, float rightX, float topY, float bottomY) {

    /**
     * Creates a rectangular bounding box given the half-width (leftRight) and half-height (topBottom) values.
     *
     * @param leftRight  The half-width of the bounding box.
     * @param topBottom  The half-height of the bounding box.
     * @return A rectangular {@link BoundingBox}.
     */
    public static BoundingBox rectangle(float leftRight, float topBottom) {
        return new BoundingBox(-leftRight, leftRight, topBottom, -topBottom);
    }

    /**
     * Creates a square bounding box given its radius (half-width/height).
     *
     * @param radius The radius of the square (half the length of one side).
     * @return A square {@link BoundingBox}.
     */
    public static BoundingBox square(float radius) {
        return rectangle(radius, radius);
    }

    /**
     * Checks if a given point (pointX, pointY) is contained within this bounding box.
     *
     * @param selfX   The X coordinate of the bounding box's origin.
     * @param selfY   The Y coordinate of the bounding box's origin.
     * @param pointX  The X coordinate of the point to check.
     * @param pointY  The Y coordinate of the point to check.
     * @return {@code true} if the point is inside this bounding box; otherwise, {@code false}.
     */
    public boolean containsPoint(float selfX, float selfY, float pointX, float pointY) {
        float leftX = selfX + this.leftX();
        float bottomY = selfY + this.bottomY();
        float rightX = selfX + this.rightX();
        float topY = selfY + this.topY();

        if (!(leftX - .01f <= pointX && rightX + .01f >= pointX)) return false;
        return bottomY - .01f <= pointY && topY + .01f >= pointY;
    }


    /**
     * Determines if this {@link BoundingBox} intersects with another {@link BoundingBox} based on their origin points.
     * <p>
     * An intersection occurs if any part of this bounding box overlaps with the other bounding box. This method
     * considers both the positions and the dimensions of the two bounding boxes, checking if the edges overlap
     * on both the X and Y axes.
     * </p>
     *
     * @param selfX  X coordinate of the origin of this bounding box.
     * @param selfY  Y coordinate of the origin of this bounding box.
     * @param other  The other {@link BoundingBox} to check against.
     * @param otherX X coordinate of the origin of the other bounding box.
     * @param otherY Y coordinate of the origin of the other bounding box.
     * @return {@code true} if this bounding box intersects the other; otherwise, {@code false}.
     */
    public boolean intercepts(float selfX, float selfY, BoundingBox other, float otherX, float otherY) {
        float thisLeftX = selfX + this.leftX;
        float thisRightX = selfX + this.rightX;
        float thisTopY = selfY + this.topY;
        float thisBottomY = selfY + this.bottomY;

        float otherLeftX = otherX + other.leftX;
        float otherRightX = otherX + other.rightX;
        float otherTopY = otherY + other.topY;
        float otherBottomY = otherY + other.bottomY;

        boolean overlapX = thisRightX >= otherLeftX && otherRightX >= thisLeftX;
        boolean overlapY = thisTopY >= otherBottomY && otherTopY >= thisBottomY;
        return overlapX && overlapY;
    }

    /**
     * Multiplies all boundaries of this bounding box by a given floating factor.
     *
     * @param factor The factor by which to multiply all boundaries.
     * @return A new {@link BoundingBox} with scaled boundaries.
     */
    @NotNull
    @Contract("_ -> new")
    public BoundingBox mulAll(float factor) {
        return new BoundingBox(leftX * factor, rightX * factor, topY * factor, bottomY * factor);
    }

    @NotNull
    @Contract("_ -> new")
    public BoundingBox mulLR(float factor) {
        return new BoundingBox(leftX * factor, rightX * factor, topY, bottomY);
    }

    @NotNull
    @Contract("_ -> new")
    public BoundingBox mulTB(float factor) {
        return new BoundingBox(leftX, rightX, topY * factor, bottomY * factor);
    }

    /**
     * Checks if this bounding box is visible within the game window
     *
     * @param x                The X coordinate of the bounding box's origin.
     * @param y                The Y coordinate of the bounding box's origin.
     * @param projectionMatrix The projection to calculate visibility against. Usually the {@code projectionMatrix} or {@code combinedMatrix} derived from the {@link GameRenderer}
     * @return {@code true} if the bounding box is visible on the screen; otherwise, {@code false}.
     */
    public boolean isScreenVisible(float x, float y, @NotNull Matrix4f projectionMatrix) {
        Vector4f[] corners = {
                new Vector4f(x + this.leftX(), y + this.bottomY(), 0, 1), // Bottom-left
                new Vector4f(x + this.rightX(), y + this.bottomY(), 0, 1), // Bottom-right
                new Vector4f(x + this.leftX(), y + this.topY(), 0, 1), // Top-left
                new Vector4f(x + this.rightX(), y + this.topY(), 0, 1) // Top-right
        };

        boolean visible = false;

        for (Vector4f corner : corners) {
            projectionMatrix.transform(corner);
            if (corner.w != 0) {
                corner.x /= corner.w;
                corner.y /= corner.w;
                corner.z /= corner.w;
            }

            if (corner.x >= -1 && corner.x <= 1 && corner.y >= -1 && corner.y <= 1 && corner.z >= -1 && corner.z <= 1) {
                visible = true;
                break; // If any corner is visible, the bounding box is visible
            }
        }

        return visible;
    }
}