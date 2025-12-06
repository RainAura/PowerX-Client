
uniform float iTime;
uniform vec2 iMouse;
uniform vec2 iResolution;

float timeScale = 0.5;
float pi = 3.14;

float fractured(float offset){
    float _fractured = fract(iTime*timeScale + offset);
    _fractured = distance(_fractured, 0.5) * 2.;
    _fractured = 1. - _fractured;
    return _fractured;
}

void main(void) 
{
    vec2 xy = gl_FragCoord/iResolution.xy;
    float geometry = -1.*(sin(xy.x*pi/2.)+cos(xy.y*pi/2.))/6.;
    vec4 texColor = vec4(
        fractured(0. + geometry),
        fractured(0.33 + geometry),
        fractured(0.67 + geometry),
        1.0);
    gl_FragColor = texColor;
    
}