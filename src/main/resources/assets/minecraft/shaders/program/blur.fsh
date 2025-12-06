#version 120

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform float Radius;
uniform float sigma;

float gaussianOffsets(float x, float sigma) {
    float pow = x / sigma;
    return (1.0 / (abs(sigma) * 2.50662827463) * exp(-0.5 * pow * pow));
}

void main() {

    vec4 blurredColor = vec4(0.0);
    float horizontal = BlurDir.x * oneTexel.x;
    float vertical = BlurDir.y * oneTexel.y;
    for(float r = -Radius; r <= Radius; r++) {
        blurredColor += texture2D(DiffuseSampler, vec2(texCoord.x + r * horizontal, texCoord.y + r * vertical)) * gaussianOffsets(r, Radius / 2);
    }
    gl_FragColor = vec4(blurredColor.rgb, 1.0);
}