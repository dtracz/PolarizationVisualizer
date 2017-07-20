package com.visualizer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.visualizer.engine.Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesktopLauncher {
	public static class MyInternalFrame extends JInternalFrame {
		static int openFrameCount = 0;
		static final int xOffset = 30, yOffset = 30;
		
		public MyInternalFrame() {
			super("Document #" + (++openFrameCount),
					true, //resizable
					true, //closable
					true, //maximizable
					true);//iconifiable
			
			//...Create the GUI and put it in the window...
			JButton button2 = new JButton("load file2");
			button2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("dupa1");
				}
			});
			button2.setBounds(100, 0, 100, 20);
			//	add(button2);
			
			//...Then set the window size or call pack...
			setSize(300, 300);
			
			//Set the window's location.
			setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
		}
	}
	
	public static void main(String[] arg) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JDesktopPane desktop = new JDesktopPane();
		frame.setContentPane(desktop);
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		
		MyInternalFrame inFrame = new MyInternalFrame();
		inFrame.setVisible(true);
		try {
			inFrame.setSelected(true); }
		catch(java.beans.PropertyVetoException e) { }
		
		desktop.add(inFrame);
		
		LwjglAWTCanvas canvas = new LwjglAWTCanvas(new Main());
		inFrame.add(canvas.getCanvas());
		
		frame.setVisible(true);
		frame.setSize(800, 600);
		
		
	}
}


