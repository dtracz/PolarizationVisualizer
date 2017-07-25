package com.visualizer.userInterface;

import com.visualizer.engine.AllModels;
import com.visualizer.engine.Atom;
import com.visualizer.engine.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel{
	private int xSize;
	private int minYSize;
	
	private Controller controller;
	private AllModels models;
	
	public ControlPanel(AllModels models, Controller controller) {
		setBackground(Color.GRAY);
		xSize = 200;
		minYSize = 0;
		this.controller = controller;
		this.models = models;
		System.out.println("xxx");
		
		int curr = 10;
		/*for(final Atom atom: models.atoms) {
			JButton button = new JButton(atom.name + "on/off");
			button.setBounds(50, curr, 100, 30);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					atom.renderable = !atom.renderable; } });
			curr += 40; } */
	}
	
	public void updateBounds(int width, int height) {
		width = width<xSize ? xSize : width;
		height = height<minYSize ? minYSize : height;
		setBounds(width-xSize, 0, xSize, height); }
}
