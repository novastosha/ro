package zodalix.ro.game.dungeon;

import org.jetbrains.annotations.NotNull;
import zodalix.ro.engine.base.Tickable;
import zodalix.ro.game.entity.Player;

public class Dungeon implements Tickable {

    private final Player player;

    Dungeon() {
        this.player = new Player();
    }


    public static DungeonGenerator generator() {
        long time = System.currentTimeMillis();
        long sign = time % 2 == 0 ? 1L : -1L;

        return Dungeon.generator(sign * time);
    }

    public static DungeonGenerator generator(long seed) {
        return new DungeonGenerator(seed);
    }

    public Player getPlayer() {
        return this.player;
    }

    public static @NotNull Dungeon dummyDungeon() {
        return new Dungeon();
    }

    @Override
    public void tick(float deltaTime) {
        this.player.tick(deltaTime);
    }
}
