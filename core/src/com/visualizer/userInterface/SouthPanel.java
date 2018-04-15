package com.visualizer.userInterface;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class SouthPanel extends JScrollPane {
	private JPanel viewPanel;
	private final JTextArea textArea;
	
	private ArrayList<String> paths;
	private ArrayList<Process> processes;
	
	private void runSubroutine(final String path, final int index) {
		Thread th = new Thread(new Runnable() {
			public void run() {
				try {
					processes.set(index, new ProcessBuilder(path).start());
					InputStream is = processes.get(index).getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String output;
					
					while((output = br.readLine()) != null) {
						textArea.append(output+'\n');
					}
					processes.get(index).waitFor();
					textArea.append("program ended with exit status " + processes.get(index).exitValue() + "\n");
				}
				catch(IOException ioe) {
					ioe.printStackTrace();
				}
				catch(InterruptedException ie) {
					ie.printStackTrace();
				}
				processes.set(index, null);
			}
		});
		th.start();
//		try {
//			th.join();
//		}
//		catch(InterruptedException ie) {
//			ie.printStackTrace();
//		}
	}
	
	
	private JButton createRunButton(String name, final int index) {
		JButton runButton = new JButton(name);
		runButton.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(processes.get(index) == null) {
					runSubroutine("./test.x", index);
				}
				else {
					System.out.println("is not null");
				}
			}
		});
		return runButton;
	}
	
	private JButton createKillButton(final int index) {
		JButton runButton = new JButton("kill");
		runButton.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(processes.get(index) != null) {
					processes.get(index).destroy();
				}
			}
		});
		return runButton;
	}
	
	
	public SouthPanel(int xSize, int ySize) {
		setPreferredSize(new Dimension(xSize, ySize));
		viewPanel = new JPanel(new BorderLayout());
		
		paths = new ArrayList<String>();
		paths.add("./PolaberExe.exe");
		paths.add("./FG_assign.exe");
		paths.add("./FG_calc.exe");
		processes = new ArrayList<Process>(Collections.<Process>nCopies(paths.size(), null));
		System.out.print(processes.size());
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.gridx = 0;
		JButton button;
		for(int i=0; i<paths.size(); i++) {
			gbc.insets = new Insets(5,5,0, 5);
			button = createRunButton(paths.get(i), i);
			buttonPanel.add(button, gbc);
			gbc.gridy++;
		}
		gbc.gridy = 0;
		gbc.gridx++;
		for(int i=0; i<paths.size(); i++) {
			gbc.insets = new Insets(5,0,0, 5);
			button = createKillButton(i);
			buttonPanel.add(button, gbc);
			gbc.gridy++;
		}
		viewPanel.add(buttonPanel, BorderLayout.WEST);

		JPanel textAreaPane = new JPanel(new BorderLayout());
		textAreaPane.setBorder(BorderFactory.createTitledBorder("program output"));
		textArea = new JTextArea(7, 16);
		textArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textAreaPane.add(textArea, BorderLayout.CENTER);
		JScrollPane scroll = new JScrollPane (textArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		viewPanel.add(scroll, BorderLayout.CENTER);
		
		setViewportView(viewPanel);
	}
}
