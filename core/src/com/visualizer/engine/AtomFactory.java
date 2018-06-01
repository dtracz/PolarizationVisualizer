package com.visualizer.engine;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.jet.math.Functions;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.visualizer.userInterface.MainWindow;

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class AtomFactory {
	private ModelSet models;
	private float sumWeights;
	private Vector3 midCoords;
	
	private Vector3 axesOrigin;
	private Queue<String> names;
	private Queue<Vector3> positions;
	private Queue<Quaternion> orientations;
	private Queue<Vector3> sizes;
	private Queue<String> bonds;
	private Queue<String> alphas;
	
	//----------------------------------------------------------------
	
	private static boolean isClose(double x1, double x2) {
		return Math.abs(x1 - x2) < 0.0001;
	}
	
	private static double getX01(double phi, double nx, double ny, double nz) {
		return nz*Math.sin(phi) + nx*ny*(1.-Math.cos(phi));
	}
	
	private static double getX02(double phi, double nx, double ny, double nz) {
		return -ny*Math.sin(phi) + nx*nz*(1.-Math.cos(phi));
	}
	
	private static double getX12(double phi, double nx, double ny, double nz) {
		return nx*Math.sin(phi) + ny*nz*(1.-Math.cos(phi));
	}
	
	private static Quaternion getAngle(DoubleMatrix2D eigenVecs, Vector3 helper, Algebra algebra) {
		eigenVecs = algebra.transpose(eigenVecs);
		if(algebra.det(eigenVecs) < 0) {
			eigenVecs.assign(Functions.mult(-1));
		}
		double phi = Math.acos((algebra.trace(eigenVecs)-1.)/2.);
		
		double nx = Math.sqrt((eigenVecs.getQuick(0,0) - Math.cos(phi))/(1.00000001-Math.cos(phi)));
		double ny = Math.sqrt((eigenVecs.getQuick(1,1) - Math.cos(phi))/(1.00000001-Math.cos(phi)));
		double nz = Math.sqrt((eigenVecs.getQuick(2,2) - Math.cos(phi))/(1.00000001-Math.cos(phi)));
		
		if(isClose(getX01(phi, nx, ny, nz), eigenVecs.getQuick(0,1)) &&
				   isClose(getX02(phi, nx, ny, nz), eigenVecs.getQuick(0,2)) &&
				   isClose(getX12(phi, nx, ny, nz), eigenVecs.getQuick(1,2)) ) {
			//System.out.println("OK");
		}
		else if(isClose(getX01(phi, nx, ny, -nz), eigenVecs.getQuick(0,1)) &&
				        isClose(getX02(phi, nx, ny, -nz), eigenVecs.getQuick(0,2)) &&
				        isClose(getX12(phi, nx, ny, -nz), eigenVecs.getQuick(1,2)) ) {
			//System.out.println("#Z");
			nz *= -1;
		}
		else if(isClose(getX01(phi, nx, -ny, nz), eigenVecs.getQuick(0,1)) &&
				        isClose(getX02(phi, nx, -ny, nz), eigenVecs.getQuick(0,2)) &&
				        isClose(getX12(phi, nx, -ny, nz), eigenVecs.getQuick(1,2)) ) {
			//System.out.println("#Y");
			ny *= -1;
		}
		else if(isClose(getX01(phi, -nx, ny, nz), eigenVecs.getQuick(0,1)) &&
				        isClose(getX02(phi, -nx, ny, nz), eigenVecs.getQuick(0,2)) &&
				        isClose(getX12(phi, -nx, ny, nz), eigenVecs.getQuick(1,2)) ) {
			//System.out.println("#X");
			nx *= -1;
		}
		else if(isClose(getX01(phi, nx, -ny, -nz), eigenVecs.getQuick(0,1)) &&
				        isClose(getX02(phi, nx, -ny, -nz), eigenVecs.getQuick(0,2)) &&
				        isClose(getX12(phi, nx, -ny, -nz), eigenVecs.getQuick(1,2)) ) {
			//System.out.println("#YZ");
			ny *= -1;
			nz *= -1;
		}
		else if(isClose(getX01(phi, -nx, ny, -nz), eigenVecs.getQuick(0,1)) &&
				        isClose(getX02(phi, -nx, ny, -nz), eigenVecs.getQuick(0,2)) &&
				        isClose(getX12(phi, -nx, ny, -nz), eigenVecs.getQuick(1,2)) ) {
			//System.out.println("#XZ");
			nx *= -1;
			nz *= -1;
		}
		else if(isClose(getX01(phi, -nx, -ny, nz), eigenVecs.getQuick(0,1)) &&
				        isClose(getX02(phi, -nx, -ny, nz), eigenVecs.getQuick(0,2)) &&
				        isClose(getX12(phi, -nx, -ny, nz), eigenVecs.getQuick(1,2)) ) {
			//System.out.println("#XY");
			nx *= -1;
			ny *= -1;
		}
		else if(isClose(getX01(phi, -nx, -ny, -nz), eigenVecs.getQuick(0,1)) &&
				        isClose(getX02(phi, -nx, -ny, -nz), eigenVecs.getQuick(0,2)) &&
				        isClose(getX12(phi, -nx, -ny, -nz), eigenVecs.getQuick(1,2)) ) {
			//System.out.println("#XYZ");
			nx *= -1;
			ny *= -1;
			nz *= -1;
		}
		else {
			//System.out.println("Sth's very wrong");
		}
		
		helper.set((float)nx, (float)ny, (float)nz);
		return new Quaternion(helper.nor(), (float)Math.toDegrees(phi));
	}
	
	//----------------------------------------------------------------
	
	AtomFactory(ModelSet models) {
		this.models = models;
		sumWeights = 0;
		midCoords = new Vector3(0, 0, 0);
		
		axesOrigin = new Vector3(1000, 1000, 1000);
		names = new LinkedList<String>();
		positions = new LinkedList<Vector3>();
		orientations = new LinkedList<Quaternion>();
		sizes = new LinkedList<Vector3>();
		bonds = new LinkedList<String>();
		alphas = new LinkedList<String>();
	}
	
	//----------------------------------------------------------------
	
	
	private Vector3 getAtoms(Scanner scanner, DoubleMatrix2D alpha, Algebra algebra) {
		String name;
		Vector3 helper = new Vector3();
		midCoords.scl(sumWeights);
		
		while(!scanner.nextLine().matches("ATOMIC COORDINATES \\(ORTHOGONAL SYSTEM\\)"));
		scanner.nextLine(); scanner.nextLine(); scanner.nextLine();
		while((name = scanner.next()).matches("[A-Z][a-z]?.*")) {
			names.add(name);
			positions.add(new Vector3(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat()));
		}
		while(!scanner.nextLine().matches("ATOMIC PROPERTIES"));
		while(!scanner.nextLine().matches("Atom.*"));
		scanner.nextLine();
		while(scanner.next().matches("[A-Z][a-z]?.*")) {
			scanner.nextFloat();
			alpha.setQuick(0,0, scanner.nextDouble());
			alpha.setQuick(1,1, scanner.nextDouble());
			alpha.setQuick(2,2, scanner.nextDouble());
			double d01 = scanner.nextDouble();
			double d02 = scanner.nextDouble();
			double d12 = scanner.nextDouble();
			alpha.setQuick(0,1, d01);
			alpha.setQuick(1,0, d01);
			alpha.setQuick(0,2, d02);
			alpha.setQuick(2,0, d02);
			alpha.setQuick(1,2, d12);
			alpha.setQuick(2,1, d12);
			
			// calculating ellipsoid
			EigenvalueDecomposition eigenDecomp = new EigenvalueDecomposition(alpha);
			DoubleMatrix2D eigenValues = eigenDecomp.getD();
			sizes.add(new Vector3((float)eigenValues.getQuick(0,0), (float)eigenValues.getQuick(1,1), (float)eigenValues.getQuick(2,2)));
			orientations.add(getAngle(eigenDecomp.getV(), helper, algebra));
			alphas.add(alpha.toString());
			
			// calculating center of molecule
			float weight = scanner.nextFloat();
			sumWeights += weight;
			Vector3 coords = positions.poll();
			midCoords.add(coords.x*weight, coords.y*weight, coords.z*weight);
			positions.add(coords);
			scanner.nextFloat();
		}
		return midCoords.scl(1f/sumWeights);
	}
	
	
	private String getMolecule(Scanner scanner, DoubleMatrix2D alpha, Algebra algebra,
	                           Vector3 size, Quaternion orientation) {
		while(!scanner.nextLine().matches("MOLECULAR POLARIZABILITY TENSOR CARTESIAN SYSTEM"));
		scanner.nextLine(); scanner.nextLine(); scanner.nextLine();
		alpha.setQuick(0,0, scanner.nextDouble());
		alpha.setQuick(1,1, scanner.nextDouble());
		alpha.setQuick(2,2, scanner.nextDouble());
		double d01 = scanner.nextDouble();
		double d02 = scanner.nextDouble();
		double d12 = scanner.nextDouble();
		alpha.setQuick(0,1, d01);
		alpha.setQuick(1,0, d01);
		alpha.setQuick(0,2, d02);
		alpha.setQuick(2,0, d02);
		alpha.setQuick(1,2, d12);
		alpha.setQuick(2,1, d12);
		
		EigenvalueDecomposition eigenDecomp = new EigenvalueDecomposition(alpha);
		DoubleMatrix2D eigenValues = eigenDecomp.getD();
		orientation.set(getAngle(eigenDecomp.getV(), size, algebra)); // size here only as helper
		size.set((float)eigenValues.getQuick(0,0), (float)eigenValues.getQuick(1,1), (float)eigenValues.getQuick(2,2));
		return alpha.toString();
	}
	
	
	private void getBonds(Scanner scanner, Queue<String> bonds) {
		while(!scanner.nextLine().matches("BOND PROPERTIES"));
		while(!scanner.nextLine().matches("\\s+A.B.*"));
		scanner.nextLine();
		while(scanner.hasNext("[A-Z][a-z]?\\d*")) {
			bonds.add(scanner.next());
			bonds.add(scanner.next());
			for(int i=0; i<3; i++) { scanner.next(); }
			bonds.add(Boolean.toString(scanner.nextFloat() < 0.1));
			for(int i=0; i<5; i++) { scanner.next(); }
			bonds.add(scanner.next());
			scanner.nextLine();
		}
	}
	
	
	private String readFile(File sourceFile, Vector3 molecularCoords, Vector3 molecularSize, Quaternion molecularOrientation)
		throws FileNotFoundException, RuntimeException {
		String molecularAlpha;
		
		Scanner scanner = new Scanner(sourceFile).useLocale(Locale.ROOT); 	// dot decimal separator instead of comma
		DoubleMatrix2D alpha = DoubleFactory2D.dense.make(3,3);
		Algebra algebra = new Algebra();
		
		molecularCoords.set(getAtoms(scanner, alpha, algebra));
		molecularAlpha = getMolecule(scanner, alpha, algebra, molecularSize, molecularOrientation);
		getBonds(scanner, bonds);
		
		scanner.close();
		
		return molecularAlpha;
	}
	
	
	Vector3 parseFile(File sourceFile) throws FileNotFoundException, RuntimeException {
		XMLDecoder xmlDecoder = new XMLDecoder(new FileInputStream(MainWindow.selfPath+"atomColors.xml"));
		Map<String, Integer> atomColors = (TreeMap<String, Integer>)xmlDecoder.readObject();
		
		Vector3 molecularPosition = new Vector3(); // position of the center of molecule
		Vector3 molecularSize = new Vector3();
		Quaternion molecularOrintation = new Quaternion();
		String molecularAlpha = readFile(sourceFile, molecularPosition, molecularSize, molecularOrintation);
		if(molecularAlpha == null) return axesOrigin;
		
		Color col = new Color(atomColors.get("MOL"));
		models.addAtom("MOL", col, molecularPosition, molecularOrintation, molecularSize, molecularAlpha);
		
		while(!sizes.isEmpty()) {
			String name = names.poll();
			Color color = null;
			if(name.matches("[A-Z][a-z].*")) {
				color = new Color(atomColors.get(name.substring(0,2))); }
			else if(name.matches("[A-Z].*")) {
				color = new Color(atomColors.get(name.substring(0,1))); }
			if(color == null) {
				color = Color.WHITE; }
			Vector3 position = positions.poll();
			axesOrigin.x = position.x < axesOrigin.x ? position.x : axesOrigin.x;
			axesOrigin.y = position.y < axesOrigin.y ? position.y : axesOrigin.y;
			axesOrigin.z = position.z < axesOrigin.z ? position.z : axesOrigin.z;
			models.addAtom(name, color, position, orientations.poll(), sizes.poll(), alphas.poll());
		}
		while(!bonds.isEmpty()) {
			models.addBond(bonds.poll(), bonds.poll(), Boolean.parseBoolean(bonds.poll()), bonds.poll());
		}
		//for(Atom atom: models.atoms) {
		//	atom.setTexture(false); }
		
		xmlDecoder.close();
		return axesOrigin;
	}
}
