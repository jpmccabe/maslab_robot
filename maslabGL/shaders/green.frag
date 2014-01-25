varying float x;
varying float y;
uniform sampler2D txtr;

void main() {
	vec3 color = texture2D(txtr,vec2(x,y));
	gl_FragColor = vec4(color.z,color.y,color.x,1);
	
	// x = blue
	// y = green
	// z = red
	
	if ( color.y > (color.z+4.0/255.0) && color.z > (color.x+12.0/255.0) )
		gl_FragColor = vec4(0,1,0,1);
	else
		gl_FragColor = vec4(0,0,0,1);
}