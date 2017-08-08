package com.visualizer.desktop;

import com.badlogic.gdx.graphics.Color;
import com.visualizer.userInterface.MainWindow;

import java.beans.XMLEncoder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.TreeMap;

public class DesktopLauncher {

	public static void main(String[] arg) {
		MainWindow.getInstance();
	/*
		Map atomColors = new TreeMap<String, Integer>();
		atomColors.put("H", Color.rgba8888(0.90f,0.90f,0.90f, 1));
		atomColors.put("O", Color.rgba8888(1,0,0,1));
		atomColors.put("N", Color.rgba8888(0,0,1,1));
		atomColors.put("C", Color.rgba8888(0.15f,0.15f,0.15f,1));
		try {
			XMLEncoder xmlEncoder = new XMLEncoder(new FileOutputStream("atomColors.xml"));
			xmlEncoder.writeObject(atomColors);
			xmlEncoder.close(); }
		catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace(); }
	//*/
	}
}


