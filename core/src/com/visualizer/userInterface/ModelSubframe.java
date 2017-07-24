package com.visualizer.userInterface;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.visualizer.engine.Main;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class ModelSubframe extends JInternalFrame {
	private static final int xStart = -30, yStart = 0;
	private static final int xOffset = 30, yOffset = 0;
	private static int frameCounter = 0;
	
	private Main engine;
	private LwjglAWTCanvas canvas;
	
	public ModelSubframe(String name, int width, int height, final String sourcePath) {
		super(name + " ("+(++frameCounter)+')', true, true, true, true);
		
		addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameOpened(InternalFrameEvent e) { }
			
			@Override
			public void internalFrameClosing(InternalFrameEvent e) { }
			
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				--frameCounter; }
			
			@Override
			public void internalFrameIconified(InternalFrameEvent e) { }
			
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) { }
			
			@Override
			public void internalFrameActivated(InternalFrameEvent e) { }
			
			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) { }
		});
		
		setSize(width, height);
		setLocation(xStart + xOffset*frameCounter, yStart + yOffset*frameCounter);
		try {
			setSelected(true); }
		catch(java.beans.PropertyVetoException e) {
			e.printStackTrace(); }
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				engine = new Main(sourcePath);
				canvas = new LwjglAWTCanvas(engine);
				add(canvas.getCanvas()); } });
		
		setVisible(true); }
}
