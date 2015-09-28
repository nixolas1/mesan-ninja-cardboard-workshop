precision mediump float;
varying vec2 v_TexCoordinate; // Interpolated texture coordinate per fragment
uniform sampler2D u_Texture; // The input texture.

void main() {
    gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
}