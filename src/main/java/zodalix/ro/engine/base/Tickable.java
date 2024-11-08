package zodalix.ro.engine.base;

public interface Tickable {

    /**
     * {@inheritDoc}
     * @implNote Subclasses implementing this interface should not perform any rendering operations in this method.
     */
    void tick(float deltaTime);

}
