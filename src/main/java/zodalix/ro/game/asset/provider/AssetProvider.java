package zodalix.ro.game.asset.provider;

import java.io.InputStream;

public sealed interface AssetProvider permits
        ClassResourceAssetProvider,
        DirectoryAssetProvider
{


    IllegalStateException RESOURCE_NOT_FOUND = new IllegalStateException("Couldn't find resource.");

    InputStream provide(String path);
}
