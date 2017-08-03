package com.visualizer.userInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends JFrame {
	private static MainWindow instance;
	private JMenuBar bar;
	private JDesktopPane desktop;
	
	private Map<ModelSubframe, ControlPanel> projects;
	private ModelSubframe topSubframe;
	private boolean controlPanelOn;
	private int xSize;
	private ControlPanel currControlPanel;
	
	/* - CONSTRUCTOR - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	private void createMenu() {
		bar = new JMenuBar();
		
		JMenu menuFile = new JMenu("File");
		final JMenuItem itemImport = new JMenuItem("Import...");
		itemImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					addModel(selectFile()); }
				catch(FileNotFoundException fnfe) {
					fnfe.printStackTrace(); } } });
		final JMenuItem itemExportAs = new JMenuItem("Export as...");
		final JMenuItem itemExit = new JMenuItem("Exit");
		itemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.exit(0); } });
		menuFile.add(itemImport);
		menuFile.add(itemExportAs);
		menuFile.addSeparator();
		menuFile.add(itemExit);
		bar.add(menuFile);
		
		JMenu menuView = new JMenu("View");
		final JMenuItem itemMode = new JMenuItem("Mode 2D/3D");
		itemMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(topSubframe == null) {
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "No model frame selected!", "Error", JOptionPane.ERROR_MESSAGE); }
				else {
					topSubframe.engine.changeMode(); }
				MainWindow.getInstance().validate(); }
		});
		final JMenuItem itemCamera = new JMenuItem("Set Camera");
		final JMenuItem itemControl = new JMenuItem("Show control panel");
		itemControl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				controlPanelOn = !controlPanelOn;
				if(controlPanelOn) {
					try {
						setCurrControlPanel(getControlPanel());
						itemControl.setText("Hide control panel"); }
					catch(NullPointerException npe) {
						controlPanelOn = false; } }
				else {
					setCurrControlPanel(null);
					itemControl.setText("Show control panel"); }
				MainWindow.getInstance().validate(); }
		});
		menuView.add(itemMode);
		menuView.add(itemCamera);
		menuView.add(itemControl);
		bar.add(menuView);
		
		setJMenuBar(bar); }
	
	protected MainWindow(int width, int height) {
		controlPanelOn = false;
		xSize = 240;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		desktop = new JDesktopPane();
		desktop.setBackground(Color.CYAN);
		desktop.setMaximumSize(new Dimension(400, 300));
		add(desktop, BorderLayout.CENTER);
		
		setSize(width, height);
		createMenu();
		projects = new HashMap<ModelSubframe, ControlPanel>();
		addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				if(currControlPanel != null) {
					//	currControlPanel.updateBounds(getWidth(), getHeight());
				} }
			
			@Override
			public void componentMoved(ComponentEvent e) { }
			
			@Override
			public void componentShown(ComponentEvent e) { }
			
			@Override
			public void componentHidden(ComponentEvent e) { }
		});
		setVisible(true); }
	
	public static MainWindow getInstance() {
		if(instance == null) {
			instance = new MainWindow(1280, 720); }
		return instance; }
	
	/* - SUBFRAME- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	private File selectFile() throws FileNotFoundException {
		final JFileChooser jFC = new JFileChooser();
		jFC.setCurrentDirectory(new File("."));                                             // set default Directory!!!
		int parent = jFC.showOpenDialog(new JFrame());
		File file;
		if(parent == JFileChooser.APPROVE_OPTION) {
			file = jFC.getSelectedFile(); }
		else {
			throw new FileNotFoundException("No file selected"); }
		return file; }
	
	private ModelSubframe addModel(File sourceFile) {
		ModelSubframe subframe = new ModelSubframe("subframe", 800, 600, sourceFile);
		desktop.add(subframe);
		subframe.toFront();
		return subframe; }
	
	public void setTopSubframe(ModelSubframe subframe) {
		topSubframe = subframe;
		if(controlPanelOn) {
			System.out.println("setCurrCP");
			setCurrControlPanel(getControlPanel()); }
		MainWindow.getInstance().validate(); }
		//try {
		//catch(NullPointerException npe) {
			//	try {
			//		subframe.setSelected(false); }
			//	catch(PropertyVetoException pve) {
			//		pve.printStackTrace(); } } }
	
	public void deleteSubframe(ModelSubframe subframe) {
		if(topSubframe == subframe) {
			topSubframe = null;
			setCurrControlPanel(null); }
		projects.remove(subframe); }
	
	/* - CONTROL PANEL - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	private ControlPanel getControlPanel() throws NullPointerException {
		if(topSubframe == null) {
			JOptionPane.showMessageDialog(this, "No model frame selected!", "Error", JOptionPane.ERROR_MESSAGE);
			throw new NullPointerException("No model frame selected!"); }
			//return null; }
		ControlPanel controlPanel;
		if(projects.containsKey(topSubframe)) {
			controlPanel = projects.get(topSubframe); }
		else {
			controlPanel = new ControlPanel(topSubframe, xSize);
			//controlPanel.updateBounds(getWidth(), getHeight());
			projects.put(topSubframe, controlPanel); }
		return controlPanel; }
	
	private void setCurrControlPanel(ControlPanel controlPanel) {
		if(currControlPanel != null) {
			currControlPanel.setVisible(false);
			remove(currControlPanel); }
		currControlPanel = controlPanel;
		if(currControlPanel != null) {
			add(controlPanel, BorderLayout.EAST);
			currControlPanel.setVisible(true); } }
	
}