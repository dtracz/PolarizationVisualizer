package com.visualizer.userInterface;

import com.visualizer.engine.AllModels;
import com.visualizer.engine.Atom;
import com.visualizer.engine.Controller;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ControlPanel extends JScrollPane {
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
			JButton params = new JButton("param.");
			add(params);
		}
		
	}
	
	private JPanel viewPanel;
	
	private int xSize;
	private int minYSize;
	
	private Controller controller;
	private AllModels models;
	
	//private Set<JPanel> buttons;
	
	public ControlPanel(AllModels allModels, Controller controller) {
		viewPanel = new JPanel(new GridLayout(allModels.atoms.size()+1, 1));
		
		viewPanel.setBackground(Color.GRAY);
		this.controller = controller;
		this.models = allModels;
		System.out.println("ControlPanel::ControlPanel()");
		
		xSize = 220;
		minYSize = 40;
		
		//*
		JPanel opacityPanel = new JPanel();
		//opacityPanel.setBackground(Color.WHITE);
		JLabel name = new JLabel("Opacity:");
		opacityPanel.add(name);
		final JSlider slider = new JSlider(0, 100, 100);
		slider.setPreferredSize(new Dimension(100, 30));
		slider.setMajorTickSpacing(50);
		slider.setMinorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(false);
		final JLabel sliderLabel = new JLabel(" 1.00");
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				sliderLabel.setText(String.format(Locale.ROOT," %.2f", (float)slider.getValue()/100));
				for(Atom atom: models.atoms) {
					atom.changeOpacity((float)slider.getValue()/100); }
			} });
		opacityPanel.add(slider);
		opacityPanel.add(sliderLabel);
		opacityPanel.setPreferredSize(new Dimension(200, 40));
		viewPanel.add(opacityPanel);
		//*/
		for(Atom atom: models.atoms) {
			AtomicControlPanel atomController = new AtomicControlPanel(atom);
			atomController.setPreferredSize(new Dimension(200, 40));
			viewPanel.add(atomController);
			//xSize = atomController.getWidth() > xSize ? atomController.getWidth() : xSize;
			minYSize += 40; }
		//setPreferredSize(new Dimension(xSize, minYSize));
		setViewportView(viewPanel);
		}
	
	public void updateBounds(int width, int height) {
		width = width<xSize ? xSize : width;
		height = height<minYSize ? minYSize : height;
		setBounds(width-xSize, 0, xSize, height-20);
		//for()
	}
	
	private void setOpacityAll(float opacity) {
		for(Atom atom: models.atoms) {
			atom.changeOpacity(opacity/100);
		}
	}
}
