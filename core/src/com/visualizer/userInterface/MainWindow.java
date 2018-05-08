package com.visualizer.userInterface;

import com.badlogic.gdx.Gdx;
import com.visualizer.engine.MainEngine;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends JFrame {
	private final static int[] mainWindowSizes = new int[]{1680, 1050};
	private final static int[] modelSubframeSizes = new int[]{1280, 720};
	private final static int[] northPanelSizes = new int[]{10, 105};
	private final static int[] eastPanelSizes = new int[]{245, 10};
	private final static int[] southPanelSizes = new int[]{10, 205};
	private final JScrollPane[] emptyPanels; // {east, north}
	
	public static String selfPath;
	private static MainWindow instance;
	private JMenuBar bar;
	private JDesktopPane desktop;
	
	private Map<ModelSubframe, JScrollPane[]> projects;
	public ModelSubframe topSubframe;
	private boolean showPanels; //////////////////////////////////////////////////////////////////////////////////
	private boolean showSouth;
	private JScrollPane currEastPanel;
	private JScrollPane currNorthPanel;
	private JScrollPane southPanel;
	
	/* - MENU CREATOR- - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	private JMenu createMenuFile(String name) {
		JMenu menuFile = new JMenu(name);
		
		final JMenuItem itemImport = new JMenuItem("Import...");
		itemImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					topSubframe = addModel(selectFile(), false); }
				catch(FileNotFoundException fnfe) {
					fnfe.printStackTrace(); }
//				MainWindow.getInstance().validate();
			}
		});
		
//		final JMenuItem longImport = new JMenuItem("Long import...");
//		itemImport.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ev) {
//				try {
//					topSubframe = addModel(selectFile(), true); }
//				catch(FileNotFoundException fnfe) {
//					fnfe.printStackTrace(); }
////				MainWindow.getInstance().validate();
//			}
//		});
		
		final JMenuItem itemExport = new JMenuItem("Export as png");
		itemExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					final String filename = selectDirectory();
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							topSubframe.engine.exportImage(filename); }
					}); }
				catch(FileNotFoundException fnfe) {
					fnfe.printStackTrace(); } }
		});
		
		final JMenuItem itemExit = new JMenuItem("Exit");
		itemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				System.exit(0); }
		});
		
		menuFile.add(itemImport);
//		menuFile.add(longImport);
		menuFile.add(itemExport);
		menuFile.addSeparator();
		menuFile.add(itemExit);
		return menuFile; }
	
	private JMenu createMenuView(String name) {
		JMenu menuView = new JMenu(name);
		
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
		
		final JMenuItem itemControl = new JMenuItem("Hide control panel");
		itemControl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				showPanels = !showPanels;
				if(showPanels) {
					try {
						setCurrPanels(getControlPanel());
						itemControl.setText("Hide control panel"); }
					catch(NullPointerException npe) {
						showPanels = false; } }
				else {
					setCurrPanels(null);
					itemControl.setText("Show control panel"); }
				MainWindow.getInstance().validate(); }
		});
		
		final JMenuItem itemSubroutine = new JMenuItem("Hide subroutine panel");
		itemSubroutine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				showSouth = !showSouth;
				southPanel.setVisible(showSouth);
				itemSubroutine.setText(showSouth ? "Hide subroutine panel" : "Show subroutine panel");
				MainWindow.getInstance().validate();
            }
		});
		
		menuView.add(itemMode);
		//menuView.add(itemCamera);
		menuView.add(itemControl);
		menuView.add(itemSubroutine);
		return menuView;
	}
	
	private JMenu createMenuHelp(String name) {
		JMenu menuHelp = new JMenu(name);
		
		final JMenuItem itemDoc = new JMenuItem("Documentation");
		itemDoc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (Desktop.isDesktopSupported()) {
						Desktop.getDesktop().open(new File(MainWindow.selfPath +"/documentation.pdf")); }
					else {
						JOptionPane.showMessageDialog(MainWindow.getInstance(), "Not supported on Your computer", "Error", JOptionPane.ERROR_MESSAGE); }
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(MainWindow.getInstance(), "No documentation found", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		final JMenuItem itemWeb = new JMenuItem("Website");
		itemWeb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (Desktop.isDesktopSupported()) {
						Desktop.getDesktop().browse(new URI("http://www.chemcrys.unibe.ch/PolaBer.html")); }
					else {
						JOptionPane.showMessageDialog(MainWindow.getInstance(), "Not supported on Your computer", "Error", JOptionPane.ERROR_MESSAGE); }
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(MainWindow.getInstance(), e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				} catch (URISyntaxException e1) {
					JOptionPane.showMessageDialog(MainWindow.getInstance(), e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		final JMenuItem itemRef = new JMenuItem("References");
		itemRef.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String references = "<html>"
						                          +"1. A. Krawczuk, D. Perez, P. Macchi  <i>J.Appl. Cryst.</i> (2014) <b>47</b>, 1452–1458<br>"
						                          +"&nbsp&nbsp&nbsp PolaBer : a program to calculate and visualize distributed atomic polarizabilities based on electron density partitioning<br>"
						                          +"2. A. Krawczuk, D. Perez, K. Stadnicka, P. Macchi, P. Trans.  <i>Am. Crystallogr. Assoc.</i> (2011) 1–25<br>"
						                          +"&nbsp&nbsp&nbsp Distributed atomic polarizabilities from electron density. 1. Motivations and Theory<br>"
						                          +"3. P. Macchi, A. Krawczuk  <i>Comput. Theor. Chem.</i> (2015) <b>1053</b>, 165–172<br>"
						                          +"&nbsp&nbsp&nbsp The polarizability of organometallic bond<br>"
						                          +"4. L.H.R. Dos Santos, A. Krawczuk, P. Macchi  <i>J. Phys. Chem. A</i> (2015) <b>119</b>, 3285−3298<br>"
						                          +"&nbsp&nbsp&nbsp Distributed Atomic Polarizabilities of Amino Acids and their Hydrogen-Bonded Aggregates<br>"
						                          +"</html>";
				
				JLabel label = new JLabel(references);
				label.setFont(new Font(null, Font.PLAIN, 14));
				JOptionPane.showMessageDialog(MainWindow.getInstance(), label, "References", JOptionPane.PLAIN_MESSAGE);
			}
		});
		
		menuHelp.add(itemDoc);
		menuHelp.add(itemWeb);
		menuHelp.add(itemRef);
		return menuHelp;
	}
		
	private void createMenu() {
		bar = new JMenuBar();
		JMenu menuFile = createMenuFile("File");
		bar.add(menuFile);
		//JMenu menuEdit = new JMenu("Edit");
		//bar.add(menuEdit);
		JMenu menuView = createMenuView("View");
		bar.add(menuView);
		//JMenu menuSettings = new JMenu("Settings");
		//bar.add(menuSettings);
		JMenu menuHelp = createMenuHelp("Help");
		bar.add(menuHelp);
		setJMenuBar(bar);
	}
	
	/* - CONSTRUCTOR - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	protected MainWindow(int width, int height) {
		addMouseListener(new MouseAdapter() { // show tooltips forever
			public void mouseEntered(MouseEvent me) {
				ToolTipManager.sharedInstance().setDismissDelay(0);
			}
			public void mouseExited(MouseEvent me) {
				ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
			}
		});
		
		showPanels = true;
		
		emptyPanels = new JScrollPane[2];
		JPanel east = new JPanel();
		east.setPreferredSize(new Dimension(eastPanelSizes[0], eastPanelSizes[1]));
		emptyPanels[0] = new JScrollPane(east);
		JPanel north = new JPanel();
		north.setPreferredSize(new Dimension(northPanelSizes[0], northPanelSizes[1]));
		emptyPanels[1] = new JScrollPane(north);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		desktop = new JDesktopPane();
		add(desktop, BorderLayout.CENTER);
		
		setSize(width, height);
		createMenu();
		projects = new HashMap<ModelSubframe, JScrollPane[]>();
		setVisible(true);
		setCurrPanels(emptyPanels);
		
		southPanel = new SouthPanel(southPanelSizes[0], southPanelSizes[1]);
		add(southPanel, BorderLayout.SOUTH);
		showSouth = true;
		southPanel.setVisible(showSouth);
		
		validate();
	}
	
	
	public static MainWindow getInstance() {
		if(instance == null) {
			instance = new MainWindow(mainWindowSizes[0], mainWindowSizes[1]); }
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
				return file.getName().matches(".+\\.(png|PNG)") || file.isDirectory(); }      				// not working in fact...
			@Override
			public String getDescription() {
				return "Image file (*.png)"; } });
		int result = jFC.showSaveDialog(null);
		if(result == JFileChooser.APPROVE_OPTION) {
			int homelength = System.getProperty("user.home").length();
			return jFC.getSelectedFile().getAbsolutePath().substring(homelength); }
		else {
			throw new FileNotFoundException("No file selected"); } }
	
			
	private ModelSubframe addModel(File sourceFile, boolean longImport) {
		ModelSubframe subframe = new ModelSubframe("subframe", modelSubframeSizes[0], modelSubframeSizes[1], sourceFile, longImport);
		desktop.add(subframe);
		subframe.toFront();
		return subframe;
	}
	
	public void resetTopSubframe() {
		setTopSubframe(topSubframe);
		topSubframe.setSize(modelSubframeSizes[0], modelSubframeSizes[1]);
	}
		
	public void setTopSubframe(ModelSubframe subframe) {
		topSubframe = subframe;
		if(showPanels) {
			setCurrPanels(getControlPanel()); }
		MainWindow.getInstance().validate(); }
	
		
	public void deleteSubframe(ModelSubframe subframe) {
		if(topSubframe == subframe) {
			topSubframe = null;
			setCurrPanels(showPanels ? emptyPanels : null); }
		projects.remove(subframe); }
	
	/* - CONTROL PANEL - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	private JScrollPane[] getControlPanel() throws NullPointerException {
		if(topSubframe == null) {
			return emptyPanels; }
		JScrollPane[] controlPanels;
		if(projects.containsKey(topSubframe)) {
			controlPanels = projects.get(topSubframe); }
		else {
			EastPanel eastPanel = new EastPanel(topSubframe, eastPanelSizes[0], eastPanelSizes[1]);
			NorthPanel northPanel = new NorthPanel(topSubframe, northPanelSizes[0], northPanelSizes[1]);
			controlPanels = new JScrollPane[]{eastPanel, northPanel};
			projects.put(topSubframe, controlPanels); }
		return controlPanels; }
	
	/**
	 *  removes current panels if exist
	 *  sets new panel, or nulls if panels should be hide
	 *  if there are new panels, shows them
	 */
	private void setCurrPanels(JScrollPane[] controlPanels) {
		if(currEastPanel != null) {
			currNorthPanel.setVisible(false);
			remove(currNorthPanel);
			currEastPanel.setVisible(false);
			remove(currEastPanel); }
		if(controlPanels == null) {
			currEastPanel = null;
			currNorthPanel = null; }
		else {
			currEastPanel = controlPanels[0];
			currNorthPanel = controlPanels[1]; }
		if(currEastPanel != null) {
			add(currNorthPanel, BorderLayout.NORTH);
			currNorthPanel.setVisible(true);
			add(currEastPanel, BorderLayout.EAST);
			currEastPanel.setVisible(true); } }

}