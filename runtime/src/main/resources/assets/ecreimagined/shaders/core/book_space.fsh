#version 330
#define ECR_RENDER_PIPELINE

in vec2 texCoord0;
out vec4 fragColor;

#moj_import <ecreimagined:background_compat.glsl>

float hash21(vec2 point) {
    point = fract(point * vec2(123.34, 456.21));
    point += dot(point, point + 45.32);
    return fract(point.x * point.y);
}

void main() {
    vec2 offset = (scrollOffset - 0.5) * 0.75;
    float rawZ = (zoom - 4.0) / 24.0;
    float localZoom = 0.65 + rawZ * 1.8;

    vec2 resolution = size;
    if (resolution.y < 1.0) {
        resolution.y = 1080.0;
    }

    vec2 uv = gl_FragCoord.xy / resolution.y;
    uv = uv / localZoom + offset;

    vec3 color = vec3(0.008, 0.012, 0.035);
    float gameTime = time / 1200.0;

    for (int layer = 0; layer < 3; layer++) {
        float scale = 26.0 + float(layer) * 22.0;
        vec2 cell = floor(uv * scale);

        vec2 shift = vec2(hash21(cell + 11.1), hash21(cell + 22.2)) - 0.5;
        vec2 local = fract(uv * scale) - 0.5 - shift * 0.7;

        float random = hash21(cell + float(layer) * 19.7);
        float radius = mix(0.025, 0.14, random * random);
        float star = smoothstep(radius, 0.0, length(local));
        float twinkle = 0.65 + 0.35 * sin(gameTime * 180.0 + random * 31.0);
        vec3 tint = mix(vec3(0.45, 0.65, 1.0), vec3(1.0, 0.65, 0.45), random);
        color += tint * star * twinkle * (0.35 + float(layer) * 0.18);
    }

    float nebula = sin(uv.x * 3.1 + gameTime * 2.0) * cos(uv.y * 2.7 - gameTime * 1.4);
    color += vec3(0.035, 0.015, 0.08) * (nebula * 0.5 + 0.5);

    fragColor = vec4(color, 1.0);
}
