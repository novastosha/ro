package zodalix.ro.game.asset.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zodalix.ro.game.asset.AssetManager;

import java.io.InputStream;
import java.util.Objects;

public non-sealed class ClassResourceAssetProvider implements AssetProvider {
    private final AssetManager assetManager;

    public ClassResourceAssetProvider(@NotNull AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    @Nullable
    public InputStream provide(@NotNull String path) {
        Objects.requireNonNull(path, "path cannot be null.");
        return assetManager.getClass().getResourceAsStream(path);
    }
}
