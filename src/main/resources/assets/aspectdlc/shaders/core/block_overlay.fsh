#version 150

in vec2 FragCoord;
in vec4 FragColor;

uniform float Time;
uniform vec4 ThemeColor;
uniform float Alpha;

out vec4 OutColor;

#define MAX_ITER 10

void main() {
    vec2 p = FragCoord * 8.0 - vec2(20.0);
    vec2 i = p;
    float c = 1.0;
    float inten = 0.05;

    for (int n = 0; n < MAX_ITER; n++) {
        float t = Time * (0.5 - (2.0 / float(n + 1)));
        i = p + vec2(cos(t - i.x) + sin(t + i.y), sin(t - i.y) + cos(t + i.x));
        c += 1.0 / length(vec2(p.x / (sin(i.x + t) / inten), p.y / (cos(i.y + t) / inten)));
    }

    c /= float(MAX_ITER);
    c = 1.5 - sqrt(c);

    vec3 wave = vec3(c * c * c * c) + vec3(0.2 * sin(Time) + 0.5, 0.2, 0.4);
    vec3 tinted = mix(wave, ThemeColor.rgb, 0.55);

    OutColor = vec4(tinted, clamp(c * c * 1.8, 0.0, 1.0) * Alpha * FragColor.a);
}
