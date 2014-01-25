varying float x;
varying float y;
uniform sampler2D txtr;

/*
    Any live cell with fewer than two live neighbours dies, as if caused by under-population.
    Any live cell with two or three live neighbours lives on to the next generation.
    Any live cell with more than three live neighbours dies, as if by overcrowding.
    Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
*/

void main() {
	float dx = 1.0/640.0;
	float dy = 1.0/480.0;
	
	int self = texture2D(txtr,vec2(x,y)).x>0.5?1:0;
	int n0 = texture2D(txtr,vec2(x-dx,y-dy)).x>0.5?1:0;
	int n1 = texture2D(txtr,vec2(x,y-dy)).x>0.5?1:0;
	int n2 = texture2D(txtr,vec2(x+dx,y-dy)).x>0.5?1:0;
	int n3 = texture2D(txtr,vec2(x-dx,y)).x>0.5?1:0;
	int n4 = texture2D(txtr,vec2(x+dx,y)).x>0.5?1:0;
	int n5 = texture2D(txtr,vec2(x-dx,y+dy)).x>0.5?1:0;
	int n6 = texture2D(txtr,vec2(x,y+dy)).x>0.5?1:0;
	int n7 = texture2D(txtr,vec2(x+dx,y+dy)).x>0.5?1:0;
	
	int n = n0+n1+n2+n3+n4+n5+n6+n7;
	if ( self==1 && n < 2 ) {
		gl_FragColor = vec4(0,0,0,1);
	} else if ( self==1 && (n==2 || n==3) ) {
		gl_FragColor = vec4(1,1,1,1);
	} else if ( self==1 && n>3 ) {
		gl_FragColor = vec4(0,0,0,1);
	} else if ( self==0 && n==3 ) {
		gl_FragColor = vec4(1,1,1,1);
	}
}