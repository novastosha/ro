attribute vec2 aPosition;
uniform mat4 uMVPMatrix;
attribute vec2 aTexCoord;
attribute vec4 aColor;
varying vec2 vTexCoord;
varying vec4 vColor;

uniform float uRotation; // Rotation angle in radians

void main() {
    // Apply rotation to the vertex position
    float cosTheta = cos(uRotation);
    float sinTheta = sin(uRotation);

    // Rotation matrix applied to vertex position
    mat2 rotationMatrix = mat2(
        cosTheta, -sinTheta,
        sinTheta, cosTheta
    );

    // Rotate the position
    vec2 rotatedPosition = rotationMatrix * aPosition;

    // Transform the rotated position with the MVP matrix
    gl_Position = uMVPMatrix * vec4(rotatedPosition, 0.0, 1.0);

    // Pass through the texture coordinates and color
    vTexCoord = aTexCoord;
    vColor = aColor;
}