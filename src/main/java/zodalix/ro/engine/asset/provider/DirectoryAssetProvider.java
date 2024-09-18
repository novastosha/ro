package zodalix.ro.engine.asset.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public non-sealed class DirectoryAssetProvider implements AssetProvider {
    @Override
    @Nullable
    public InputStream provide(@NotNull String path) {
        Objects.requireNonNull(path, "path cannot be null.");
        try { return new FileInputStream(path); }
        catch (FileNotFoundException _) {
            return FileInputStream.nullInputStream();
        }
    }
}
