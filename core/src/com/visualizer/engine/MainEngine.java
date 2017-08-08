package com.visualizer.engine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class MainEngine implements ApplicationListener {
	public final static Vector3 ONES = new Vector3(1,1,1);
	
	private Environment environment;
	private Viewport viewport;
	private CameraHandler cameraHandler;
	
	private Map<String, Integer> atomColors;
	
	boolean mode2d;
	float m2dFactor;
	
	public File sourceFile;
	
	public Listener listener;
	//private ModelBatch modelBatch;
	
	public ModelSet models;
	//public Axes axes;
	
	ModelInstance cylinderInstance;
	ModelInstance capsuleInstance;
	Bound bound;
	
	/* - CONSTRUCTOR - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	private void atomFactory() {
		Scanner scanner;
		try {
			scanner = new Scanner(sourceFile);
			scanner.useLocale(Locale.ROOT);
			Queue<String> names = new LinkedList<String>();
			Queue<Matrix4> transforms = new LinkedList<Matrix4>();
			while (scanner.hasNext()) {
				names.add(scanner.next());
				Vector3 size = new Vector3(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());
				Vector3 position = new Vector3(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());
				Quaternion rotation = new Quaternion(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());
				transforms.add(new Matrix4(position, rotation, size)); }
			while(!transforms.isEmpty()) {
				String name = names.poll();
				Color color = null;
				if(name.matches("[A-Z][a-z].*")) {
					color = new Color(atomColors.get(name.substring(0,2))); }
				else if(name.matches("[A-Z].*")) {
					color = new Color(atomColors.get(name.substring(0,1))); }
				//else {
				//	JOptionPane.showMessageDialog(MainWindow.getInstance(), "Cannot recognize atom type from given name: "+name,
				//								 "Error", JOptionPane.ERROR_MESSAGE); }
				if(color == null) {
					color = Color.WHITE; }
				models.addAtom(name, transforms.poll(), color); } }
		catch(Exception e) {
			e.printStackTrace(); }
		models.addBound(1,2);
		models.addBound(2,3);
		models.addBound(3,0);
		models.addBound(0,1);
	}
	
	public MainEngine(File file) {
		sourceFile = file;
		mode2d = false;
		m2dFactor = 30;
		try {
			XMLDecoder xmlDecoder = new XMLDecoder(new FileInputStream("atomColors.xml"));
			atomColors = (TreeMap<String, Integer>)xmlDecoder.readObject();
			xmlDecoder.close(); }
		catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace(); }
	}
	
	/* - PUBLIC SETTINGS - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public void changeMode() {
		setMode(!mode2d); }
	
	public void setMode(boolean m2d) {
		if(mode2d^m2d) {
			mode2d = m2d; }
		else return;
		if(mode2d) {
			cameraHandler.setCamera(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), 500);
			models.scaleAll(m2dFactor); }
		else {
			cameraHandler.setCamera(new PerspectiveCamera(50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), -500);
			models.scaleAll(1f/m2dFactor);  }
	}
	
	/* - APPLICATION LISTENER- - - - - - - - - - - - - - - - - - - - - - - - */
	
	@Override
	public void create() {
		System.out.println("!!! MAIN::create()");
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, -10f, -10f, 10f, 200f));

		ModelBuilder builder = new ModelBuilder();
		//modelBatch = new ModelBatch();
		
		models = new ModelSet(builder);
		//axes = new Axes(builder, 5);
		
		Camera camera = new PerspectiveCamera(50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//Camera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0f, -20f, 0f);
		//camera.near = 0.1f;
		//camera.far = 30f;
		
		cameraHandler = new CameraHandler(camera);
		listener = new Listener(cameraHandler, this);
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		Gdx.input.setInputProcessor(listener);
		
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