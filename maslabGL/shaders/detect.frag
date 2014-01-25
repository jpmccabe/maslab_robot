varying float x;
varying float y;
uniform sampler2D txtr;

void main() {
	float dx = 1.0/640.0;
	float dy = 1.0/480.0;
	/*
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
	*/
		
	// WALL DETECTION
	float my = y;
	int q = -4;
	
	//Find valid edge
	while ( q<2 ) {
		vec3 col = texture2D(txtr,vec2(x,y+q*dy));
		if ( q>0 && !(length(col-vec4(1,1,1,1))<0.02) )
			return;
		else if ( q<=0 && !(length(col-vec4(1,0,0,1))<0.02) )
			return;
		q++;
	}
	
	//Scan up for edge
	int pix = 10;
	my = y+10*dy;
	while ( my<1 ) {
		vec3 col = texture2D(txtr,vec2(x,my));
		if ( length(col-vec4(1,1,1,1))<0.02 ) {
			gl_FragColor = vec4(0,0,pix/200.0,1);
			return;		
		}
		my += dy;
		pix++;
	}
}