package jogl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;

public class ShaderProgram {
	
	private int ID;
	private ArrayList<Integer> shaders;
	
	public ShaderProgram() {
		shaders = new ArrayList<Integer>();
	}
	
	public void loadShader(GL2 gl, String path) {
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
		String[] shaderSource = {output};
		
		int currentShader = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
		gl.glShaderSource(currentShader, 1, shaderSource, null);
		gl.glCompileShader(currentShader);
		checkLogInfo(gl, currentShader);
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
	
}
