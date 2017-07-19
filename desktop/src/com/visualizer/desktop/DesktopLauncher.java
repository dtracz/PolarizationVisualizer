package com.visualizer.desktop;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.visualizer.MainEngine;
import org.lwjgl.Sys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesktopLauncher {
	private static class ModelWindow extends JPanel {
		public ModelWindow() {
		}
	}
	
	private static class MainWindow extends JFrame {
		private JPanel panel;
		private JButton button1, button2, button3;
		
		public MainWindow() {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			//final Container container = getContentPane();
			//container.setLayout(new BorderLayout());
			
			/*-----------------------------------------------------------*/
			
			panel = new ModelWindow();
			panel.setBounds(600,0, 200, 600);
			add(panel);
			
			
			button1 = new JButton( "load file1" );
			button1.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("dupa1");
				} } );
			button1.setBounds(0, 0, 100, 20);
		//	add(button1);
			
			button2 = new JButton( "load file2" );
			button2.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("dupa1");
				} } );
			button2.setBounds(100, 0, 100, 20);
		//	add(button2);
			
			button3 = new JButton( "load file3" );
			button3.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("dupa1");
				} } );
			button3.setBounds(200, 0, 100, 20);
		//	add(button3);
			
			/*-----------------------------------------------------------*/
			
			LwjglApplicationConfiguration config;
			config = new LwjglApplicationConfiguration();
			LwjglAWTCanvas canvas = new LwjglAWTCanvas(new MainEngine());
			add(canvas.getCanvas(), BorderLayout.CENTER);
			
			
			//pack();
			setVisible(true);
			setSize(800, 600);
			
			
			
			//setVisible(true);
		}
	}
	
	public static void main (String[] arg) {
		MainWindow window = new MainWindow();
	/*	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainWindow window = new MainWindow();
			}
		}); //*/
	}
	
//		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		new LwjglApplication(new MyGdxGame(), config);
//	}
}