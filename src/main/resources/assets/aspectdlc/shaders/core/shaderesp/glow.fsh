#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform vec3 color;
uniform vec3 color2;
uniform float exposure;
uniform float time;
uniform float animate;

in vec2 TexCoord;
out vec4 OutColor;

void main() {
    vec2 uv = TexCoord;
    vec4 bloom  = texture(Sampler0, uv);
    vec4 mask   = texture(Sampler1, uv);

    // Чёткий силуэт — берём максимальный канал маски
    float maskVal = max(max(mask.r, mask.g), max(mask.b, mask.a));

    // --- Чёткая граница (sharp outline) ---
    // Жёсткий внешний контур с минимальным размытием
    float edge = smoothstep(0.002, 0.065, maskVal);          // почти бинарная граница
    float outerGlow = clamp(bloom.a, 0.0, 1.0);

    // Внешнее свечение: bloom минус закрытая маска
    float halo = max(outerGlow - edge * 0.30, 0.0);
    halo = smoothstep(0.005, 0.18, halo);
    halo = pow(clamp(halo, 0.0, 1.0), 1.08);

    // Дополнительный тонкий ring прямо у контура
    float ring = edge * (1.0 - edge);                        // пик на границе
    ring = pow(clamp(ring * 3.8, 0.0, 1.0), 1.5);

    // Пульсация
    float pulse = 1.0;
    if (animate > 0.5) {
        pulse = 0.93 + 0.07 * sin(time * 2.2);
    }

    // Итоговая интенсивность: сильный ring + halo
    float intensity = clamp((ring * 0.82 + halo * 0.45) * exposure * pulse * 3.5, 0.0, 1.0);
    if (intensity <= 0.002) discard;

    // Градиент по Y от color до color2
    vec3 grad = mix(color, color2, uv.y);

    // Яркий белый highlights у самого контура
    float highlight = pow(ring, 2.5) * 0.35;
    grad = mix(grad, vec3(1.0), highlight);

    OutColor = vec4(grad, intensity);
}
