package com.visualizer.userInterface;

import com.visualizer.engine.AllModels;
import com.visualizer.engine.Atom;
import com.visualizer.engine.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Set;

public class ControlPanel extends JPanel {
	private class AtomicControlPanel extends JPanel {
		AtomicControlPanel(final Atom atom) {
			setBackground(Color.LIGHT_GRAY);
			JLabel name = new JLabel(atom.name + " : ");
			add(name);
			JCheckBox box = new JCheckBox("on/off");
			box.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					atom.renderable = (e.getStateChange() == ItemEvent.SELECTED); } });
			box.setSelected(true);
			add(box);
			
		}
		
	}
	
	private int xSize;
	private int minYSize;
	
	private Controller controller;
	private AllModels models;
	
	//private Set<JPanel> buttons;
	
	public ControlPanel(AllModels models, Controller controller) {
		setBackground(Color.GRAY);
		this.controller = controller;
		this.models = models;
		System.out.println("ControlPanel::ControlPanel()");
		
		xSize = 200;
		minYSize = 5;
		for(final Atom atom: models.atoms) {
			AtomicControlPanel atomController = new AtomicControlPanel(atom);
			add(atomController);
			xSize = atomController.getWidth() > xSize ? atomController.getWidth() : xSize;
			minYSize += atomController.getHeight() + 5; }
		}
	
	public void updateBounds(int width, int height) {
		width = width<xSize ? xSize : width;
		height = height<minYSize ? minYSize : height;
		setBounds(width-xSize, 0, xSize, height);
		//for()
	}
}
