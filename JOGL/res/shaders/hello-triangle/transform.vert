#version 410

#define PI 3.1415926535897932384626433832795

// SAVE THE SHADER BEFORE YOU TEST

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

layout(location = 0) in vec3 vertex_position;
layout(location = 1) in vec3 vertex_color;
layout(location = 2) in vec2 tex_coord;

out vec3 color;
out vec2 texCoord;

void main() {
	
	// Math is in RADIANS
//	gl_Position = model * vec4(vertex_position, 1.0);
	gl_Position = projection * view * model * vec4(vertex_position, 1.0);
	color = vertex_color;
	texCoord = tex_coord;
}