package com.visualizer.engine;

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

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class MainEngine implements ApplicationListener {
	public File sourceFile;
	public final static Vector3 ONES = new Vector3(1,1,1);
	public final static Quaternion Q_ZERO = new Quaternion();
	
	private Environment environment;
	private PointLight pointLight;
	private Viewport viewport;
	private CameraHandler cameraHandler;
	private Listener listener;
	private Map<String, Integer> atomColors;
	
	boolean mode2d;
	float m2dFactor;
	
	public ModelSet models;
	
	/* - CONSTRUCTOR - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	private Quaternion countAngle(Matrix3 alpha, Vector3 helper) {
		helper.set(Vector3.Z);
		helper.mul(alpha);
		return new Quaternion().setFromCross(helper.nor(), Vector3.Z); }
	
	private void fileReader(Queue<String> names, Queue<Vector3> positions, Queue<Quaternion> orientations,
	                        Queue<Vector3> sizes, Queue<String> bonds) throws FileNotFoundException, RuntimeException {
		Scanner scanner = new Scanner(sourceFile).useLocale(Locale.ROOT); 	// dot decimal separator instead of comma
		String name;
		Matrix3 alpha = new Matrix3();
		Vector3 helper = new Vector3();
		
		while(!scanner.nextLine().matches("ATOMIC COORDINATES \\(ORTHOGONAL SYSTEM\\)"));
		scanner.nextLine(); scanner.nextLine(); scanner.nextLine();
		while((name = scanner.next()).matches("[A-Z][a-z]?.*")) {
			names.add(name);
			positions.add(new Vector3(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat())); }
		
		while(!scanner.nextLine().matches("ATOMIC PROPERTIES"));
		while(!scanner.nextLine().matches("Atom.*")); scanner.nextLine();
		while(scanner.next().matches("[A-Z][a-z]?.*")) {
			scanner.nextFloat();
			alpha.val[0] = scanner.nextFloat();
			alpha.val[4] = scanner.nextFloat();
			alpha.val[8] = scanner.nextFloat();
			alpha.val[1] = alpha.val[3] = scanner.nextFloat();
			alpha.val[2] = alpha.val[6] = scanner.nextFloat();
			alpha.val[5] = alpha.val[7] = scanner.nextFloat();
			sizes.add(new Vector3(alpha.val[0], alpha.val[4], alpha.val[8]));
			orientations.add(countAngle(alpha, helper));
			scanner.nextFloat(); scanner.nextFloat(); }
		
		while(!scanner.nextLine().matches("BOND PROPERTIES"));
		while(!scanner.nextLine().matches("\\s+A.B.*")); scanner.nextLine();
		while(scanner.hasNext("[A-Z][a-z]?\\d*")) {
			bonds.add(scanner.next());
			bonds.add(scanner.next());
			scanner.nextLine(); }
		scanner.close(); }

	private void atomFactory() {
		Queue<String> names = new LinkedList<String>();
		Queue<Vector3> positions = new LinkedList<Vector3>();
		Queue<Quaternion> orientations = new LinkedList<Quaternion>();
		Queue<Vector3> sizes = new LinkedList<Vector3>();
		Queue<String> bonds = new LinkedList<String>();
		try {
			fileReader(names, positions, orientations, sizes, bonds); }
		catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return; }
		catch(RuntimeException re) {
			re.printStackTrace();
			return; }
		while(!sizes.isEmpty()) {
			String name = names.poll();
			Color color = null;
			if(name.matches("[A-Z][a-z].*")) {
				color = new Color(atomColors.get(name.substring(0,2))); }
			else if(name.matches("[A-Z].*")) {
				color = new Color(atomColors.get(name.substring(0,1))); }
			if(color == null) {
				color = Color.WHITE; }
			models.addAtom(name, color, positions.poll(), orientations.poll(), sizes.poll()); }
		while(!bonds.isEmpty()) {
			try {
				models.addBond(bonds.poll(), bonds.poll()); }
			catch(NoSuchElementException nsee) {
				nsee.printStackTrace(); } }
		
	}
	
	public MainEngine(File file) {
		sourceFile = file;
		mode2d = false;
		m2dFactor = 34;
		try {
			XMLDecoder xmlDecoder = new XMLDecoder(new FileInputStream("atomColors.xml"));
			atomColors = (TreeMap<String, Integer>)xmlDecoder.readObject();
			xmlDecoder.close(); }
		catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace(); } }
	
	/* - PUBLIC OPERATIONS - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public void changeMode() {
		setMode(!mode2d); }
	
	public void setMode(boolean m2d) {
		if(mode2d^m2d) {
			mode2d = m2d; }
		else return;
		if(mode2d) {
			cameraHandler.setCamera(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), 500);
			//environment.remove(pointLight);
			((OrthographicCamera)cameraHandler.getCamera()).zoom /= m2dFactor; }
		else {
			//environment.add(pointLight);
			cameraHandler.setCamera(new PerspectiveCamera(50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), -500); }
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
		pointLight = new PointLight().set(0.8f, 0.8f, 0.8f, -10f, -10f, 10f, 200f);
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.add(pointLight);

		ModelBuilder builder = new ModelBuilder();
		models = new ModelSet(builder);
		
		Camera camera = new PerspectiveCamera(50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//Camera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0f, -20f, 0f);
		//camera.near = 0.1f;
		//camera.far = 30f;
		
		cameraHandler = new CameraHandler(camera);
		listener = new Listener(cameraHandler, this);
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		Gdx.input.setInputProcessor(listener);
		
		//cameraHandler.setCenter(0,-5,-5);
		atomFactory(); }
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		//viewport.apply();
		cameraHandler.updateCamera();
		models.renderAll(cameraHandler.getCamera(), environment); }
	
	@Override
	public void pause() {
		System.out.println("x0");
	}
	
	@Override
	public void resume() {
		System.out.println("x1");
	}
	
	@Override
	public void dispose() {
		System.out.println("x2");
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		listener.resize(width, height);
		cameraHandler.updateCamera(); }
}