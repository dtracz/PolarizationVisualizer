package com.visualizer.userInterface;

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
	private LwjglAWTCanvas canvas;
	
	
	public ModelSubframe(String name, int width, int height, final File sourceFile) {
		super(sourceFile.getName() + " ("+(++frameCounter)+')', true, true, true, false);
		
		final ModelSubframe self = this;
		addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				MainWindow.getInstance().validate(); }
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
				engine.dispose();
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
		
		setSize(width, height);
		setLocation(xStart + xOffset*(frameCounter-1), yStart + yOffset*(frameCounter-1));
		try {
			setSelected(true); }
		catch(java.beans.PropertyVetoException e) {
			e.printStackTrace(); }
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				engine = new MainEngine(sourceFile);
				canvas = new LwjglAWTCanvas(engine);
				add(canvas.getCanvas()); } });
		setVisible(true); }
}
