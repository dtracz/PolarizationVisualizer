package com.visualizer.userInterface;

import com.visualizer.engine.Atom;
import com.visualizer.engine.Bond;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class EastPanel extends JScrollPane {
	private final ArrayList<Atom> atoms;
	private final ArrayList<Bond> bonds;
	
	private JPanel viewPanel;
	private int xSize;
	
	private final Component atomTable;
	//private final Component bondTable;
	private final JTextArea textArea;
	
	private JTable createAtomTable(final ArrayList<Atom> atoms) {
		Object columnNames[] = new Object[] {" ", "at.", "lbl"};
		Object values[][] = new Object[atoms.size()][];
		int i = 0;
		for(Atom atom: atoms) {
			values[i++] = new Object[]{atom.name, true, true}; }
		
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
					atoms.get(j).setRenderable((Boolean)table.getValueAt(j, i)); }
				if(i == 2) {
					atoms.get(j).visibleLabel = (Boolean)table.getValueAt(j, i); } }
		});
		
		/** set selection listener */
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event) {
				int row = table.getSelectedRow();
				String description = atoms.get(row).getDescription();
				textArea.setText(description); }
		});
		return table; }
	
	private JTextArea createTextArea() {
		JTextArea textArea = new JTextArea(7, 17);
		textArea.setEditable(false);
		return textArea; }
	
	public EastPanel(ModelSubframe subframe, int xSize) {
		atoms = subframe.engine.models.atoms;
		bonds = subframe.engine.models.bonds;
		this.xSize = xSize;
		setPreferredSize(new Dimension(xSize, 10));
		
		viewPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10,10,10,10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		textArea = createTextArea();
		atomTable = createAtomTable(atoms);
		
		viewPanel.add(new JScrollPane(atomTable), gbc);
		gbc.gridy++;
		viewPanel.add(new JScrollPane(textArea), gbc);
		
		setViewportView(viewPanel); }
	
}
