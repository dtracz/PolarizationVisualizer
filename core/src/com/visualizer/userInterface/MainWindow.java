package com.visualizer.userInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

public class MainWindow extends JFrame {
	private static MainWindow instance;
	
	private JDesktopPane desktop;
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		JMenuItem itemImport = new JMenuItem("Import...");
		itemImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					String str = selectFile(); }
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
}
