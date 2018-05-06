package jogl;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderUtil;

public class HelloTriangle extends GLCanvas implements GLEventListener {
	
	private IntBuffer vboID;
	private IntBuffer colorID;
	private IntBuffer vaoID;
	private IntBuffer ind;
	private FloatBuffer vboMesh;
	private FloatBuffer vboColor;
	private float[] points = {
			 0.0f,  0.5f,  0.0f,
			 0.5f, -0.5f,  0.0f,
			-0.5f, -0.5f,  0.0f,
	};
	private float[] color = {
			1.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 1.0f,
	};
	
	private float[] texCoords = {
		    0.0f, 0.0f,  
		    1.0f, 0.0f, 
		    0.5f, 1.0f  
		};
	
	private IntBuffer indexBufferID;
	private int[] indices = {0, 1, 2};
	
	private int tri;
	
	private int program;
	private int vertShader, fragShader;
	private String[] vertSource, fragSource;
	
	private float rotation;
	int rotationLoc;
	
	final int FPS = 60;
	
	private GLU glu = new GLU();
	
	public HelloTriangle() {
		vboID = IntBuffer.allocate(1);
		colorID = IntBuffer.allocate(1);
		vaoID = IntBuffer.allocate(1);
		indexBufferID = IntBuffer.allocate(1);
		vertSource = new String[1];
		fragSource = new String[1];
		rotation = 0;
		rotationLoc = 0;
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
		
		vboColor = GLBuffers.newDirectFloatBuffer(color);
		gl.glGenBuffers(1, colorID);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, colorID.get(0));
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, color.length * Float.BYTES, vboColor, GL2.GL_STATIC_DRAW);
		
		gl.glGenVertexArrays(1, vaoID);
		gl.glBindVertexArray(vaoID.get(0));
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboID.get(0));
		gl.glVertexAttribPointer(0, 3, GL2.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, colorID.get(0));
		gl.glVertexAttribPointer(1, 3, GL2.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		vertSource[0] = loadShader("/res/shaders/hello-triangle/triangle.vert");
		fragSource[0] = loadShader("/res/shaders/hello-triangle/triangle.frag");
		
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
		
	}
	
	private void checkLogInfo(GL2 gl, int programObject) {
        IntBuffer intValue = Buffers.newDirectIntBuffer(1);
        gl.glGetObjectParameterivARB(programObject, GL2.GL_OBJECT_INFO_LOG_LENGTH_ARB, intValue);

        int lengthWithNull = intValue.get();

        if (lengthWithNull <= 1) {
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
	    gl.glUseProgram(program);
		gl.glUniform1f(rotationLoc, rotation);

		gl.glBindVertexArray(vaoID.get(0));

	//	gl.glDrawElements(GL2.GL_TRIANGLES, 3, GL2.GL_UNSIGNED_INT, 0);
		gl.glDrawArrays(GL2.GL_TRIANGLES, 0, points.length / 3);
		
		gl.glBindVertexArray(0);
		gl.glUseProgram(0);
		rotation += 0.03f; 
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();
	}
	
}
