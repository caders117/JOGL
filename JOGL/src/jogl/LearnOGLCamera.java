package jogl;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.math.VectorUtil;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class LearnOGLCamera extends GLCanvas implements GLEventListener, KeyListener, MouseListener {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		LearnOGLCamera t = new LearnOGLCamera();
		final FPSAnimator animator = new FPSAnimator(t, 60, true);
		
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(animator.isStarted())
					animator.stop();
				System.exit(0);
			}
		});
		
	    animator.start();
		
		frame.getContentPane().add(t);
		t.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
					frame.dispose();
			}
		});
		t.addMouseListener(t);
		t.addKeyListener(t);
		frame.setSize(750, 750);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		t.requestFocus();
	}
	
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
/*	private float[] points = {
			 // positions		 // texture coords
			-0.5f, -0.5f,  0.0f, 0.0f, 0.0f,	// lower left
			 0.5f, -0.5f,  0.0f, 1.0f, 0.0f,	// lower right
			 0.5f,  0.5f,  0.0f, 1.0f, 1.0f,	// upper right
			-0.5f,  0.5f,  0.0f, 0.0f, 1.0f,	// upper left
			
			-0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
			 0.5f, -0.5f, -1.0f, 1.0f, 0.0f,
			 0.5f,  0.5f, -1.0f, 1.0f, 1.0f,
	        -0.5f,  0.5f, -1.0f, 0.0f, 1.0f,
	};*/
	private float[] points = {
			-0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
		     0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
		     0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		     0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		    -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
		    -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

		    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		     0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
		     0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
		     0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
		    -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
		    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

		    -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		    -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		    -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

		     0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		     0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		     0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		     0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		     0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		     0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

		    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
		     0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
		     0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
		     0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
		    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
		    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

		    -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
		     0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
		     0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		     0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
		    -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
		    -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
	};
	private float[][] cubePositions = {
			  { 0.0f,  0.0f,  0.0f },
			  { 2.0f,  5.0f, -15.0f},
			  {-1.5f, -2.2f, -2.5f },
			  {-3.8f, -2.0f, -12.3f},
			  { 2.4f, -0.4f, -3.5f },
			  {-1.7f,  3.0f, -7.5f },
			  { 1.3f, -2.0f, -2.5f },
			  { 1.5f,  2.0f, -2.5f },
			  { 1.5f,  0.2f, -1.5f },
			  {-1.3f,  1.0f, -1.5f },
			};
	private int[] indices = {
			0, 1, 2,
			0, 2, 3,
		/*	
			4, 5, 6,
			4, 6, 7,
			
			0, 4, 5,
			0, 1, 5,
			
			3, 7, 6,
			3, 2, 6,
			
			0, 4, 7,
			0, 3, 7,
			
			1, 5, 6,
			1, 2, 6*/
	};
	private float[] color = {
			1.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 1.0f,
			1.0f, 1.0f, 0.0f
	};
	private float[] cameraPos = {0.0f, 0.0f, 3.0f};
	private float[] cameraTarget = {0.0f, 0.0f, 0.0f};
	private float[] cameraFront = {0.0f, 0.0f, -1.0f};
	private float[] cameraUp = {0.0f, 1.0f, 0.0f};
	private int program;
	private int vertShader, fragShader;
	private String[] vertSource, fragSource;
	
	private float rotation;
	int modelLoc, projLoc, viewLoc;
	final int FPS = 60;
	
	private GLU glu = new GLU();
	
	public LearnOGLCamera() {
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
		
		vertSource[0] = loadShader("/res/shaders/hello-triangle/transform.vert");
		fragSource[0] = loadShader("/res/shaders/hello-triangle/transform.frag");
		
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
	/*	Matrix4 trans = new Matrix4();
		trans.rotate((float) (Math.PI/2.0), 0.0f, 0.0f, 1.0f);
		trans.scale(0.5f, 0.5f, 0.5f);
		FloatBuffer t = GLBuffers.newDirectFloatBuffer(trans.getMatrix());
		System.out.println(gl.glGetUniformLocation(program, "transform"));
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(program, "transform"), 1, false, t);
	*/
		modelLoc = gl.glGetUniformLocation(program, "model");
		projLoc = gl.glGetUniformLocation(program, "projection");
		viewLoc = gl.glGetUniformLocation(program, "view");
	
		gl.glEnable(GL2.GL_DEPTH_TEST);
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
	
		updateEvents();
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texID.get(0));
		gl.glActiveTexture(GL2.GL_TEXTURE1);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texID2.get(0));
		
		float[] cameraDirection = new float[3];
		VectorUtil.addVec3(cameraDirection, cameraPos, cameraTarget);
		float[] up = {0.0f, 1.0f, 0.0f};
		float[] cameraRight = new float[3];
		VectorUtil.crossVec3(cameraRight, up, cameraDirection);
		VectorUtil.normalizeVec3(cameraRight);
		
		Matrix4 proj = new Matrix4();
		proj.makePerspective((float) (Math.PI / 4), this.getWidth() / this.getHeight(), 0.1f, 100.0f);
		FloatBuffer p = GLBuffers.newDirectFloatBuffer(proj.getMatrix());
		
		Matrix4 view = new Matrix4();
		float[] temp = new float[16];
		float[] result = new float[3];
		FloatUtil.makeLookAt(view.getMatrix(), 0, cameraPos, 0, VectorUtil.addVec3(result, cameraPos, cameraFront), 0, cameraUp, 0, temp);
	/*  ***IMPORTANT***
	 * 	FloatUtil.makeLookAt(view.getMatrix(), 0, cameraPos, 0, cameraTarget, 0, up, 0, temp);
	 */
		FloatBuffer v = GLBuffers.newDirectFloatBuffer(view.getMatrix());
		
		gl.glUseProgram(program);
		gl.glBindVertexArray(vaoID.get(0));
		gl.glUniformMatrix4fv(projLoc, 1, false, p);
	    gl.glUniformMatrix4fv(viewLoc, 1, false, v);
		
		for(int i = 0; i < cubePositions.length; i++) {
			Matrix4 model = new Matrix4();
			model.translate(cubePositions[i][0], cubePositions[i][1], cubePositions[i][2]);
			if(i % 3 == 0)
				model.rotate(rotation, 1.0f, 1.0f, 0.5f);
			else
				model.rotate((float) Math.toRadians(20.0 * i), 1.0f, 1.0f, 0.5f);
			FloatBuffer m = GLBuffers.newDirectFloatBuffer(model.getMatrix());
			gl.glUniformMatrix4fv(modelLoc, 1, false, m);
			gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 36);
		}
	    
	//	gl.glDrawElements(GL2.GL_TRIANGLES, indices.length, GL2.GL_UNSIGNED_INT, 0);
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
			gl.glViewport(0, 0, width, height);
	}
	
	private ArrayList<Integer> keys = new ArrayList<Integer>();
	private float pitch = 0.0f, yaw = 0.0f, roll = 0.0f;
	private float cameraSpeed = 0.1f;
	private float sensitivity = 0.5f;
	private float zoom = 0.0f;
	private boolean mouseControl = false;
	private Point lastMouse;
	private boolean mouseInFrame = false;
	private boolean firstMouse = true;
	private boolean closeWindow = false;
	
	private void updateEvents() {
		updateCameraPos();
	}
	
	private void updateCameraPos() {
		float[] temp = new float[3];
		cameraFront[0] = (float) Math.sin(Math.toRadians(yaw));
		cameraFront[1] = (float) Math.sin(Math.toRadians(pitch));
		cameraFront[2] = (float) ( Math.cos(Math.toRadians(pitch)) * -Math.cos(Math.toRadians(yaw)));
	
		if(keys.contains(KeyEvent.VK_W)) {
			VectorUtil.scaleVec3(temp, cameraFront, cameraSpeed);
			VectorUtil.addVec3(cameraPos, cameraPos, temp);
		} 
		if(keys.contains(KeyEvent.VK_S)) {
			VectorUtil.scaleVec3(temp, cameraFront, cameraSpeed);
			VectorUtil.subVec3(cameraPos, cameraPos, temp);
		} 
		if(keys.contains(KeyEvent.VK_A)) {
			VectorUtil.scaleVec3(temp, VectorUtil.normalizeVec3(VectorUtil.crossVec3(temp, cameraFront, cameraUp)), cameraSpeed);
			VectorUtil.subVec3(cameraPos, cameraPos, temp);
		} 
		if(keys.contains(KeyEvent.VK_D)) {
			VectorUtil.scaleVec3(temp, VectorUtil.normalizeVec3(VectorUtil.crossVec3(temp, cameraFront, cameraUp)), cameraSpeed);
			VectorUtil.addVec3(cameraPos, cameraPos, temp);
		} 
		if(mouseControl) {
			if(mouseInFrame) {
				if(firstMouse) {
					System.out.println("first");
					lastMouse = MouseInfo.getPointerInfo().getLocation();
					firstMouse = false;
				}
				int newx = MouseInfo.getPointerInfo().getLocation().x;
				int newy = MouseInfo.getPointerInfo().getLocation().y;
				yaw += sensitivity * (newx - lastMouse.getX());
				pitch += -sensitivity * (newy - lastMouse.getY());
				lastMouse = new Point(newx, newy);
			}
		} else {
			if(keys.contains(KeyEvent.VK_RIGHT)) yaw += sensitivity * 2.5;
			if(keys.contains(KeyEvent.VK_LEFT)) yaw -= sensitivity * 2.5;
			if(keys.contains(KeyEvent.VK_UP)) pitch += sensitivity * 2.5;
			if(keys.contains(KeyEvent.VK_DOWN)) pitch -= sensitivity * 2.5;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("Key pressed");
		if(!keys.contains(e.getKeyCode())) 
			keys.add(e.getKeyCode());		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_M) mouseControl = !mouseControl;
		if(keys.contains(e.getKeyCode())) 
			keys.remove(keys.indexOf(e.getKeyCode()));		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		mouseInFrame = true;
		System.out.println("Mouse Entered Frame");
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		mouseInFrame = false;
		System.out.println("Mouse exited frame");
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
