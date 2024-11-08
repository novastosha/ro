package zodalix.ro.game.gui;


import org.joml.Matrix4f;
import zodalix.ro.engine.asset.GameTexture;
import zodalix.ro.engine.renderer.DrawProperty;
import zodalix.ro.engine.screen.GameScreen;
import zodalix.ro.engine.utils.NamespacedKey;
import zodalix.ro.engine.utils.RenderingUtils;
import zodalix.ro.engine.utils.position.MutablePosition;
import zodalix.ro.engine.utils.position.Point2D;
import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.game.entity.Cloud;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.opengl.GL11.*;

final class BackgroundScreen implements GameScreen {

    private final Map<Cloud, MutablePosition> clouds;
    private final GameTexture backgroundImageTexture;

    private final Random random;


    {
        this.backgroundImageTexture = RoguesOdyssey.instance().assetManager.getTexture(
                NamespacedKey.getDefault("textures/backgrounds/title")
        );

        this.random = ThreadLocalRandom.current();
        float x = RenderingUtils.transformPoint(-9.5f, RoguesOdyssey.instance().renderer);

        this.clouds = new HashMap<>();

        int cloudSelection = 1;
        for (int i = 0; i < 20; i++) {
            var position = new MutablePosition(x += 1f + (random.nextBoolean() ? +random.nextFloat() : -random.nextFloat()), 7.5f + (random.nextBoolean() ? -1.25f : +1.75f));
            this.clouds.put(new Cloud(position, cloudSelection, true), position);

            cloudSelection++;
            if (cloudSelection > 10) cloudSelection = 0;
        }
    }

    @Override
    public void draw(Matrix4f projectionMatrix, float deltaTime) {
        this.backgroundImageTexture.drawDefault(
                RoguesOdyssey.instance().assetManager,
                Point2D.ZERO,
                projectionMatrix,
                DrawProperty.stretch()
        );

        tickClouds(deltaTime);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        clouds.keySet().forEach(cloud -> cloud.draw(projectionMatrix, deltaTime));
        glDisable(GL_BLEND);
    }

    private void tickClouds(float deltaTime) {
        final float x = RenderingUtils.transformPoint(-9.5f, RoguesOdyssey.instance().renderer);

        int index = 0;
        for (var entry : clouds.entrySet()) {
            var cloud = entry.getKey();
            var pos = entry.getValue();

            boolean isFast = index % 2 != 0;

            pos.setX(pos.x() + (0.25f * deltaTime * (isFast ? 1.75f : 1)));
            var renderer = RoguesOdyssey.instance().renderer;

            if (!cloud.boundingBox().isScreenVisible(pos.x(), pos.y(), renderer)) {
                pos.setX(x + (.5f + (random.nextBoolean() ? +random.nextFloat() : -random.nextFloat())));
                pos.setY(7.5f + (random.nextBoolean() ? -.75f : +1f));
            }
            index++;
        }
    }
}
