package jogl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class Textures extends GLCanvas implements GLEventListener {
	
	private IntBuffer vboID;
	private IntBuffer eboID;
	private IntBuffer colorID;
	private IntBuffer vaoID;
	private IntBuffer texID;
	private IntBuffer texID2;
	private IntBuffer texCoordsID;
	private FloatBuffer vboMesh;
	private FloatBuffer vboColor;
	private int texWidth;
	private int texHeight;
	private float[] points = {
			 // positions		 // texture coords
			 0.5f,  0.5f,  0.0f, 1.0f, 1.0f,
			 0.5f, -0.5f,  0.0f, 1.0f, 0.0f,
			-0.5f, -0.5f,  0.0f, 0.0f, 0.0f,
			-0.5f,  0.5f,  0.0f, 0.0f, 1.0f
			
	};
	private int[] indices = {
			0, 1, 2,
			0, 2, 3
	};
	private float[] color = {
			1.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f
	};
	
	private int program;
	private int vertShader, fragShader;
	private String[] vertSource, fragSource;
	
	private float rotation;
	int rotationLoc;
	
	final int FPS = 60;
	
	private GLU glu = new GLU();
	
	public Textures() {
		vboID = IntBuffer.allocate(1);
		eboID = IntBuffer.allocate(1);
		colorID = IntBuffer.allocate(1);
		vaoID = IntBuffer.allocate(1);
		texID = IntBuffer.allocate(1);
		texID2 = IntBuffer.allocate(1);
		texCoordsID = IntBuffer.allocate(1);
		vertSource = new String[1];
		fragSource = new String[1];
		rotation = 0;
		rotationLoc = 0;
		texWidth = 0;
		texHeight = 0;
		this.addGLEventListener(this);
	}
	
	public String loadShader(String path) {
		String output = "";
		try {
			File file = new File("." + path);
			FileReader fr = new FileReader(file);

			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null)
				output += line + "\n";
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);

		
				
		vboMesh = GLBuffers.newDirectFloatBuffer(points);
		System.out.println(points.length * Float.BYTES);
		gl.glGenBuffers(1, vboID);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID.get(0));
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, points.length * Float.BYTES, vboMesh, GL2.GL_STATIC_DRAW);
		
		IntBuffer indexData = GLBuffers.newDirectIntBuffer(indices);
		gl.glGenBuffers(1, eboID);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, eboID.get(0));
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.length * Integer.BYTES, indexData, GL2.GL_STATIC_DRAW);
		
		vboColor = GLBuffers.newDirectFloatBuffer(color);
		gl.glGenBuffers(1, colorID);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, colorID.get(0));
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, color.length * Float.BYTES, vboColor, GL2.GL_STATIC_DRAW);
		
		gl.glGenVertexArrays(1, vaoID);
		gl.glBindVertexArray(vaoID.get(0));
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID.get(0));
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, eboID.get(0));
		gl.glVertexAttribPointer(0, 3, GL2.GL_FLOAT, false, 5 * Float.BYTES, 0);
		gl.glVertexAttribPointer(2, 2, GL2.GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
		gl.glEnableVertexAttribArray(0);
		gl.glEnableVertexAttribArray(2);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, colorID.get(0));
		gl.glVertexAttribPointer(1, 3, GL2.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		vertSource[0] = loadShader("/res/shaders/hello-triangle/texture.vert");
		fragSource[0] = loadShader("/res/shaders/hello-triangle/texture.frag");
		
		vertShader = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
		gl.glShaderSource(vertShader, 1, vertSource, null);
		gl.glCompileShader(vertShader);
		checkLogInfo(gl, vertShader);

		fragShader = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fragShader, 1, fragSource, null);
		gl.glCompileShader(fragShader);
		checkLogInfo(gl, vertShader);
		
		program = gl.glCreateProgram();
		gl.glAttachShader(program, fragShader);
		gl.glAttachShader(program, vertShader);
		gl.glLinkProgram(program);
		rotationLoc = gl.glGetUniformLocation(program, "rotation");
		
		gl.glGenTextures(1, texID);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texID.get(0));
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_MIRRORED_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_MIRRORED_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		try {
			File f = new File("./res/textures/wall.jpg");
			System.out.println(f.toURL().toString());
			TextureData data = TextureIO.newTextureData(gl.getGLProfile(), f.toURL(), false, TextureIO.JPG);
			gl.glTexImage2D(GL2.GL_TEXTURE_2D,
					0,
					GL2.GL_RGB,
					data.getWidth(),
					data.getHeight(),
					0,
					GL2.GL_RGB,
					GL2.GL_UNSIGNED_BYTE,
					data.getBuffer());
			gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		gl.glGenTextures(1, texID2);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texID2.get(0));
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
		try {
			File f = new File("./res/textures/ccf.png");
			System.out.println(f.toURL().toString());
			TextureData data = TextureIO.newTextureData(gl.getGLProfile(), f.toURL(), false, TextureIO.PNG);
			gl.glTexImage2D(GL2.GL_TEXTURE_2D,
					0,
					GL2.GL_RGBA,
					data.getWidth(),
					data.getHeight(),
					0,
					GL2.GL_RGBA,
					GL2.GL_UNSIGNED_BYTE,
					data.getBuffer());
			gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);
			gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gl.glUseProgram(program);
		gl.glUniform1i(gl.glGetUniformLocation(program, "ourTexture"), 0);
		gl.glUniform1i(gl.glGetUniformLocation(program, "texture2"), 1);		
	}
	
	private void checkLogInfo(GL2 gl, int programObject) {
        IntBuffer intValue = Buffers.newDirectIntBuffer(1);
        gl.glGetObjectParameterivARB(programObject, GL2.GL_OBJECT_INFO_LOG_LENGTH_ARB, intValue);

        int lengthWithNull = intValue.get();

        if (lengthWithNull <= 1) {
        	System.out.println("Shader compiled with no errors.");
            return;
        }

        ByteBuffer infoLog = Buffers.newDirectByteBuffer(lengthWithNull);

        intValue.flip();
        gl.glGetInfoLogARB(programObject, lengthWithNull, intValue, infoLog);

        int actualLength = intValue.get();

        byte[] infoBytes = new byte[actualLength];
        infoLog.get(infoBytes);
        System.out.println("GLSL Validation >> " + new String(infoBytes));
    }

	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT);
	//	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
	
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texID.get(0));
		gl.glActiveTexture(GL2.GL_TEXTURE1);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texID2.get(0));
		
	    gl.glUseProgram(program);
		gl.glUniform1f(rotationLoc, rotation);
		gl.glBindVertexArray(vaoID.get(0));
		gl.glDrawElements(GL2.GL_TRIANGLES, indices.length, GL2.GL_UNSIGNED_INT, 0);
		gl.glBindVertexArray(0);
		gl.glUseProgram(0);
		rotation += 0.01f; 
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		final GL2 gl = drawable.getGL().getGL2();
		if(height <= 0) height = 1;
			final float h = (float) width / (float) height;
			gl.glViewport(0, 0, width, height);
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluPerspective(45.0f, h, 1.0, 1000.0);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
	}
	
}
