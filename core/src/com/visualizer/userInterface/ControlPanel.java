package com.visualizer.userInterface;

import com.visualizer.engine.Atom;
import com.visualizer.engine.MainEngine;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;

public class ControlPanel extends JScrollPane {
	private MainEngine engine;
	
	private JPanel viewPanel;
	private int xSize;
	
	private JPanel addAtomController(final Atom atom) {
		JPanel atomicControlPanel = new JPanel();
		atomicControlPanel.setBackground(Color.LIGHT_GRAY);
		JLabel name = new JLabel(atom.name + " : ");
		atomicControlPanel.add(name);
		JCheckBox checkBox = new JCheckBox("on/off");
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				atom.renderable = (e.getStateChange() == ItemEvent.SELECTED); } });
		checkBox.setSelected(true);
		atomicControlPanel.add(checkBox);
		JButton params = new JButton("param.");
		atomicControlPanel.add(params);
		return atomicControlPanel; }
	
	private JPanel addOpacityPanel() {
		JPanel opacityPanel = new JPanel();
		JLabel name = new JLabel("Opacity:");
		opacityPanel.add(name);
		final JSlider slider = new JSlider(0, 100, 100);
		slider.setPreferredSize(new Dimension(100, 30));
		slider.setMajorTickSpacing(50);
		slider.setMinorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(false);
		final JLabel sliderLabel = new JLabel("1.00");
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				sliderLabel.setText(String.format(Locale.ROOT,"%.2f", (float)slider.getValue()/100));
				for(Atom atom: engine.models.atoms) {
					atom.changeOpacity((float)slider.getValue()/100); }
				MainWindow.getInstance().validate(); } });
		opacityPanel.add(slider);
		opacityPanel.add(sliderLabel);
		opacityPanel.setPreferredSize(new Dimension(220, 40));
		return opacityPanel; }
	
	public ControlPanel(ModelSubframe subframe, int xSize) {
		engine = subframe.engine;
		this.xSize = xSize;
		
		viewPanel = new JPanel();
		viewPanel.setBackground(Color.GRAY);
		
		Box box = Box.createVerticalBox();
		box.add(addOpacityPanel());

		for(Atom atom: engine.models.atoms) {
			box.add(addAtomController(atom)); }
		
		viewPanel.setPreferredSize(new Dimension(xSize, engine.models.getNumberOfAtoms()*40 + 40));
		viewPanel.add(box);
		setViewportView(viewPanel); }
	
	/*public void updateBounds(int width, int height) {
		width = width<xSize ? xSize : width;
		//height = height<minYSize ? minYSize : height;
		setBounds(width-xSize, 0, xSize, height-20); }
	//*/
}
