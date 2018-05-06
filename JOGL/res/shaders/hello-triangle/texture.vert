#version 410

#define PI 3.1415926535897932384626433832795

// SAVE THE SHADER BEFORE YOU TEST

uniform float scale = 1;
uniform float rotation = 0;

layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertex_color;
layout(location = 2) in vec2 tex_coord;

out vec3 color;
out vec2 texCoord;

void main() {
	
	float vpx = vertex_position.x;
	float vpy = vertex_position.y;
	float vpz = vertex_position.z;
	
	// Math is in RADIANS
//	gl_Position = vec4(vpx * cos(rotation) - vpy * sin(rotation), vpy * cos(rotation) + vpx * sin(rotation), vpz, 1.0);
	gl_Position = vec4(vertex_position, 1.0);
	color = vertex_color;
	texCoord = tex_coord;
}