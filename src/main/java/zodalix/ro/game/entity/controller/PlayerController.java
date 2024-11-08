package zodalix.ro.game.entity.controller;

import zodalix.ro.engine.base.entity.EntityController;
import zodalix.ro.engine.utils.position.MutablePosition;
import zodalix.ro.game.entity.Player;

import static zodalix.ro.engine.input.GameInputHandler.*;

public class PlayerController extends EntityController {
    private boolean jumpingHeld = false;

    public PlayerController(final Player player) {
        super(player);
    }

    public static final float BASE_HORIZONTAL_MOVEMENT_FACTOR = 10f,
                              BASE_VERTICAL_MOVEMENT_FACTOR = BASE_HORIZONTAL_MOVEMENT_FACTOR * 5;

    @Override
    public void checkInput(int releasedKey) {
        var player = (Player) this.getControlledEntity(); // Too lazy to give "it" its own field.

        if (MOVE_RIGHT.isDown()) {
            player.move(PlayerController.Movement.RIGHT, BASE_HORIZONTAL_MOVEMENT_FACTOR);
        }

        if (MOVE_LEFT.isDown()) {
            player.move(Movement.LEFT, BASE_HORIZONTAL_MOVEMENT_FACTOR);
        }

        if (JUMP.isDown() && !this.jumpingHeld) { // Check if on-ground too
            player.move(Movement.UP, BASE_VERTICAL_MOVEMENT_FACTOR);
            this.jumpingHeld = true;
        } else if (JUMP.wasDown(releasedKey)) this.jumpingHeld = false;
    }

    public enum Movement {
        LEFT {
            @Override
            public void mutate(MutablePosition position, float factor) {
                var orgX = position.x();
                position.setX(orgX - factor);
            }
        },
        RIGHT {
            @Override
            public void mutate(MutablePosition position, float factor) {
                var orgX = position.x();
                position.setX(orgX + factor);
            }
        },
        UP {
            @Override
            public void mutate(MutablePosition position, float factor) {
                var orgY = position.y();
                position.setY(orgY + factor);
            }
        },
        DOWN {
            @Override
            public void mutate(MutablePosition position, float factor) {

            }
        };

        public abstract void mutate(MutablePosition position, float factor);
    }
}
