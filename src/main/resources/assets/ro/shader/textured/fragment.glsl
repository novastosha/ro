precision mediump float;
uniform sampler2D uTexture;
varying vec2 vTexCoord;
varying vec4 vColor;

void main() {
    vec4 texColor = texture2D(uTexture, vTexCoord);
    gl_FragColor = texColor * vColor;
}