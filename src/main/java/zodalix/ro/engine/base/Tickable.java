package zodalix.ro.game.base;

public interface Tickable {

    /**
     * @implNote Subclasses implementing this interface should not perform any rendering operations in this method.
     */
    void tick();

}
