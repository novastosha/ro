package zodalix.ro.game.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zodalix.ro.game.RoguesOdyssey;

public final class GameBootstrap {
    private static final Logger log = LogManager.getLogger(GameBootstrap.class);

    public static void main(String[] args) {
        // Parse arguments if needed.

        {
            long startTime = System.currentTimeMillis();
            RoguesOdyssey.init();
            log.info("Game initialized in {}ms",System.currentTimeMillis()-startTime);
        }
        RoguesOdyssey.instance().startGame();
    }
}
