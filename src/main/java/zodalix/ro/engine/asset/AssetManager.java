package zodalix.ro.game.asset;

import zodalix.ro.game.RoguesOdyssey;

import zodalix.ro.game.asset.provider.AssetProvider;
import zodalix.ro.game.asset.provider.ClassResourceAssetProvider;
import zodalix.ro.game.utils.NamespacedKey;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class AssetManager {
    private final Map<NamespacedKey, GameTexture> loadedTextures;
    private final Map<NamespacedKey, GameShader> loadedShaders;
    private final AssetProvider assetProvider;

    public AssetManager(RoguesOdyssey instance) {
        {
            this.loadedTextures = new HashMap<>();
            this.loadedShaders  = new HashMap<>();
        }
        this.assetProvider = new ClassResourceAssetProvider(this);
    }

    public GameTexture getTexture(NamespacedKey key) {
        if (!loadedTextures.containsKey(key)) {
            GameTexture asset;
            this.loadedTextures.put(key, asset = new GameTexture(key));
            return asset;
        }

        return this.loadedTextures.get(key);
    }

    public AnimatedGameTexture getAnimatedTexture(NamespacedKey key) {
        if (!loadedTextures.containsKey(key)) {
            AnimatedGameTexture asset;
            this.loadedTextures.put(key, asset = new AnimatedGameTexture(key));
            return asset;
        }

        return (AnimatedGameTexture) this.loadedTextures.get(key);
    }
    
    public GameShader getShader(NamespacedKey key) {
        if (!loadedShaders.containsKey(key)) {
            GameShader asset;
            this.loadedShaders.put(key, asset = new GameShader(key));
            return asset;
        }

        return this.loadedShaders.get(key);
    }

    InputStream provideAsset(String path) {
        return assetProvider.provide(path);
    }
}
