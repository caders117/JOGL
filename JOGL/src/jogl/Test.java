package jogl;

import com.jogamp.opengl.math.FixedPoint;
import com.jogamp.opengl.math.Matrix4;

public class Test {
	public static void main(String[] args) {
		float[] vec = {1.0f, 0.0f, 0.0f, 1.0f};
		Matrix4 trans = new Matrix4();
		trans.translate(1.0f, 1.0f, 0.0f);
		trans.multVec(vec, vec);
		System.out.println(vec[0] + "" + vec[1]  + "" + vec[2]);
	}
}
