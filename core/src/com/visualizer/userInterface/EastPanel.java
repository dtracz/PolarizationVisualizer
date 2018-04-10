package com.visualizer.userInterface;

import com.visualizer.engine.Atom;
import com.visualizer.engine.Bond;
import com.visualizer.engine.SpatialObject;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

public class EastPanel extends JScrollPane {
	private final ArrayList<Atom> atoms;
	private final ArrayList<Bond> bonds;
	private SpatialObject selected = null;
	
	private JPanel viewPanel;
	private JPanel windowsPanel;
	private int xSize;
	
	private final Component atomTable;
	private final Component bondTable;
	private final JTextArea textArea;
	
	private JTable createAtomTable(final ArrayList<Atom> atoms) {
		Object columnNames[] = new Object[] {"atom", "at.", "lbl", "ax."};
		Object values[][] = new Object[atoms.size()][];
		int i = 0;
		for(Atom atom: atoms) {
			values[i++] = new Object[]{atom.name, true, true, true}; }
		
		/** creates model: set type of columns' values and whats editable */
		DefaultTableModel model = new DefaultTableModel(values, columnNames) {
			@Override
			public Class<?> getColumnClass(int column) {
				switch(column) {
					case 0:
						return String.class;
					case 1:
						return Boolean.class;
					case 2:
						return Boolean.class;
					case 3:
						return Boolean.class;
					default:
						return String.class; } }
			@Override
			public boolean isCellEditable(int row, int column) {
				return column != 0; }
		};
		
		/** creates table from model */
		final JTable table = new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension( 180, 120));
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		
		/** set renderer */
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		renderer.setFont(renderer.getFont().deriveFont(Font.BOLD));                     // not working :/
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);

		/** add change listener to model (uses table so needs to be implement after creating) */
		table.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent event) {
				int i = event.getColumn();
				int j = event.getFirstRow();
				if(i == 1) {
					atoms.get(j).setRenderable((Boolean)table.getValueAt(j, i)); }
				else if(i == 2) {
					atoms.get(j).visibleLabel = (Boolean)table.getValueAt(j, i); }
				else if(i == 3) {
					atoms.get(j).visibleAxes = (Boolean)table.getValueAt(j, i); } }
		});
		
		/** set selection listener */
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event) {
				int row = table.getSelectedRow();
				String description = atoms.get(row).getDescription();
				textArea.setText(description);
				selected = atoms.get(row); }
		});
		return table; }
	
		
	private JTable createBondTable(final ArrayList<Bond> bonds) {
		Object columnNames[] = new Object[] {"bond", "visible", "dashed"};
		Object values[][] = new Object[bonds.size()][];
		int i = 0;
		for(Bond bond: bonds) {
			values[i++] = new Object[]{bond.getAtomsNames(), true, bond.ifDashed()};
		}
		
		/** creates model: set type of columns' values and whats editable */
		DefaultTableModel model = new DefaultTableModel(values, columnNames) {
			@Override
			public Class<?> getColumnClass(int column) {
				switch(column) {
					case 0:
						return String.class;
					case 1:
						return Boolean.class;
					case 2:
						return Boolean.class;
					default:
						return String.class; } }
			@Override
			public boolean isCellEditable(int row, int column) {
				return column != 0; }
		};
		
		/** creates table from model */
		final JTable table = new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension( 180, 120));
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		
		/** set renderer */
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		renderer.setFont(renderer.getFont().deriveFont(Font.BOLD));                     // not working :/
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);
		
		/** add change listener to model (uses table so needs to be implement after creating) */
		table.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent event) {
				int i = event.getColumn();
				int j = event.getFirstRow();
				if(i == 1) {
					bonds.get(j).setRenderable((Boolean)table.getValueAt(j, i)); }
				else if(i == 2) {
					bonds.get(j).stripe((Boolean)table.getValueAt(j, i)); }
			}
		});
		
		/** set selection listener */
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event) {
				int row = table.getSelectedRow();
				String description = bonds.get(row).getDescription();
				textArea.setText(description);
				selected = null; }
		});
		return table; }
	
	private JPanel createOpacitySlider() {
		JPanel sliderPane = new JPanel();
		final JSlider slider = new JSlider(0, 100, 100);
		slider.setPreferredSize(new Dimension(165, 30));
		slider.setMajorTickSpacing(50);
		slider.setMinorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(false);
		final JLabel sliderLabel = new JLabel("1.00");
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				sliderLabel.setText(String.format(Locale.ROOT,"%.2f", (float)slider.getValue()/100));
				try {
					selected.changeOpacity((float)slider.getValue()/100); }
				catch(NullPointerException npe) { }
				//MainWindow.getInstance().validate();                                                        //????
			} });
		sliderPane.add(slider);
		sliderPane.add(sliderLabel);
		return sliderPane; }
		
	public EastPanel(ModelSubframe subframe, int xSize, int ySize) {
		atoms = subframe.engine.models.atoms;
		bonds = subframe.engine.models.bonds;
		this.xSize = xSize;
		setPreferredSize(new Dimension(xSize, ySize));
		
		viewPanel = new JPanel();
		windowsPanel = new JPanel(new GridBagLayout());
		
		JPanel textAreaPane = new JPanel(new GridBagLayout());
		textAreaPane.setBorder(BorderFactory.createTitledBorder("Object info"));
		textArea = new JTextArea(7, 16);
		textArea.setEditable(false);
		textAreaPane.add(textArea);
		atomTable = createAtomTable(atoms);
		bondTable = createBondTable(bonds);
		JPanel opacitySlider = createOpacitySlider();
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(15,9,5,9);
		gbc.gridx = 0;
		gbc.gridy = 0;
		windowsPanel.add(new JScrollPane(atomTable), gbc);
		gbc.gridy++;
		windowsPanel.add(opacitySlider, gbc);
		gbc.gridy++;
		windowsPanel.add(new JScrollPane(bondTable), gbc);
		gbc.gridy++;
		windowsPanel.add(new JScrollPane(textAreaPane), gbc);
		
		viewPanel.add(windowsPanel);
		setViewportView(viewPanel); }
	
}
