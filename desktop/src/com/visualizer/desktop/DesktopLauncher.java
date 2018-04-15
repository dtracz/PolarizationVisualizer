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
		atomColors.put("H",  Color.rgba8888(0.90f,0.90f,0.90f, 1));
		atomColors.put("He", Color.rgba8888(217f/255f,255f/255f,255f/255f, 1));
		atomColors.put("Li", Color.rgba8888(204f/255f,128f/255f,255f/255f, 1));
		atomColors.put("Be", Color.rgba8888(194f/255f,255f/255f,000f/255f, 1));
		atomColors.put("B",  Color.rgba8888(255f/255f,181f/255f,181f/255f, 1));
		atomColors.put("C",  Color.rgba8888(074f/255f,074f/255f,074f/255f, 1));
		atomColors.put("N",  Color.rgba8888(048f/255f,080f/255f,248f/255f, 1));
		atomColors.put("O",  Color.rgba8888(255f/255f,013f/255f,013f/255f, 1));
		atomColors.put("F",  Color.rgba8888(144f/255f,224f/255f,080f/255f, 1));
		atomColors.put("Ne", Color.rgba8888(179f/255f,227f/255f,245f/255f, 1));
		atomColors.put("Na", Color.rgba8888(171f/255f,092f/255f,242f/255f, 1));
		atomColors.put("Mg", Color.rgba8888(138f/255f,255f/255f,000f/255f, 1));
		atomColors.put("Al", Color.rgba8888(191f/255f,166f/255f,166f/255f, 1));
		atomColors.put("Si", Color.rgba8888(240f/255f,200f/255f,160f/255f, 1));
		atomColors.put("P",  Color.rgba8888(255f/255f,128f/255f,000f/255f, 1));
		atomColors.put("S",  Color.rgba8888(255f/255f,255f/255f,048f/255f, 1));
		atomColors.put("Cl", Color.rgba8888(031f/255f,240f/255f,031f/255f, 1));
		atomColors.put("Ar", Color.rgba8888(128f/255f,209f/255f,227f/255f, 1));
		atomColors.put("K",  Color.rgba8888(143f/255f,064f/255f,212f/255f, 1));
		atomColors.put("Ca", Color.rgba8888(061f/255f,255f/255f,000f/255f, 1));
		atomColors.put("Sc", Color.rgba8888(230f/255f,230f/255f,230f/255f, 1));
		atomColors.put("Ti", Color.rgba8888(191f/255f,194f/255f,199f/255f, 1));
		atomColors.put("V",  Color.rgba8888(166f/255f,166f/255f,171f/255f, 1));
		atomColors.put("Cr", Color.rgba8888(138f/255f,153f/255f,199f/255f, 1));
		atomColors.put("Mn", Color.rgba8888(156f/255f,122f/255f,199f/255f, 1));
		atomColors.put("Fe", Color.rgba8888(224f/255f,102f/255f,051f/255f, 1));
		atomColors.put("Co", Color.rgba8888(240f/255f,144f/255f,160f/255f, 1));
		atomColors.put("Ni", Color.rgba8888(080f/255f,208f/255f,080f/255f, 1));
		atomColors.put("Cu", Color.rgba8888(200f/255f,128f/255f,051f/255f, 1));
		atomColors.put("Zn", Color.rgba8888(125f/255f,128f/255f,176f/255f, 1));
		atomColors.put("Ga", Color.rgba8888(194f/255f,143f/255f,143f/255f, 1));
		atomColors.put("Ge", Color.rgba8888(102f/255f,143f/255f,143f/255f, 1));
		atomColors.put("As", Color.rgba8888(189f/255f,128f/255f,227f/255f, 1));
		atomColors.put("Se", Color.rgba8888(255f/255f,161f/255f,000f/255f, 1));
		atomColors.put("Br", Color.rgba8888(166f/255f,041f/255f,041f/255f, 1));
		atomColors.put("Kr", Color.rgba8888(092f/255f,184f/255f,209f/255f, 1));
		atomColors.put("Rb", Color.rgba8888(112f/255f,046f/255f,176f/255f, 1));
		atomColors.put("Sr", Color.rgba8888(000f/255f,255f/255f,000f/255f, 1));
		atomColors.put("Y",  Color.rgba8888(148f/255f,255f/255f,255f/255f, 1));
		atomColors.put("Zr", Color.rgba8888(148f/255f,224f/255f,224f/255f, 1));
		atomColors.put("Nb", Color.rgba8888(115f/255f,194f/255f,201f/255f, 1));
		atomColors.put("Mo", Color.rgba8888(084f/255f,181f/255f,181f/255f, 1));
		atomColors.put("Tc", Color.rgba8888(059f/255f,158f/255f,158f/255f, 1));
		atomColors.put("Ru", Color.rgba8888(036f/255f,143f/255f,143f/255f, 1));
		atomColors.put("Rh", Color.rgba8888(010f/255f,125f/255f,140f/255f, 1));
		atomColors.put("Pd", Color.rgba8888(000f/255f,105f/255f,133f/255f, 1));
		atomColors.put("Ag", Color.rgba8888(192f/255f,192f/255f,192f/255f, 1));
		atomColors.put("Cd", Color.rgba8888(255f/255f,217f/255f,143f/255f, 1));
		atomColors.put("In", Color.rgba8888(166f/255f,117f/255f,115f/255f, 1));
		atomColors.put("Sn", Color.rgba8888(102f/255f,128f/255f,128f/255f, 1));
		atomColors.put("Sb", Color.rgba8888(158f/255f,099f/255f,181f/255f, 1));
		atomColors.put("Te", Color.rgba8888(212f/255f,122f/255f,000f/255f, 1));
		atomColors.put("I",  Color.rgba8888(148f/255f,000f/255f,148f/255f, 1));
		atomColors.put("Xe", Color.rgba8888(066f/255f,158f/255f,176f/255f, 1));
		atomColors.put("Cs", Color.rgba8888(087f/255f,023f/255f,143f/255f, 1));
		atomColors.put("Ba", Color.rgba8888(000f/255f,201f/255f,000f/255f, 1));
		atomColors.put("La", Color.rgba8888(112f/255f,212f/255f,255f/255f, 1));
		atomColors.put("Ce", Color.rgba8888(255f/255f,255f/255f,199f/255f, 1));
		atomColors.put("Pr", Color.rgba8888(217f/255f,255f/255f,199f/255f, 1));
		atomColors.put("Nd", Color.rgba8888(199f/255f,255f/255f,199f/255f, 1));
		atomColors.put("Pm", Color.rgba8888(163f/255f,255f/255f,199f/255f, 1));
		atomColors.put("Sm", Color.rgba8888(143f/255f,255f/255f,199f/255f, 1));
		atomColors.put("Eu", Color.rgba8888(097f/255f,255f/255f,199f/255f, 1));
		atomColors.put("Gd", Color.rgba8888(069f/255f,255f/255f,199f/255f, 1));
		atomColors.put("Dy", Color.rgba8888(031f/255f,255f/255f,199f/255f, 1));
		atomColors.put("Tb", Color.rgba8888(048f/255f,255f/255f,199f/255f, 1));
		atomColors.put("Ho", Color.rgba8888(000f/255f,255f/255f,156f/255f, 1));
		atomColors.put("Er", Color.rgba8888(000f/255f,230f/255f,117f/255f, 1));
		atomColors.put("Tm", Color.rgba8888(000f/255f,121f/255f,082f/255f, 1));
		atomColors.put("Yb", Color.rgba8888(000f/255f,191f/255f,056f/255f, 1));
		atomColors.put("Lu", Color.rgba8888(000f/255f,171f/255f,036f/255f, 1));
		atomColors.put("Hf", Color.rgba8888(077f/255f,194f/255f,255f/255f, 1));
		atomColors.put("Ta", Color.rgba8888(077f/255f,166f/255f,255f/255f, 1));
		atomColors.put("W",  Color.rgba8888(033f/255f,148f/255f,214f/255f, 1));
		atomColors.put("Re", Color.rgba8888(038f/255f,125f/255f,171f/255f, 1));
		atomColors.put("Os", Color.rgba8888(038f/255f,102f/255f,150f/255f, 1));
		atomColors.put("Ir", Color.rgba8888(023f/255f,084f/255f,135f/255f, 1));
		atomColors.put("Pt", Color.rgba8888(208f/255f,208f/255f,224f/255f, 1));
		atomColors.put("Au", Color.rgba8888(255f/255f,209f/255f,025f/255f, 1));
		atomColors.put("Hg", Color.rgba8888(184f/255f,184f/255f,208f/255f, 1));
		atomColors.put("Tl", Color.rgba8888(166f/255f,084f/255f,077f/255f, 1));
		atomColors.put("Pb", Color.rgba8888(087f/255f,089f/255f,097f/255f, 1));
		atomColors.put("Bi", Color.rgba8888(158f/255f,079f/255f,181f/255f, 1));
		atomColors.put("Po", Color.rgba8888(171f/255f,092f/255f,000f/255f, 1));
		atomColors.put("At", Color.rgba8888(117f/255f,079f/255f,069f/255f, 1));
		atomColors.put("Rn", Color.rgba8888(066f/255f,130f/255f,150f/255f, 1));
		atomColors.put("Fr", Color.rgba8888(066f/255f,000f/255f,102f/255f, 1));
		atomColors.put("Ra", Color.rgba8888(000f/255f,125f/255f,000f/255f, 1));
		atomColors.put("Ac", Color.rgba8888(112f/255f,171f/255f,250f/255f, 1));
		atomColors.put("Th", Color.rgba8888(000f/255f,186f/255f,255f/255f, 1));
		atomColors.put("Pa", Color.rgba8888(000f/255f,161f/255f,255f/255f, 1));
		atomColors.put("U",  Color.rgba8888(000f/255f,143f/255f,255f/255f, 1));
		atomColors.put("Np", Color.rgba8888(000f/255f,128f/255f,255f/255f, 1));
		atomColors.put("Pu", Color.rgba8888(000f/255f,107f/255f,255f/255f, 1));
		atomColors.put("Am", Color.rgba8888(084f/255f,092f/255f,242f/255f, 1));
		atomColors.put("Cm", Color.rgba8888(120f/255f,092f/255f,227f/255f, 1));
		atomColors.put("Bk", Color.rgba8888(138f/255f,079f/255f,227f/255f, 1));
		atomColors.put("Cf", Color.rgba8888(161f/255f,054f/255f,212f/255f, 1));
		atomColors.put("Es", Color.rgba8888(179f/255f,031f/255f,212f/255f, 1));
		atomColors.put("Fm", Color.rgba8888(179f/255f,031f/255f,186f/255f, 1));
		atomColors.put("Md", Color.rgba8888(179f/255f,013f/255f,166f/255f, 1));
		atomColors.put("No", Color.rgba8888(189f/255f,013f/255f,135f/255f, 1));
		atomColors.put("Lr", Color.rgba8888(199f/255f,000f/255f,102f/255f, 1));
		atomColors.put("Rf", Color.rgba8888(204f/255f,000f/255f,089f/255f, 1));
		atomColors.put("Db", Color.rgba8888(209f/255f,000f/255f,079f/255f, 1));
		atomColors.put("Sg", Color.rgba8888(217f/255f,000f/255f,069f/255f, 1));
		atomColors.put("Bh", Color.rgba8888(224f/255f,000f/255f,056f/255f, 1));
		atomColors.put("Hs", Color.rgba8888(230f/255f,000f/255f,046f/255f, 1));
		atomColors.put("Mt", Color.rgba8888(235f/255f,000f/255f,038f/255f, 1));
		try {
			XMLEncoder xmlEncoder = new XMLEncoder(new FileOutputStream("atomColors.xml"));
			xmlEncoder.writeObject(atomColors);
			xmlEncoder.close(); }
		catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace(); }
	//*/
	}
}


