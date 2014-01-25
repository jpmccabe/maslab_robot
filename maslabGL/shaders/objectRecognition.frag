varying float x;
varying float y;
uniform sampler2D txtr;

void main() {
	float dx = 1.0/320.0;
	float dy = 1.0/240.0;
	
	// BALL DETECTION
	// Stochastically sample from pixel center.
	// If many samples begin missing at a particular radius, return.
	// That radius is the ball radius, and this pixel is the center.
	// Color returned as RED.
	// Radius = RED*64.0/256.0;
	//
	int r = 1;
	int miss = 0;	
	while ( r < 50 ) {
		miss = 0;
		float start = sin(r+x*y)*3.14;
		for ( float a = start; a < start+6.28; a+=0.5 ) {
			vec3 col = texture2D(txtr,vec2(x+r*cos(a)*dx,y+r*sin(a)*dy));
			if ( !(col.z==1 && col.y==0 && col.x==0) )
				miss++;
		}
		if ( miss>=4 )
			break;
		r++;
	}
	if ( miss>=8 && r>=4 )
		gl_FragColor = vec4(1,0,0,1);//r/64.0,0,0,1);

		
	// WALL DETECTION
	// 
	//
	
	if ( y > 0.5 )
		return;
	
	float my = y;
	vec3 col = texture2D(txtr,vec2(x,my));
	vec3 col2 = texture2D(txtr,vec2(x,my-dy));
	if ( length(vec3(col.z-0.3,col.y-0.3,col.x-0.3))<0.02 || length(vec3(col.z-1,col.y-1,col.x-1))<0.02 ) {
		if ( col2.z==0 && col2.y==0 && col2.x==1 ) {
			gl_FragColor = vec4(0,0,1,1);
			/*
			while ( my > 0 ){
				vec3 col = texture2D(txtr,vec2(x,my));
				my -= dy;
			}*/
		}
	} 
}