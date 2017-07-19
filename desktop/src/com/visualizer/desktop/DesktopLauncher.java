package com.visualizer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.visualizer.MainEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesktopLauncher {
	private static class MainWindow extends JFrame {
		private JPanel buttonPanel;
		private JButton button1, button2, button3;
		private LwjglApplicationConfiguration config;
		
		public MainWindow() {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			final Container container = getContentPane();
			container.setLayout(new BorderLayout());


			/*-----------------------------------------------------------*/
			
			button1 = new JButton( "load file" );
			button1.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("dupa1");
				} } );
			button1.setBounds(0, 0, 100, 20);
			add(button1);
/*
			buttonPanel = new JPanel();
			buttonPanel.setBounds(0, 0, 300, 30);
			add(buttonPanel);

			button1 = new JButton( "load file" );
			button1.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("dupa1");
				} } );
			button2 = new JButton( "x,y ind." );
			button2.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("dupa2");
				} } );
			button3 = new JButton( "x,y dep." );
			button3.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("dupa3");
				} } );

			button1.setBounds(0, 0, 100, 20);
			button2.setBounds(100, 0, 100, 20);
			button3.setBounds(200, 0, 100, 20);

			if(button1 == null) {
				System.out.println("dupa");
			}

			buttonPanel.add(button1);
			buttonPanel.add(button2);
			buttonPanel.add(button3);

			/*-----------------------------------------------------------*/
			
			config = new LwjglApplicationConfiguration();
			LwjglAWTCanvas canvas = new LwjglAWTCanvas(new MainEngine(), config);
			container.add(canvas.getCanvas(), BorderLayout.CENTER);
			
			pack();
			setVisible(true);
			setSize(800, 600);
			
			
			
			//setVisible(true);
		}
	}
	
	public static void main (String[] arg) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainWindow window = new MainWindow();
			}
		});
	}

//		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		new LwjglApplication(new MyGdxGame(), config);
//	}
}