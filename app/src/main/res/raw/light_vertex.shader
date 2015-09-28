uniform mat4 u_Model;
uniform mat4 u_MVP;

attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec2 a_TexCoordinate; // Per-vertex texture coordinate information we will pass in.

varying vec4 v_Color;
varying vec3 v_Grid;
varying vec2 v_TexCoordinate;   // This will be passed into the fragment shader.

void main() {
   v_Grid = vec3(u_Model * a_Position);

   // Pass through the texture coordinate.
   v_TexCoordinate = a_TexCoordinate;

   v_Color = a_Color;
   gl_Position = u_MVP * a_Position;
}
