varying float x;
varying float y;
uniform sampler2D txtr;

void main() {
	float dx = 1.0/512.0;
	float dy = 1.0/512.0;
	
	vec3 self = texture2D(txtr,vec2(x,y));
	
	vec3 n0 = texture2D(txtr,vec2(x-dx,y-dy));
	vec3 n1 = texture2D(txtr,vec2(x,y-dy));
	vec3 n2 = texture2D(txtr,vec2(x+dx,y-dy));
	
	vec3 n3 = texture2D(txtr,vec2(x-dx,y));
	vec3 n4 = texture2D(txtr,vec2(x+dx,y));
	
	vec3 n5 = texture2D(txtr,vec2(x-dx,y+dy));
	vec3 n6 = texture2D(txtr,vec2(x,y+dy));
	vec3 n7 = texture2D(txtr,vec2(x+dx,y+dy));
	
	vec3 GX = -1*n0+1*n2 -2*n3+2*n4 -1*n5+1*n7;
	vec3 GY = -1*n0+1*n5 -2*n1+2*n6 -1*n2+1*n7;
	
	float lx = length(GX);
	float ly = length(GY);
	float d = lx*lx+ly*ly;
	
	if ( d > 0.1 )
		gl_FragColor = vec4(d,d,d,1);
}