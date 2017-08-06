package com.visualizer.engine;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.*;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.io.File;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.Scanner;

public class MainEngine implements ApplicationListener {
	public final static Vector3 ONES = new Vector3(1,1,1);
	
	private Environment environment;
	private Viewport viewport;
	private CameraHandler cameraHandler;
	
	boolean mode2d;
	float m2dFactor;
	
	public File sourceFile;
	
	public Listener listener;
	public AtomBatch models;
	
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
			Queue<Vector3> sizes = new LinkedList<Vector3>();
			Queue<Matrix4> transforms = new LinkedList<Matrix4>();
			while (scanner.hasNext()) {
				names.add(scanner.next());
				sizes.add(new Vector3(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat()));
				Vector3 position = new Vector3(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());
				Quaternion rotation = new Quaternion(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());
				transforms.add(new Matrix4(position, rotation, ONES)); }
			while(!transforms.isEmpty()) {
				models.addAtom(names.poll(), sizes.poll(), transforms.poll(), Color.RED, 40); } }
		catch(Exception e) {
			e.printStackTrace(); } }
	
	public MainEngine(File file) {
		sourceFile = file;
		mode2d = false;
		m2dFactor = 30; }
	
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
			models.scaleAll(1f/m2dFactor); }
	}
	
	/* - APPLICATION LISTENER- - - - - - - - - - - - - - - - - - - - - - - - */
	
	@Override
	public void create() {
		System.out.println("!!! MAIN::create()");
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, -10f, -10f, 10f, 200f));
		
		//modelBuilder = new ModelBuilder();
		//models = new ModelBatch();
		ModelBuilder builder = new ModelBuilder();
		models = new AtomBatch(builder);
		
		Camera camera = new PerspectiveCamera(50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//Camera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0f, -20f, 0f);
		//camera.near = 0.1f;
		//camera.far = 30f;
		
		cameraHandler = new CameraHandler(camera);
		listener = new Listener(cameraHandler, this);
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		Gdx.input.setInputProcessor(listener);
		
		atomFactory();
		//models.addAtom(4,5,6, new Matrix4(new Vector3(0,0,0), new Quaternion(), new Vector3(1,1,1)), Color.RED, 40);
		/*
		Model capsule = builder.createCapsule(0.2f, 6, 20,
				new Material(ColorAttribute.createDiffuse(Color.BLUE),  new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		capsuleInstance = new ModelInstance(capsule, new Matrix4(new Vector3(3,0,0),
				                                                        new Quaternion(new Vector3(1,0,0), 90f),
				                                                        new Vector3(1,1,1)));
		*/
		Model cylinder = builder.createCylinder(0.5f,5,0.5f,8,
				new Material(ColorAttribute.createDiffuse(Color.BLUE),  new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		cylinderInstance = new ModelInstance(cylinder, new Matrix4(Vector3.Zero, new Quaternion(Vector3.Z, 45f), new Vector3(1,1,1)));
	
		Atom at1 = models.atoms.get(1);
		Atom at2 = models.atoms.get(2);
		bound = new Bound(at1, at2,0.3f, builder, 16);
		
		
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	//	viewport.apply();
		cameraHandler.updateCamera();
		models.renderAll(cameraHandler.getCamera(), environment);
	//	models.render(cylinderInstance, environment);
		for(ModelInstance inst: bound.getInstances()) { models.render(inst, environment); }
		models.end();
	}
	
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