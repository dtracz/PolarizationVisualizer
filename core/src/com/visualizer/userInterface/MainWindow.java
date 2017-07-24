package com.visualizer.userInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

public class MainWindow extends JFrame {
	private static MainWindow instance;
	
	private JDesktopPane desktop;
	
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
		menuView.add(itemMode);
		JMenuItem itemCamera = new JMenuItem("set Camera");
		menuView.add(itemCamera);
		bar.add(menuView);
		
		setJMenuBar(bar);
	}
	
	protected MainWindow(int width, int height) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		desktop = new JDesktopPane();
		setContentPane(desktop);
		
		setSize(width, height);
		createMenu();
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setBackground(Color.GRAY);
		settingsPanel.setBounds((int)(width*3/4), 0, (int)(width*1/4), height);
		add(settingsPanel);
		
		setVisible(true);
	}
	
	public static MainWindow getInstance() {
		if(instance == null) {
			instance = new MainWindow(1280, 720); }
		return instance; }
		
	public void addModel(String sourcePath) {
		ModelSubframe subframe = new ModelSubframe("subframe", 800, 600, sourcePath);
		desktop.add(subframe);
	}

}
