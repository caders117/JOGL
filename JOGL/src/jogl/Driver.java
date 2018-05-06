package jogl;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;

public class Driver {

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Transformations t = new Transformations();
	//	HelloTriangle t = new HelloTriangle();
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
		frame.setSize(750, 750);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
}
