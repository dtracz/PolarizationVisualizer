package com.visualizer.userInterface;

import com.visualizer.engine.CameraHandler;
import com.visualizer.engine.MainEngine;
import com.visualizer.engine.ModelSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;

public class SouthPanel extends JScrollPane{
	private MainEngine engine;
	private ModelSet models;
	private CameraHandler cameraHandler;
	
	private JPanel viewPanel;
	private JPanel optionsPane;
	private int ySize;
	
	private JPanel createTitle(final String name) {
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
	
	private JPanel createOpacitySlider(final String objects) {
		JPanel sliderPane = new JPanel();
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
				//MainWindow.getInstance().validate();                                                        //????
			} });
		sliderPane.add(slider);
		sliderPane.add(sliderLabel);
		return sliderPane; }
		
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

	private JButton createAxesButton() {
		JButton setAxes = new JButton("set axes");
		return setAxes; }
		
	private JCheckBox createCameraBox() {
		JCheckBox checkBox = new JCheckBox("mode 2D");
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				engine.setMode2D(e.getStateChange() == ItemEvent.SELECTED); } });
		return checkBox; }
		
	private JCheckBox createNameBox() {
		JCheckBox checkBox = new JCheckBox("Show atom names");
		checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				models.visibleLabels = e.getStateChange() == ItemEvent.SELECTED; } });
		checkBox.setSelected(true);
		return checkBox; }
		
	private JButton createCameraResetButton() {
		JButton reset = new JButton("reset");
		return reset; }
		
	private JButton createViewButton() {
		JButton setView = new JButton("set view");
		return setView; }
		
	public SouthPanel(ModelSubframe subframe, int ySize) {
		engine = subframe.engine;
		models = subframe.engine.models;
		cameraHandler = subframe.engine.cameraHandler;
		this.ySize = ySize;
		setPreferredSize(new Dimension(360, ySize));
		
		viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		optionsPane = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.insets = new Insets(0,0,0,25);
		Component nameBox = createNameBox();
		optionsPane.add(nameBox, gbc);
		gbc.gridx++;
		Component atoms = createTitle("ATOMS");
		optionsPane.add(atoms, gbc);
		gbc.gridx++;
		Component bonds = createTitle("BONDS");
		optionsPane.add(bonds, gbc);
		gbc.gridx++;
		Component axes = createTitle("AXES");
		optionsPane.add(axes, gbc);
		gbc.gridx++;
		gbc.insets.right = 0;
		Component cameraLabel = new JLabel("CAMERA");
		optionsPane.add(cameraLabel);
		gbc.gridx++;
		Component cameraBox = createCameraBox();
		optionsPane.add(cameraBox, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets.right = 25;
		Component opacitySlider = createOpacitySlider("ATOMS");
		optionsPane.add(opacitySlider, gbc);
		gbc.gridx++;
		Component atomSizer = addSizeSpinner("ATOMS");
		optionsPane.add(atomSizer, gbc);
		gbc.gridx++;
		Component bondSizer = addSizeSpinner("BONDS");
		optionsPane.add(bondSizer, gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		Component setAxes = createAxesButton();
		optionsPane.add(setAxes, gbc);
		gbc.gridx++;
		gbc.insets.right = 0;
		Component resetCamera = createCameraResetButton();
		optionsPane.add(resetCamera, gbc);
		gbc.gridx++;
		Component setView = createViewButton();
		optionsPane.add(setView, gbc);
		
		viewPanel.add(optionsPane);
		setViewportView(viewPanel); }

}