varying float x;
varying float y;
uniform sampler2D txtr;

void main() {
	float dx = 1.0/320.0;
	float dy = 1.0/240.0;
	
	vec3 col = texture2D(txtr,vec2(x,y));
	float lum = length(col)/1.73205;
	
	// WALL
	// Temporal matching
	// Uniform white expects to have low standard deviation!
	// Assume current color is the mean
	float diff = 0;
	int sz = 5;
	vec3 avg = vec3(0,0,0);
	for ( int j = -sz; j <= sz; j++ ) {
		avg += texture2D(txtr,vec2(x,y+j*dy));
	}
	avg = avg/(sz*2+1);
	for ( int j = -sz; j <= sz; j++ ) {
		vec3 other = texture2D(txtr,vec2(x,y+j*dy));
		diff += pow(length(other-avg),2);
	}
	float stddev = sqrt(diff/(sz*2+1));
	//if ( stddev<0.07 )
	gl_FragColor = vec4(1,1,1,1);
			
	// FLOOR
	// R(x) = 1.08636/(1 + pow(2,-5.43028*(x-0.583279)))
	// G(x) = 1.2038/(1 + pow(2,-4.87707*(x-0.592743)))
	// B(x) = 1.06218/(1 + pow(2,-5.70837*(x-0.497658)))
	
	float redM = 1.08636/(1 + pow(2,-5.43028*(lum-0.583279)));
	float greenM = 1.2038/(1 + pow(2,-4.87707*(lum-0.592743)));
	float blueM = 1.06218/(1 + pow(2,-5.70837*(lum-0.497658)));
	float dR = redM-col.z;
	float dG = greenM-col.y;
	float dB = blueM-col.x;
	
	if ( dR*dR+dG*dG+dB*dB<0.02 && stddev>0.02 ) {
		gl_FragColor = vec4(0.3,0.3,0.3,1);
	}
	
	// RED
	// Color Model = a/(1 + 2^(-k*(x + c)))
	// R(x) = 0.96271/(1 + pow(2,-10.5543*(x-0.343443)))
	// G(x) = 1.28698/(1 + pow(2,-4.91718*(x-0.774994)))
	// B(x) = 61.3266/(1 + pow(2,-2.64158*(x-3.34511)))
	
	redM = 0.96271/(1 + pow(2,-10.5543*(lum-0.343443)));
	greenM = 1.28698/(1 + pow(2,-4.91718*(lum-0.774994)));
	blueM = 61.3266/(1 + pow(2,-2.64158*(lum-3.34511)));
	dR = redM-col.z;
	dG = greenM-col.y;
	dB = blueM-col.x;
	
	if ( dR*dR+dG*dG+dB*dB<0.025 )
		gl_FragColor = vec4(1,0,0,1);

	// BLUE
	// Color Model = a/(1 + 2^(-k*(x + c)))
	// R(x) = 0.4/(1 + pow(2,-16.3332*(x-0.359805)))
	// G(x) = 0.543269/(1 + pow(2,-16.3332*(x-0.359805)))
	// B(x) = 6.52837/(1 + pow(2,-4.83888*(x-1.14863)))
	
	redM = 0.4/(1 + pow(2,-16.3332*(lum-0.359805)));
	greenM = 0.543269/(1 + pow(2,-16.3332*(lum-0.359805)));
	blueM = 6.52837/(1 + pow(2,-4.83888*(lum-1.14863)));
	dR = redM-col.z;
	dG = greenM-col.y;
	dB = blueM-col.x;
	
	if ( dR*dR+dG*dG+dB*dB<0.02 )
		gl_FragColor = vec4(0,0,1,1);
}