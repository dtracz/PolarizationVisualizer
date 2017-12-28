package com.visualizer.userInterface;

import com.badlogic.gdx.math.Vector3;
import com.visualizer.engine.Atom;
import com.visualizer.engine.CameraHandler;
import com.visualizer.engine.MainEngine;
import com.visualizer.engine.ModelSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;
import java.util.NoSuchElementException;

public class NorthPanel extends JScrollPane{
	private final static String floatMatch = "-?\\d+\\.?\\d*";
	private final static String atomMatch = "[A-Z][a-z]?\\d*";
	private final Vector3 position = new Vector3();
	private final Vector3 position2 = new Vector3();
	private final Vector3 position3 = new Vector3();
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
		
	private JPanel createSizeSpinner(final String objects) {
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
			spinner.setValue(0.2); }
		sizePanel.add(spinner);
		return sizePanel; }

	private JPanel createAxesSetter() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("set pos:");
		
		final JTextField textField = new JTextField(7);
		textField.setMinimumSize(new Dimension(70, 20));
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				String input = textField.getText();
				position.set(0,0,0);
				if(input.matches("[ ]*" +atomMatch+ "[ ]*")) {
					Atom atom = null;
					try {
						atom = models.getAtom(input); }
					catch(NoSuchElementException nsee) {
						JOptionPane.showMessageDialog(MainWindow.getInstance(), "No such atom", "Error", JOptionPane.ERROR_MESSAGE);
						return; }
					position.set(atom.getCenter(position)); }
				else if(input.matches("[ ]*" +floatMatch+ "[;, ]+" +floatMatch+ "[;, ]+" +floatMatch+ "[;, ]*")) {
					String[] sPos = input.split("[;, ]+");
					position.set(Float.parseFloat(sPos[0]), Float.parseFloat(sPos[1]), Float.parseFloat(sPos[2])); }
				else {
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "Cannot resolve input", "Error", JOptionPane.ERROR_MESSAGE);
					return; }
				models.setAxesPosition(position);
				textField.setText(""); }
		});
		
		panel.add(label);
		panel.add(textField);
		return panel; }
		
	private JCheckBox createCameraBox() {
		JCheckBox checkBox = new JCheckBox("mode 2D");
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				engine.setMode2D(e.getStateChange() == ItemEvent.SELECTED); } });
		return checkBox; }
		
	private JCheckBox createNameBox() {
		JCheckBox checkBox = new JCheckBox("Show atom labels");
		checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				models.visibleLabels = e.getStateChange() == ItemEvent.SELECTED; } });
		checkBox.setSelected(true);
		return checkBox; }
		
	private JButton createResetUpButton() {
		JButton reset = new JButton("res. up");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				cameraHandler.setUpAxis(Vector3.Z); }
		});
		return reset; }
		
	private JButton createResetCenterButton() {
		JButton reset = new JButton("res. cent.");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				position.set(models.getMoleculeCenter());
				cameraHandler.lookAt(position); }
		});
		return reset; }
	
	private JPanel createLookAt() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("look at:      ");
		
		final JTextField textField = new JTextField(9);
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				String input = textField.getText();
				if(input.matches("[ ]*" +floatMatch+ "[;, ]+" +floatMatch+ "[;, ]+" +floatMatch+ "[;, ]*")) {
					String[] sPos = input.split("[;, ]+");
					position.set(Float.parseFloat(sPos[0]), Float.parseFloat(sPos[1]), Float.parseFloat(sPos[2]));
					cameraHandler.lookAt(position);
					textField.setText(""); }
				else if(input.matches("[ ]*" +atomMatch+ "[;, ]+" +atomMatch+ "[;, ]+" +atomMatch+ "[;, ]*")) {
					String[] atoms = input.split("[;, ]+");
					Vector3 at1pos = models.getAtom(atoms[0]).getCenter(position.set(0,0,0));
					Vector3 at2pos = models.getAtom(atoms[1]).getCenter(position2.set(0,0,0));
					Vector3 at3pos = models.getAtom(atoms[2]).getCenter(position3.set(0,0,0));
					try {
						cameraHandler.lookAt(at1pos, at2pos, at3pos);
						textField.setText(""); }
					catch(IllegalArgumentException iae) {
						JOptionPane.showMessageDialog(MainWindow.getInstance(), iae.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); } }
				else {
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "Cannot resolve input", "Error", JOptionPane.ERROR_MESSAGE); } }
		});
		
		panel.add(label);
		panel.add(textField);
		return panel; }
		
	private JPanel createLookAlong() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("look along:");
		
		final JTextField textField = new JTextField(9);
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				String input = textField.getText();
				if(input.matches("[ ]*" +floatMatch+ "[;, ]+" +floatMatch+ "[;, ]+" +floatMatch+ "[;, ]*")) {
					String[] sPos = input.split("[;, ]+");
					position.set(Float.parseFloat(sPos[0]), Float.parseFloat(sPos[1]), Float.parseFloat(sPos[2]));
					cameraHandler.lookAlong(position);
					textField.setText(""); }
				else if(input.matches("[ ]*" +atomMatch+ "[;, ]+" +atomMatch+ "[;, ]*")) {
					String[] atoms = input.split("[;, ]+");
					Vector3 at1pos = models.getAtom(atoms[0]).getCenter(position.set(0,0,0));
					Vector3 at2pos = models.getAtom(atoms[1]).getCenter(position2.set(0,0,0));
					try {
						cameraHandler.lookAlong(at1pos, at2pos);
						textField.setText(""); }
					catch(IllegalArgumentException iae) {
						JOptionPane.showMessageDialog(MainWindow.getInstance(), iae.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); } }
				else {
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "Cannot resolve input", "Error", JOptionPane.ERROR_MESSAGE); } }
		});
		
		panel.add(label);
		panel.add(textField);
		return panel; }
		
	public NorthPanel(ModelSubframe subframe, int xSize, int ySize) {
		engine = subframe.engine;
		models = subframe.engine.models;
		cameraHandler = subframe.engine.cameraHandler;
		this.ySize = ySize;
		setPreferredSize(new Dimension(xSize, ySize));
		
		viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		optionsPane = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		int sideSpace = 25;
		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.insets = new Insets(0,0,0, sideSpace);
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
		gbc.gridx++;
		gbc.insets.left = sideSpace/2;
		Component lookAt = createLookAt();
		optionsPane.add(lookAt, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets.set(0,0,0, sideSpace);
		Component opacitySlider = createOpacitySlider("ATOMS");
		optionsPane.add(opacitySlider, gbc);
		gbc.gridx++;
		Component atomSizer = createSizeSpinner("ATOMS");
		optionsPane.add(atomSizer, gbc);
		gbc.gridx++;
		Component bondSizer = createSizeSpinner("BONDS");
		optionsPane.add(bondSizer, gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		Component setAxes = createAxesSetter();
		optionsPane.add(setAxes, gbc);
		gbc.gridx++;
		gbc.insets.right = 0;
		Component resetCamera = createResetUpButton();
		optionsPane.add(resetCamera, gbc);
		gbc.gridx++;
		Component setView = createResetCenterButton();
		optionsPane.add(setView, gbc);
		gbc.gridx++;
		gbc.insets.left = sideSpace/2;
		Component lookAlong = createLookAlong();
		optionsPane.add(lookAlong, gbc);
		
		viewPanel.add(optionsPane);
		setViewportView(viewPanel); }

}