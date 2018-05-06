package utilities;

import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;

import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.VectorUtil;

public class Camera {
	
	public static enum Movement {
		FORWARD, BACKWARD, LEFT, RIGHT;
	}
	public static enum Turn {
		UP, DOWN, LEFT, RIGHT;
	}
	
	private float[] pos;
	private float[] front;
	private float[] up;
	private boolean mouseControl;
	private boolean mouseEnabled;
	private float pitch, yaw, roll;
	private float speed;
	private float sensitivity;
	private Point lastMouse;
	private boolean firstMouse;
	float[] temp = new float[3];
	private float fov;
	private Point mouseCenter;
	
	public Camera() {
		pos = new float[] {0.0f, 0.0f, 0.0f};
		front = new float[] {0.0f, 0.0f, -1.0f};
	}
	
	public Camera(float[] p, float[] f) {
		pos = p;
		front = f;
	}
	
	public Camera(float x, float y, float z) {
		pos = new float[] {x, y, z};
	}
	
	{
		mouseControl = false;
		front = new float[] {0.0f, 0.0f, -1.0f};
		up = new float[] {0.0f, 1.0f, 0.0f};
		speed = 0.25f;
		sensitivity = 1.0f;
		pitch = 0.0f;
		yaw = 0.0f;
		roll = 0.0f;
		fov = 66.0f;
		firstMouse = true;
		mouseCenter = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
	}
	
	public void setPos(float[] p) {
		pos = p;
	}
	
	public void setPos(float x, float y, float z) {
		pos[0] = x;
		pos[1] = y;
		pos[2] = z;
	}
	
	public float[] getPos() {
		return pos;
	}
	
	public float[] getFront() {
		return front;
	}
	
	public float[] getUp() {
		return up;
	}
	
	public void setMouseControl(boolean b) {
		mouseControl = b;
	}
	
	public boolean getMouseControl() {
		return mouseControl;
	}
	
	public void setSensitivity(float s) {
		sensitivity = s;
	}
	
	public float getSensitivity() {
		return sensitivity;
	}
	
	public void setSpeed(float s) {
		speed = s;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setPitch(float p) {
		pitch = p;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public void setYaw(float y) {
		yaw = y;
	}
	
	public float getYaw() {
		return yaw;
	}
	public void setRoll(float r) {
		roll = r;
	}
	
	public float getRoll() {
		return roll;
	}
	
	public float[] getViewMatrix() {
		float[] view = new float[16];
		FloatUtil.makeLookAt(view, 0, getPos(), 0, VectorUtil.addVec3(new float[3], getPos(), getFront()), 0, getUp(), 0, new float[16]);
		return view;
	}
	
	public void setFOV(float newfov) {
		fov = newfov;
	}
	
	public void changeFOV(float change) {
		fov -= change;
	}
	
	public float getFOV() {
		return fov;
	}
	
	public void setMouseEnabled(boolean enabled) {
		mouseEnabled = enabled;
	}
	
	public boolean isMouseEnabled() {
		return mouseEnabled;
	}
	
	public void setMouseCenter(Point p) {
		mouseCenter = p;
	}
	
	public void move(Movement direction) {
		if(direction == Movement.FORWARD) {
			VectorUtil.scaleVec3(temp, front, speed);
			VectorUtil.addVec3(pos, pos, temp);
		} else if(direction == Movement.BACKWARD) {
			VectorUtil.scaleVec3(temp, front, speed);
			VectorUtil.subVec3(pos, pos, temp);
		} else if(direction == Movement.RIGHT) {
			VectorUtil.scaleVec3(temp, VectorUtil.normalizeVec3(VectorUtil.crossVec3(temp, front, up)), speed);
			VectorUtil.addVec3(pos, pos, temp);
		} else if(direction == Movement.LEFT) {
			VectorUtil.scaleVec3(temp, VectorUtil.normalizeVec3(VectorUtil.crossVec3(temp, front, up)), speed);
			VectorUtil.subVec3(pos, pos, temp);
		}
	}
	
	public void turn(Turn direction) {
		if(direction == Turn.UP) {
			pitch += sensitivity;
		} else if(direction == Turn.DOWN) {
			pitch -= sensitivity;
		} else if(direction == Turn.RIGHT) {
			yaw += sensitivity;
		} else if(direction == Turn.LEFT) {
			yaw -= sensitivity;
		}
	}
	
	public void updateCamera() {
		updateDirection();
	}
	
	public void updateDirection() {
		front[0] = (float) Math.sin(Math.toRadians(yaw));
		front[1] = (float) Math.sin(Math.toRadians(pitch));
		front[2] = (float) ( Math.cos(Math.toRadians(pitch)) * -Math.cos(Math.toRadians(yaw)));
	
		if(mouseControl && mouseEnabled) {
			if(firstMouse) {
				System.out.println("first");
				lastMouse = mouseCenter;
				firstMouse = false;
			}
			int newx = MouseInfo.getPointerInfo().getLocation().x;
			int newy = MouseInfo.getPointerInfo().getLocation().y;
			yaw += sensitivity * (newx - lastMouse.getX()) / 10;
			pitch += -sensitivity * (newy - lastMouse.getY()) / 10;
			lastMouse = new Point(newx, newy);
		}
	}
}
