#version 150

in vec2 FragCoord;
in vec4 FragColor;

uniform vec2 Size;
uniform float Time;
uniform vec4 ThemeColor;
uniform float Alpha;

out vec4 OutColor;

#define NUM_OCTAVES 6

float random(vec2 pos) {
    return fract(sin(dot(pos.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

float noise(vec2 pos) {
    vec2 i = floor(pos);
    vec2 f = fract(pos);
    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

float fbm(vec2 pos) {
    float value = 0.0;
    float amplitude = 0.5;
    vec2 shift = vec2(100.0);
    mat2 rotation = mat2(cos(0.5), sin(0.5), -sin(0.5), cos(0.5));

    for (int i = 0; i < NUM_OCTAVES; i++) {
        float direction = mod(float(i), 2.0) > 0.5 ? 1.0 : -1.0;
        value += amplitude * noise(pos - 0.05 * direction * Time);
        pos = rotation * pos * 2.0 + shift;
        amplitude *= 0.5;
    }

    return value;
}

void main() {
    vec2 pos = (FragCoord * 2.0 - 1.0) * vec2(Size.x / Size.y, 1.0);
    pos -= vec2(12.0, 0.0);

    vec2 q = vec2(0.0);
    q.x = fbm(pos);
    q.y = fbm(pos + vec2(1.0));

    vec2 r = vec2(0.0);
    r.x = fbm(pos + q + vec2(1.7, 9.2) + 0.15);
    r.y = fbm(pos + q + vec2(8.3, 2.8) + 0.126);

    float field = fbm(pos + r);

    vec3 theme = ThemeColor.rgb;
    vec3 dark = theme * 0.15;
    vec3 middle = theme * 0.45;
    vec3 bright = theme * 0.75;

    vec3 color = mix(dark, middle, clamp(field * field * 4.0, 0.0, 1.0));
    color = mix(color, middle, clamp(length(q), 0.0, 1.0));
    color = mix(color, bright, clamp(length(r.x), 0.0, 1.0));
    color = (field * field * field + 0.9 * field * field + 0.8 * field) * color;

    OutColor = vec4(color * 0.85, Alpha);
}
