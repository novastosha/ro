package zodalix.ro.engine.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import zodalix.ro.engine.renderer.GameRenderer;

public record BoundingBox(float leftX, float rightX, float topY, float bottomY) {

    public static BoundingBox rectangle(float leftRight, float topBottom) {
        return new BoundingBox(-leftRight, leftRight, topBottom, -topBottom);
    }

    public static BoundingBox square(float radius) {
        return rectangle(radius,radius);
    }

    public boolean containsPoint(float x, float y, float mouseX, float mouseY) {
        float leftX = x + this.leftX();
        float bottomY = y + this.bottomY();
        float rightX = x + this.rightX();
        float topY = y + this.topY();

        if (!(leftX - .01f <= mouseX && rightX + .01f >= mouseX)) return false;
        return bottomY - .01f <= mouseY && topY + .01f >= mouseY;
    }

    //public boolean intercepts(float selfX, float selfY, BoundingBox other, float otherX, float otherY) {
    //    return false;
    //}

    /**
     * Multiplies all boundaries of this bounding box by a given floating factor.
     *
     * @param factor the factor to multiply by.
     * @return the multiplied bb.
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

    public boolean isScreenVisible(float x, float y, GameRenderer renderer) {
        var ratio = renderer.getLastKnownWindowWidth() / (float) renderer.getLastKnownWindowHeight();
        if(Float.isNaN(ratio) || ratio == 0) return true; // This occurs when the screen is minimized.

        var maxX = 9f * ratio;
        var maxY = 9f;

        float leftX = Math.abs(x + this.leftX());
        float rightX = Math.abs(x + this.rightX());

        boolean visibleOnX = leftX < maxX || rightX < maxX;

        float topY = Math.abs(y + this.topY());
        float bottomY = Math.abs(y + this.bottomY());

        boolean visibleOnY = topY < maxY || bottomY < maxY;

        return visibleOnX && visibleOnY;
    }
}