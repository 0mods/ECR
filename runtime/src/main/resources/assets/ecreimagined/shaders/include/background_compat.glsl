#ifdef ECR_RENDER_PIPELINE
layout(std140) uniform Globals {
    ivec3 CameraBlockPos;
    vec3 CameraOffset;
    vec2 ScreenSize;
    float GlintAlpha;
    float GameTime;
    int MenuBlurRadius;
    int UseRgss;
};

in vec4 vertexColor;

vec2 ecrLocalSize() {
    float width = abs(dFdx(texCoord0.x)) > 0.000001 ? 1.0 / abs(dFdx(texCoord0.x)) : ScreenSize.x;
    float height = abs(dFdy(texCoord0.y)) > 0.000001 ? 1.0 / abs(dFdy(texCoord0.y)) : ScreenSize.y;
    return vec2(width, height);
}

vec2 ecrScrollOffset() {
    float packedAlpha = floor(vertexColor.a * 255.0 + 0.5);
    float packedBlue = floor(vertexColor.b * 255.0 + 0.5);
    float alphaData = mod(packedAlpha, 128.0);
    vec2 highBits = floor(vertexColor.rg * 255.0 + 0.5) * 16.0;
    vec2 lowBits = vec2(floor(alphaData / 8.0), mod(alphaData, 8.0) + floor(packedBlue / 128.0) * 8.0);
    return (highBits + lowBits) / 4095.0;
}

float ecrZoom() {
    return mod(floor(vertexColor.b * 255.0 + 0.5), 32.0) / 31.0;
}

float ecrStarDensity() {
    return 0.75 + mod(floor(floor(vertexColor.b * 255.0 + 0.5) / 32.0), 2.0) * 0.25;
}

float ecrStarSize() {
    return 1.0 + mod(floor(floor(vertexColor.b * 255.0 + 0.5) / 64.0), 2.0) * 0.5;
}

#define size ecrLocalSize()
#define scrollOffset ecrScrollOffset()
#define scrollSize vec2(1.0)
#define time (GameTime * 1200.0)
#define zoom (4.0 + ecrZoom() * 24.0)
#define starDensity ecrStarDensity()
#define starSize ecrStarSize()
#else
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec2 size;
uniform vec2 scrollOffset;
uniform vec2 scrollSize;
uniform float time;
uniform float zoom;
#define starDensity 1.0
#define starSize 1.0
#endif
