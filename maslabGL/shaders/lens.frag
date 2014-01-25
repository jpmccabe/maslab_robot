varying float x;
varying float y;
uniform sampler2D txtr;

void main() {

	float dx = (x-0.5);
	float dy = (y-0.5);
	float r = sqrt(dx*dx+dy*dy);

	float corr = 1-0.8*r*r+0.2*r*r*r*r;
	float xu = (dx)*(corr)+0.5;
	float yu = (dy)*(corr)+0.5;

	vec3 color = texture2D(txtr,vec2(xu,yu));
	gl_FragColor = vec4(color.z,color.y,color.x,1);
}