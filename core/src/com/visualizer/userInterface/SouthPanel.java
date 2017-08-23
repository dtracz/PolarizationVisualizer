package com.visualizer.userInterface;

import com.visualizer.engine.ModelSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;

public class SouthPanel extends JScrollPane{
	private ModelSet models;
	//private MainEngine engine;
	
	private JPanel viewPanel;
	private int ySize;
	
	private JPanel addTitle(final String name) {
		JPanel namePanel = new JPanel();
		JLabel title = new JLabel(name+" ");
		namePanel.add(title);
		JCheckBox checkBox = new JCheckBox("show/hide");
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				models.show(e.getStateChange() == ItemEvent.SELECTED, name); } });
		checkBox.setSelected(true);
		namePanel.add(checkBox);
		return namePanel; }
	
	private JPanel addOpacitySlider(final String objects) {
		JPanel panel = new JPanel(new BorderLayout());
		
		JCheckBox checkBox = new JCheckBox("Show atom names");
		checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				models.visibleLabels = e.getStateChange() == ItemEvent.SELECTED; } });
		checkBox.setSelected(true);
		panel.add(checkBox, BorderLayout.NORTH);
		
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
				models.changeOpacity((float)slider.getValue()/100, objects);
				MainWindow.getInstance().validate();                                                        //????
			} });
		panel.add(slider, BorderLayout.CENTER);
		panel.add(sliderLabel, BorderLayout.EAST);
		return panel; }
		
	private JPanel addSizeSpinner(final String objects) {
		JPanel sizePanel = new JPanel();
		JLabel label = new JLabel("Size factor: ");
		sizePanel.add(label);
		final JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 0, 10, 0.05));
		try {
			JFormattedTextField textField = ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();
			textField.setColumns(4);
			//textField.setFont(textField.getFont().deriveFont(12f));
			textField.setHorizontalAlignment(JTextField.RIGHT); }
		catch(Exception e) {
			e.printStackTrace(); }
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				models.scale(((Double)spinner.getValue()).floatValue(), objects); } });
		if(objects.equals("ATOMS")) {
			spinner.setValue(0.25); }
		sizePanel.add(spinner);
		return sizePanel; }
	
	private Box addControllers(final String name) {
		Box box = Box.createVerticalBox();
		JPanel title = addTitle(name);
		box.add(title);
		JPanel sizeSpinner = addSizeSpinner(name);
		box.add(sizeSpinner);
		return box; }
	
	public SouthPanel(ModelSubframe subframe, int ySize) {
		models = subframe.engine.models;
		//engine = subframe.engine;
		this.ySize = ySize;
		
		viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//viewPanel.setBackground(Color.LIGHT_GRAY);
		setPreferredSize(new Dimension(450, ySize));
		
		Box mainBox = Box.createHorizontalBox();
			Box box = Box.createVerticalBox();
				box.add(Box.createRigidArea(new Dimension(0,5)));
				JPanel panel = addOpacitySlider("ATOMS");
					box.add(panel);
				mainBox.add(box);
				mainBox.add(Box.createRigidArea(new Dimension(30,0)));
			Box atomsController = addControllers("ATOMS");
				mainBox.add(atomsController);
				mainBox.add(Box.createRigidArea(new Dimension(30,0)));
			Box bondsController = addControllers("BONDS");
				mainBox.add(bondsController);
			viewPanel.add(mainBox);
		
		setViewportView(viewPanel); }

}