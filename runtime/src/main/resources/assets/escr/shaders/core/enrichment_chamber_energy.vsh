#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;

out vec3 effectPosition;
out vec3 viewPosition;
out vec4 vertexColor;
flat out float overflowState;

void main() {
    vec4 viewSpace = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * viewSpace;

    effectPosition = Position;
    viewPosition = viewSpace.xyz;
    vertexColor = Color;
    overflowState = step(Color.r, Color.g);
}
