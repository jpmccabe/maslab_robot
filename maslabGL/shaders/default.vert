attribute float px;
attribute float py;
varying float x;
varying float y;

void main(){
	gl_Position = gl_ModelViewProjectionMatrix*vec4(px,py,1,1);
    x = px;
    y = py;
}