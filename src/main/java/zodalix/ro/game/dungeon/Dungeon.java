package zodalix.ro.game.dungeon;

import java.util.Random;

public class Dungeon {

    public static final class Generator {
        private final Random random;

        private Generator(long seed) {
            this.random = new Random(seed);

        }
    }

    public Dungeon() {

    }

    public static Generator generator() {
        long time = System.currentTimeMillis();
        long sign = time % 2 == 0 ? 1L : -1L;

        return Dungeon.generator(sign * time);
    }

    public static Generator generator(long seed) {
        return new Generator(seed);
    }
}
