package com.visualizer.engine;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.jet.math.Functions;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.visualizer.userInterface.MainWindow;

import javax.swing.*;
import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class MainEngine implements ApplicationListener {
	public final static Vector3 ONES = new Vector3(1,1,1);
	public final static Quaternion Q_ZERO = new Quaternion();
	
	public List<PointLight> pointLights;
	private Environment environment;
	private Viewport viewport;
	private Listener listener;
	
	public CameraHandler cameraHandler;
	private boolean mode2d;
	private Camera orthographicCamera;
	private Camera perspectiveCamera;
	public final Vector3 defaultUpAxis = new Vector3(0,0,1);
	public final static Matrix3 rotationMatrix = new Matrix3(new float[] { 0, 0, 1,
																		   1, 0, 0,
																		   0, 1, 0 });
	
	private File sourceFile;
	private Map<String, Integer> atomColors;
	public ModelSet models;
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	void showMessage(final String text, final String type) {
		Thread messageThread = new Thread(new Runnable(){
			@Override
			public void run() {
				JOptionPane.showMessageDialog(MainWindow.getInstance(), text, type, JOptionPane.ERROR_MESSAGE); } } );
		messageThread.start(); }
	
	/* - CONSTRUCTOR - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	private boolean isClose(double x1, double x2) {
		return Math.abs(x1 - x2) < 0.0001;
	}
	
	private double getX01(double phi, double nx, double ny, double nz) {
		return nz*Math.sin(phi) + nx*ny*(1.-Math.cos(phi));
	}
	private double getX02(double phi, double nx, double ny, double nz) {
		return -ny*Math.sin(phi) + nx*nz*(1.-Math.cos(phi));
	}
	private double getX12(double phi, double nx, double ny, double nz) {
		return nx*Math.sin(phi) + ny*nz*(1.-Math.cos(phi));
	}
	
	private Quaternion getAngle(DoubleMatrix2D eigenVecs, Vector3 helper, Algebra algebra) {
		eigenVecs = algebra.transpose(eigenVecs);
		if(algebra.det(eigenVecs) < 0) {
			eigenVecs.assign(Functions.mult(-1));
		}
		double phi = Math.acos((algebra.trace(eigenVecs)-1.)/2.);
		
		double nx = Math.sqrt((eigenVecs.getQuick(0,0) - Math.cos(phi))/(1.-Math.cos(phi)));
		double ny = Math.sqrt((eigenVecs.getQuick(1,1) - Math.cos(phi))/(1.-Math.cos(phi)));
		double nz = Math.sqrt((eigenVecs.getQuick(2,2) - Math.cos(phi))/(1.-Math.cos(phi)));
		
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
		
		double x01 = getX01(phi, nx, ny, nz);
		double x02 = getX02(phi, nx, ny, nz);
		double x12 = getX12(phi, nx, ny, nz);
		
		helper.set((float)getX01(phi, nx, ny, nz), (float)getX02(phi, nx, ny, nz), (float)getX12(phi, nx, ny, nz));
		helper.set((float)nx, (float)ny, (float)nz);
		return new Quaternion(helper.nor(), (float)Math.toDegrees(phi));
	}
	
	
	private Vector3 getAtoms(Scanner scanner, Queue<String> names, Queue<Vector3> positions,
	                      Queue<Quaternion> orientations, Queue<Vector3> sizes, Queue<String> alphas) {
		DoubleMatrix2D alpha = DoubleFactory2D.dense.make(3,3);
		String name;
		Vector3 helper = new Vector3();
		Algebra algebra = new Algebra();
		Vector3 midCoords = new Vector3(0, 0, 0);
		
		while(!scanner.nextLine().matches("ATOMIC COORDINATES \\(ORTHOGONAL SYSTEM\\)"));
		scanner.nextLine(); scanner.nextLine(); scanner.nextLine();
		while((name = scanner.next()).matches("[A-Z][a-z]?.*")) {
			names.add(name);
			positions.add(new Vector3(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat()));
		}
		while(!scanner.nextLine().matches("ATOMIC PROPERTIES"));
		while(!scanner.nextLine().matches("Atom.*"));
		scanner.nextLine();
		float sumWeights = 0;
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
			midCoords.add(coords.x*weight, coords.y*weight, coords.x*weight);
			positions.add(coords);
			scanner.nextFloat();
		}
		return midCoords;
	}
	
	
	private void fileReader(Queue<String> names, Queue<Vector3> positions, Queue<Quaternion> orientations,
	                        Queue<Vector3> sizes, Queue<String> bonds, Queue<String> alphas)
							throws FileNotFoundException, RuntimeException {
		Scanner scanner = new Scanner(sourceFile).useLocale(Locale.ROOT); 	// dot decimal separator instead of comma
		
		Vector3 midCoords = getAtoms(scanner, names, positions, orientations, sizes, alphas);

		
//		while(!scanner.nextLine().matches("MOLECULAR POLARIZABILITY TENSOR CARTESIAN SYSTEM"));
//		scanner.nextLine(); scanner.nextLine(); scanner.nextLine();
//		alpha.setQuick(0,0, scanner.nextDouble());
//		alpha.setQuick(1,1, scanner.nextDouble());
//		alpha.setQuick(2,2, scanner.nextDouble());
//		double d01 = scanner.nextDouble();
//		double d02 = scanner.nextDouble();
//		double d12 = scanner.nextDouble();
//		alpha.setQuick(0,1, d01);
//		alpha.setQuick(1,0, d01);
//		alpha.setQuick(0,2, d02);
//		alpha.setQuick(2,0, d02);
//		alpha.setQuick(1,2, d12);
//		alpha.setQuick(2,1, d12);
		
		
		while(!scanner.nextLine().matches("BOND PROPERTIES"));
		while(!scanner.nextLine().matches("\\s+A.B.*"));
		scanner.nextLine();
		while(scanner.hasNext("[A-Z][a-z]?\\d*")) {
			bonds.add(scanner.next());
			bonds.add(scanner.next());
			for(int _=0; _<3; _++) { scanner.next(); }
			bonds.add(Boolean.toString(scanner.nextFloat() < 0.1));
			for(int _=0; _<5; _++) { scanner.next(); }
			bonds.add(scanner.next());
			scanner.nextLine(); }
		scanner.close(); }
	
		
	private Vector3 atomFactory() {
		Vector3 shift = new Vector3(1000, 1000, 1000);
		Queue<String> names = new LinkedList<String>();
		Queue<Vector3> positions = new LinkedList<Vector3>();
		Queue<Quaternion> orientations = new LinkedList<Quaternion>();
		Queue<Vector3> sizes = new LinkedList<Vector3>();
		Queue<String> bonds = new LinkedList<String>();
		Queue<String> alphas = new LinkedList<String>();
		try {
			fileReader(names, positions, orientations, sizes, bonds, alphas); }
		catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			showMessage(fnfe.getMessage(), "Error");
			return shift; }
		catch(RuntimeException re) {
			re.printStackTrace();
			showMessage(re.getMessage(), "Error");
			return shift; }
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
			shift.x = position.x < shift.x ? position.x : shift.x;
			shift.y = position.y < shift.y ? position.y : shift.y;
			shift.z = position.z < shift.z ? position.z : shift.z;
			models.addAtom(name, color, position, orientations.poll(), sizes.poll(), alphas.poll()); }
		while(!bonds.isEmpty()) {
			try {
				models.addBond(bonds.poll(), bonds.poll(), Boolean.parseBoolean(bonds.poll()), bonds.poll()); }
			catch(NoSuchElementException nsee) {
				showMessage(nsee.getMessage(), "Error");
				nsee.printStackTrace(); } }
		for(Atom atom: models.atoms) {
			atom.setTexture(false); }
		return shift; }
	
		
	public MainEngine(File file) {
		sourceFile = file;
		mode2d = false;
		try {
			XMLDecoder xmlDecoder = new XMLDecoder(new FileInputStream("atomColors.xml"));
			atomColors = (TreeMap<String, Integer>)xmlDecoder.readObject();
			xmlDecoder.close(); }
		catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace(); } }
	
	/* - PUBLIC OPERATIONS - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public void changeMode() {
		setMode2D(!mode2d); }
	
	public void setMode2D(boolean m2d) {
		if(mode2d^m2d) {
			mode2d = m2d; }
		else return;
		if(mode2d) {
			cameraHandler.setCamera(orthographicCamera); }
		else {
			cameraHandler.setCamera(perspectiveCamera); }
	}
	
	public void exportImage(String filename) {
		byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
		BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
		PixmapIO.writePNG(Gdx.files.external(filename), pixmap);
		pixmap.dispose(); }
	
	/* - APPLICATION LISTENER- - - - - - - - - - - - - - - - - - - - - - - - */
	
	@Override
	public void create() {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		ModelBuilder builder = new ModelBuilder();
		models = new ModelSet(builder);
		Vector3 origin = atomFactory();
		models.createAxes(origin.sub(ONES), 5, 0.8f, 0.1f);
		
		Vector3 center = models.getMoleculeCenter();
		perspectiveCamera = new PerspectiveCamera(50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		orthographicCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cameraHandler = new CameraHandler(perspectiveCamera, center, defaultUpAxis);
		
		pointLights = new LinkedList<PointLight>();
		pointLights.add(new PointLight().set(Color.WHITE, origin.cpy().add(-20, -10, 20), 400f));
		//pointLights.add(new PointLight().set(Color.WHITE, origin.cpy().add(-10,   0, 10), 150f));
		pointLights.add(new PointLight().set(Color.WHITE, origin.cpy().add(-20,  10, 20), 150f));
		for(PointLight pointLight: pointLights) {
			environment.add(pointLight); }
		
		listener = new Listener(this, cameraHandler, models);
		Gdx.input.setInputProcessor(listener);
		
		
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		//viewport.apply();
		cameraHandler.updateCamera();
		models.renderAll(cameraHandler.getCamera(), environment); }
	
	@Override
	public void pause() {
		System.out.println("ApplicationListener::pause()");
	}
	
	@Override
	public void resume() {
		System.out.println("ApplicationListener::resume()");
	}
	
	@Override
	public void dispose() {
		System.out.println("ApplicationListener::dispose()");
		models.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		listener.resize(width, height);
		cameraHandler.updateCamera(); }
	
}