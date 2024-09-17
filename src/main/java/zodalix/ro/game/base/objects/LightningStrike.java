package zodalix.ro.game.base.objects;

import kotlin.Pair;
import org.jetbrains.annotations.Contract;
import org.joml.Matrix4f;

import zodalix.ro.game.RoguesOdyssey;
import zodalix.ro.game.asset.AnimatedGameTexture;
import zodalix.ro.game.utils.position.Point2D;
import zodalix.ro.game.utils.BoundingBox;
import zodalix.ro.game.base.entity.DrawableEntity;
import zodalix.ro.game.utils.NamespacedKey;
import zodalix.ro.game.renderer.DrawProperty;
import zodalix.ro.game.utils.position.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class LightningStrike implements DrawableEntity {
    private final int nBranches, nTotalSteps, nRotationRandomness;
    private final AnimatedGameTexture.DrawInstance drawInstance;
    private final Position position;
    private final Random random;
    private final List<Pair<Point2D, Float>> drawPositions;

    public LightningStrike(Position position, Random random, int maxValue) {
        this(position, random, random.nextInt(1, maxValue), random.nextInt(1, maxValue), random.nextInt(2, maxValue));
    }

    public LightningStrike(Position position, Random random, int nBranches, int nTotalSteps, int nRotationRandomness) {
        this.nBranches = nBranches;
        this.nTotalSteps = nTotalSteps;
        this.nRotationRandomness = nRotationRandomness;

        this.position = position;
        this.random = random;

        this.drawPositions = new ArrayList<>();

        this.drawInstance = RoguesOdyssey.instance().assetManager.getAnimatedTexture(
                NamespacedKey.getDefault("textures/clouds/bolt"))
                .getDrawInstance();


    }

    @Override
    public void tick() {
    }

    @Override
    public void draw(Matrix4f projectionMatrix, float deltaTime) {
        this.drawInstance.tick(deltaTime);
        drawPositions.forEach(drawCall -> drawInstance.draw(
                RoguesOdyssey.instance().assetManager,
                new Point2D(drawCall.getFirst().x + position.x(), drawCall.getFirst().y + position.y() - 1.5f),
                projectionMatrix,
                DrawProperty.rotate(drawCall.getSecond()),
                DrawProperty.scale(.05f)
        ));
    }

    @Override
    @Contract("-> null")
    public UUID uuid() {
        return null;
    }

    @Override
    @Contract("-> null")
    public BoundingBox boundingBox() {
        return null;
    }

    @Override
    public Position position() {
        return position;
    }
}
