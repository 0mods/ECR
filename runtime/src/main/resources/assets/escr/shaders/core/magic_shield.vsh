#version 330
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;

out vec3 viewPosition;
out float waveDistance;
out float waveProgress;
out float waveStrength;

void main() {
    vec4 viewSpace = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * viewSpace;

    viewPosition = viewSpace.xyz;
    waveDistance = Color.r;
    waveProgress = Color.g;
    waveStrength = Color.b;
}
