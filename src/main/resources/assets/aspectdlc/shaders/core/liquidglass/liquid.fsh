#version 150

in vec2 FragCoord;
in vec2 TexCoord;
in vec4 FragColor;

uniform sampler2D Sampler0;
uniform vec2 Size;
uniform vec4 Radius;
uniform float Smoothness;
uniform float CornerSmoothness;
uniform float GlobalAlpha;
uniform float FresnelPower;
uniform vec3 FresnelColor;
uniform float FresnelAlpha;
uniform float BaseAlpha;
uniform int FresnelInvert;
uniform float FresnelMix;
uniform float DistortStrength;
uniform vec2 MotionVector;
uniform float MotionStrength;

out vec4 OutColor;

vec4 sampleMotionBlur(vec2 uv) {
    vec4 acc = vec4(0.0);
    float weightSum = 0.0;
    const int SAMPLES = 7;
    for (int i = 0; i < SAMPLES; i++) {
        float t = (float(i) / float(SAMPLES - 1)) - 0.5;
        vec2 offset = MotionVector * MotionStrength * t;
        float w = 1.0 - abs(t) * 0.72;
        acc += texture(Sampler0, clamp(uv + offset, 0.0, 1.0)) * w;
        weightSum += w;
    }
    return acc / max(weightSum, 0.001);
}

float roundedBoxSDF(vec2 center, vec2 size, vec4 radius) {
    radius.xy = (center.x > 0.0) ? radius.xy : radius.zw;
    radius.x = (center.y > 0.0) ? radius.x : radius.y;
    vec2 q = abs(center) - size + radius.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius.x;
}

void main() {
    vec2 center = Size * 0.5;
    vec2 pos = (FragCoord * Size) - center;

    float dist = roundedBoxSDF(pos, center - 1.0, Radius);
    float alpha = 1.0 - smoothstep(-Smoothness, Smoothness, dist);

    if (alpha < 0.001) {
        discard;
    }

    float edgeDist = abs(dist);
    float maxDist = min(center.x, center.y) * 0.5;
    float edgeGradient = clamp(edgeDist / maxDist, 0.0, 1.0);

    float fresnel;
    if (FresnelInvert == 1) {
        fresnel = pow(1.0 - edgeGradient, FresnelPower);
    } else {
        fresnel = pow(edgeGradient, FresnelPower);
    }
    fresnel = clamp(fresnel, 0.0, 1.0);

    vec2 distortDir = normalize(pos + vec2(0.0001));
    vec2 distortedUV = TexCoord + distortDir * fresnel * DistortStrength;
    distortedUV = clamp(distortedUV, 0.0, 1.0);

    vec4 texColor = MotionStrength > 0.0005
        ? sampleMotionBlur(distortedUV)
        : texture(Sampler0, distortedUV);

    vec3 finalColor = mix(texColor.rgb, FresnelColor, fresnel * FresnelMix);

    OutColor = vec4(finalColor, alpha * GlobalAlpha) * FragColor;
}
