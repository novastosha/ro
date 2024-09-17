attribute vec2 aPosition;
uniform mat4 uMVPMatrix;
attribute vec4 aColor;
varying vec4 vColor;

void main() {
    gl_Position = uMVPMatrix * vec4(aPosition, 0.0, 1.0);
    vColor = aColor;
}