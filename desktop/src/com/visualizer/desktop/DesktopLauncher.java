package com.visualizer.desktop;

import com.visualizer.userInterface.ModelSubframe;

import javax.swing.*;

public class DesktopLauncher {

	public static void main(String[] arg) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JDesktopPane desktop = new JDesktopPane();
		frame.setContentPane(desktop);
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		
		ModelSubframe subframe1 = new ModelSubframe("subframe_", 400, 300);
		desktop.add(subframe1);
		
		ModelSubframe subframe2 = new ModelSubframe("subframe_", 400, 300);
		desktop.add(subframe2);
		
		frame.setVisible(true);
		frame.setSize(800, 600);
		
		
	}
}


