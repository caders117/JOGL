#version 410

in vec3 color;
in vec2 texCoord;

out vec4 frag_Color;


void main() {

	frag_Color = vec4(color, 1.0);
}