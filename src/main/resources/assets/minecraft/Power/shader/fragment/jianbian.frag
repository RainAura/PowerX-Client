
uniform float iTime;
uniform vec2 iMouse;
uniform vec2 iResolution;

void main()
{
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = gl_FragCoord/iResolution.xy;
    
    // some bs
    vec3 bg = vec3(0.1, 0.1, 0.1);
    vec3 white = vec3(0.9);
    vec3 col = bg;
    //vec2 center = vec2(200.0, iResolution.y - 150.0);
    vec2 center = iMouse.xy;
    float l = length(gl_FragCoord-center);
    float radius = ((cos(iTime*3.0)+1.0) * 40.0);
    //float radius = 1.0;
    float am = abs(l - radius) - 2.0;
    am = clamp(am, 0.0, 1.0);
    col = mix(white, bg, am);

    // Output to screen
    gl_FragColor = vec4(col, 1.0);
}