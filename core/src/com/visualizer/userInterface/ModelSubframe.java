package com.visualizer.userInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.visualizer.engine.MainEngine;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

public class ModelSubframe extends JInternalFrame {
	private static final int xStart = 0, yStart = 0;
	private static final int xOffset = 30, yOffset = 30;
	private static int frameCounter = 0;
	MainEngine engine;
	LwjglAWTCanvas canvas;
	//private GWTBridge gwtBridge; // ?!
	
	public ModelSubframe(String name, int width, int height, final File sourceFile, boolean longImport) {
		super(sourceFile.getName() + " ("+(++frameCounter)+')', true, true, true, false);
		setSize(4*width, 4*height);
		setLocation(xStart + xOffset*(frameCounter-1), yStart + yOffset*(frameCounter-1));
		
		final ModelSubframe self = this;
		addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent componentEvent) {
//				System.out.println(Gdx.graphics.getWidth() +" "+ Gdx.graphics.getHeight());
//				setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				MainWindow.getInstance().validate();
			}
			@Override
			public void componentMoved(ComponentEvent componentEvent) {
				MainWindow.getInstance().validate(); }
			@Override
			public void componentShown(ComponentEvent componentEvent) { }
			@Override
			public void componentHidden(ComponentEvent componentEvent) { }
		});
		
		addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameOpened(InternalFrameEvent e) { }
			@Override
			public void internalFrameClosing(InternalFrameEvent e) { }
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						engine.dispose(); } } );
				MainWindow.getInstance().deleteSubframe(self);
				--frameCounter; }
			@Override
			public void internalFrameIconified(InternalFrameEvent e) { }
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) { }
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				MainWindow.getInstance().setTopSubframe(self); }
			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) { }
		});
		
		try {
			setSelected(true); }
		catch(java.beans.PropertyVetoException e) {
			e.printStackTrace(); }
		
//		javax.swing.SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
				engine = new MainEngine(sourceFile, longImport);
				canvas = new LwjglAWTCanvas(engine);
				add(canvas.getCanvas());
//			} });
		setVisible(true);
	}
}
