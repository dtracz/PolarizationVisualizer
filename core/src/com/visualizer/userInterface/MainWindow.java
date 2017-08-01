package com.visualizer.userInterface;

import com.visualizer.engine.Controller;

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
import java.util.concurrent.TimeUnit;

public class MainWindow extends JFrame {
	private static MainWindow instance;
	
	private JDesktopPane desktop;
	
	private Map<ModelSubframe, ControlPanel> projects;
	private ModelSubframe topSubframe;
	private ControlPanel currControlPanel;
	//private Controller currController;
	
	private String selectFile() throws FileNotFoundException {
		final JFileChooser jFC = new JFileChooser();
		jFC.setCurrentDirectory(new File("."));                                             // set default Directory!!!
		int parent = jFC.showOpenDialog(new JFrame());
		File file;
		if(parent == JFileChooser.APPROVE_OPTION) {
			file = jFC.getSelectedFile(); }
		else {
			throw new FileNotFoundException("No file selected"); }
		return file.getAbsolutePath(); }
	
	private void createMenu() {
		JMenuBar bar = new JMenuBar();
		
		JMenu menuFile = new JMenu("File");
		JMenuItem itemImport = new JMenuItem("Import...");
		itemImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					addModel(selectFile()); }
				catch(FileNotFoundException fnfe) {
					fnfe.printStackTrace(); } } });
		JMenuItem itemExportAs = new JMenuItem("Export as...");
		JMenuItem itemExit = new JMenuItem("Exit");
		itemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.exit(0); } });
		menuFile.add(itemImport);
		menuFile.add(itemExportAs);
		menuFile.addSeparator();
		menuFile.add(itemExit);
		bar.add(menuFile);
		
		JMenu menuView = new JMenu("View");
		JMenuItem itemMode = new JMenuItem("Mode 2D/3D");
		itemMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			
			}
		});
		JMenuItem itemCamera = new JMenuItem("Set Camera");
		JMenuItem itemControl = new JMenuItem("Show control panel");
		itemControl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				if(topSubframe != null) {
				
				}
			}
		});
		menuView.add(itemMode);
		menuView.add(itemCamera);
		menuView.add(itemControl);
		bar.add(menuView);
		
		setJMenuBar(bar);
	}
	
	
	protected MainWindow(int width, int height) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		desktop = new JDesktopPane();
		setContentPane(desktop);
		setSize(width, height);
		createMenu();
		projects = new HashMap<ModelSubframe, ControlPanel>();
		addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				if(currControlPanel != null) {
					currControlPanel.updateBounds(getWidth(), getHeight()); } }
			
			@Override
			public void componentMoved(ComponentEvent e) { }
			
			@Override
			public void componentShown(ComponentEvent e) { }
			
			@Override
			public void componentHidden(ComponentEvent e) { }
		});
		setVisible(true);
	}
	
	public static MainWindow getInstance() {
		if(instance == null) {
			instance = new MainWindow(1280, 720); }
		return instance; }
		
	public ModelSubframe addModel(String sourcePath) {
		ModelSubframe subframe = new ModelSubframe("subframe", 800, 600, sourcePath);
		desktop.add(subframe);
		return subframe; }
		
	public ControlPanel addControlPanel(ModelSubframe subframe) {
		ControlPanel controlPanel;
		if(projects.containsKey(subframe)) {
			controlPanel = projects.get(subframe); }
		else {
			controlPanel = new ControlPanel(subframe);
			add(controlPanel);
			controlPanel.updateBounds(getWidth(), getHeight());
			projects.put(subframe, controlPanel); }
		return controlPanel; }

	public void setTopSubframe(ModelSubframe subframe) {
		topSubframe = subframe; }
	
	public void setCurrControlPanel(ModelSubframe subframe) {
		if(currControlPanel != null) {
			currControlPanel.setVisible(false); }
		currControlPanel = projects.get(subframe);
		currControlPanel.setVisible(true); }
		
	public void setCurrControlPanel(ControlPanel controlPanel) {
		if(currControlPanel != null) {
			currControlPanel.setVisible(false); }
		currControlPanel = controlPanel;
		currControlPanel.setVisible(true);
		}
	
	
}
