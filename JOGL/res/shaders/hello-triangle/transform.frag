#version 410

in vec3 color;
in vec2 texCoord;

out vec4 frag_Color;

uniform sampler2D ourTexture;
uniform sampler2D texture2;

void main() {

	//frag_Color = vec4(color, 1.0);
//	frag_Color = texture(texture2, texCoord);
//	frag_Color = texture(ourTexture, texCoord) * vec4(color, 1.0);
	frag_Color = mix(texture(ourTexture, texCoord), texture(texture2, texCoord), 0.4);
//	frag_Color = mix(texture(ourTexture, texCoord), texture(texture2, texCoord), 0.4) * vec4(color, 1.0);

}