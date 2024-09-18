package zodalix.ro.engine.utils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class NamespacedKey implements CharSequence {

    private static final Map<String, NamespacedKey> REGISTERED_KEYS = new HashMap<>();

    private final String namespace, path;

    private NamespacedKey(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @NotNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @NotNull
    @Override
    public String toString() {
        return namespace + ":" + path;
    }

    @NotNull
    public static NamespacedKey get(String namespace, String path) {
        var fullPath = namespace + ":" + path;

        if (!REGISTERED_KEYS.containsKey(fullPath)) {
            NamespacedKey  asset = new NamespacedKey(namespace, path);
            REGISTERED_KEYS.put(fullPath,asset);
            return asset;
        }

        return REGISTERED_KEYS.get(fullPath);
    }

    @NotNull
    public static NamespacedKey getDefault(String path) {
        return get("ro", path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamespacedKey that = (NamespacedKey) o;

        if (!namespace.equals(that.namespace)) return false;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        int result = namespace.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}
