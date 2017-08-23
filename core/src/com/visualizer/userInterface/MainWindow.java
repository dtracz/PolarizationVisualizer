package com.visualizer.userInterface;

import com.badlogic.gdx.Gdx;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends JFrame {
	private static MainWindow instance;
	private JMenuBar bar;
	private JDesktopPane desktop;
	
	private Map<ModelSubframe, JScrollPane[]> projects;
	private ModelSubframe topSubframe;
	private boolean controlPanelOn; //////////////////////////////////////////////////////////////////////////////////
	private int xSize;
	private int ySize;
	private ControlPanel currControlPanel;
	private SouthPanel currSouthPanel;
	
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
		final JMenuItem itemExport = new JMenuItem("Export as png");
		itemExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					final String filename = selectDirectory();
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
								topSubframe.engine.exportImage(filename); } }); }
				catch(FileNotFoundException fnfe) {
					fnfe.printStackTrace(); } } } );
		final JMenuItem itemExit = new JMenuItem("Exit");
		itemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.exit(0); } });
		menuFile.add(itemImport);
		menuFile.add(itemExport);
		menuFile.addSeparator();
		menuFile.add(itemExit);
		bar.add(menuFile);
		
		JMenu menuEdit = new JMenu("Edit");
		bar.add(menuEdit);
		
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
						setCurrPanels(getControlPanel());
						itemControl.setText("Hide control panel"); }
					catch(NullPointerException npe) {
						controlPanelOn = false; } }
				else {
					setCurrPanels(null);
					itemControl.setText("Show control panel"); }
				MainWindow.getInstance().validate(); }
		});
		menuView.add(itemMode);
		menuView.add(itemCamera);
		menuView.add(itemControl);
		bar.add(menuView);
		
		JMenu menuSettings = new JMenu("Settings");
		bar.add(menuSettings);
		
		setJMenuBar(bar); }
	
	protected MainWindow(int width, int height) {
		controlPanelOn = false;
		xSize = 240;
		ySize = 95;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		desktop = new JDesktopPane();
		//desktop.setBackground(Color.CYAN);
		desktop.setMaximumSize(new Dimension(400, 300));
		add(desktop, BorderLayout.CENTER);
		
		setSize(width, height);
		createMenu();
		projects = new HashMap<ModelSubframe, JScrollPane[]>();
		/*addComponentListener(new ComponentListener() {
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
		});*/
		setVisible(true); }
	
	public static MainWindow getInstance() {
		if(instance == null) {
			instance = new MainWindow(1280, 720); }
		return instance; }
	
	/* - SUBFRAME- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	private File selectFile() throws FileNotFoundException {
		final JFileChooser jFC = new JFileChooser();
		jFC.setCurrentDirectory(new File("."));											// set default Directory!!!
		int result = jFC.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION) {
			return jFC.getSelectedFile(); }
		else {
			throw new FileNotFoundException("No file selected"); } }
		
	private String selectDirectory() throws FileNotFoundException {
		final JFileChooser jFC = new JFileChooser(new File("."));						// set default Directory!!!
		jFC.setDialogTitle("Save as..");
		jFC.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().matches(".+\\.[(png)(PNG)]") || file.isDirectory(); }      				// not working in fact...
			@Override
			public String getDescription() {
				return "Image file (*.png)"; } });
		int result = jFC.showSaveDialog(null);
		if(result == JFileChooser.APPROVE_OPTION) {
			int homelength = System.getProperty("user.home").length();
			return jFC.getSelectedFile().getAbsolutePath().substring(homelength); }
		else {
			throw new FileNotFoundException("No file selected"); } }
	
	private ModelSubframe addModel(File sourceFile) {
		ModelSubframe subframe = new ModelSubframe("subframe", 800, 600, sourceFile);
		desktop.add(subframe);
		subframe.toFront();
		return subframe; }
	
	public void setTopSubframe(ModelSubframe subframe) {
		topSubframe = subframe;
		if(controlPanelOn) {
			System.out.println("setCurrCP");
			setCurrPanels(getControlPanel()); }
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
			setCurrPanels(null); }
		projects.remove(subframe); }
	
	/* - CONTROL PANEL - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	private JScrollPane[] getControlPanel() throws NullPointerException {
		if(topSubframe == null) {
			JOptionPane.showMessageDialog(this, "No model frame selected!", "Error", JOptionPane.ERROR_MESSAGE);
			throw new NullPointerException("No model frame selected!"); }
			//return null; }
		JScrollPane[] controlPanels;
		if(projects.containsKey(topSubframe)) {
			controlPanels = projects.get(topSubframe); }
		else {
			ControlPanel controlPanel = new ControlPanel(topSubframe, xSize);
			SouthPanel southPanel = new SouthPanel(topSubframe, ySize);
			controlPanels = new JScrollPane[]{controlPanel, southPanel};
			projects.put(topSubframe, controlPanels); }
		return controlPanels; }
	
	/**
	 *  removes current panels if exist
	 *  sets new panel, or nulls if panels should be hide
	 *  if there are new panels, shows them
	 */
	private void setCurrPanels(JScrollPane[] controlPanels) {
		if(currControlPanel != null) {
			currSouthPanel.setVisible(false);
			remove(currSouthPanel);
			currControlPanel.setVisible(false);
			remove(currControlPanel); }
		if(controlPanels != null) {
			currControlPanel = (ControlPanel)controlPanels[0];
			currSouthPanel = (SouthPanel)controlPanels[1]; }
		else {
			currControlPanel = null;
			currSouthPanel = null; }
		if(currControlPanel != null) {
			add(currSouthPanel, BorderLayout.SOUTH);
			currSouthPanel.setVisible(true);
			add(currControlPanel, BorderLayout.EAST);
			currControlPanel.setVisible(true); } }

}