package zodalix.ro.engine.utils;

import java.util.function.Consumer;

public class Subscription<T> {

    private final Consumer<T> updateFunction;
    private final Runnable cancelledCallback;
    private final Consumer<Subscription<T>> parentCancelledCallback;

    public Runnable getCancelledCallback() {
        return cancelledCallback;
    }

    public Subscription(Consumer<T> updateFunction , Runnable cancelledCallback , Consumer<Subscription<T>> parentCancelledCallback) {
        this.updateFunction = updateFunction;
        this.cancelledCallback = cancelledCallback;
        this.parentCancelledCallback = parentCancelledCallback;
    }

    public void cancel() {
        parentCancelledCallback.accept(this);
    }

    public void update(T val) {
        this.updateFunction.accept(val);
    }
}
