package zodalix.ro.engine.asset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zodalix.ro.engine.utils.NamespacedKey;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.lwjgl.opengl.GL33.*;

public class GameShader {

    private static final Logger logger = LogManager.getLogger("Shaders");

    private final NamespacedKey key;

    public transient final int fragmentShaderId, vertexShaderId;
    public transient final int glShaderProgram;

    GameShader(NamespacedKey key, final AssetManager am) {
        this.key = key;

        var fragmentResource = am.provideAsset("/assets/" + key.toString().replaceAll(":", "/") + "/fragment.glsl");
        Objects.requireNonNull(fragmentResource);

        var vertexResource = am.provideAsset("/assets/" + key.toString().replaceAll(":", "/") + "/vertex.glsl");
        Objects.requireNonNull(vertexResource);

        try (fragmentResource; vertexResource) {
            var decompiledFragmentCode = new String(fragmentResource.readAllBytes(), StandardCharsets.UTF_8);
            var decompiledVertexCode = new String(vertexResource.readAllBytes(), StandardCharsets.UTF_8);

            this.vertexShaderId = createGLShader(GL_VERTEX_SHADER, decompiledVertexCode);
            this.fragmentShaderId = createGLShader(GL_FRAGMENT_SHADER, decompiledFragmentCode);
        } catch (IOException e) {
            logger.fatal("An unexpected I/O exception was thrown whilst loading shaders: {}", key);
            throw new RuntimeException(e);
        }

        this.glShaderProgram = newProgram();
    }

    private int createGLShader(int type, String decompiledCode) {
        int shader = glCreateShader(type);
        glShaderSource(shader, decompiledCode);
        glCompileShader(shader);
        return shader;
    }

    private int newProgram() {
        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShaderId);
        glAttachShader(shaderProgram, fragmentShaderId);

        glLinkProgram(shaderProgram);
        return shaderProgram;
    }

    public NamespacedKey getKey() {
        return key;
    }
}
