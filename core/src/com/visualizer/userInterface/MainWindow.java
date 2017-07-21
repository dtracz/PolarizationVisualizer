package com.visualizer.userInterface;

import javax.swing.*;

public class MainWindow extends JFrame {
	private static MainWindow instance;
	
	private JDesktopPane desktop;
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		JMenuItem itemImport = new JMenuItem("Import...");
		JMenuItem itemExportAs = new JMenuItem("Export as...");
		JMenuItem itemExit = new JMenuItem("Exit");
		menuFile.add(itemImport);
		menuFile.add(itemExportAs);
		menuFile.addSeparator();
		menuFile.add(itemExit);
		
		setJMenuBar(menuBar);
	}
	
	protected MainWindow(int width, int height) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		desktop = new JDesktopPane();
		setContentPane(desktop);
		
		setSize(width, height);
		createMenu();
		setVisible(true);
	}
	
	public static MainWindow getInstance() {
		if(instance == null) {
			instance = new MainWindow(1280, 720); }
		return instance; }
		
	public void addModel() {
		ModelSubframe subframe1 = new ModelSubframe("subframe_", 800, 600);
		desktop.add(subframe1);
	}
}
