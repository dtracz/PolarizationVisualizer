package com.visualizer.engine;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.jet.math.Functions;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
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
	
	public String name;
	
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
	private AtomFactory atomFactory;
	public ModelSet models;
	
	private int fontSize = 24;
	private final boolean longImport;
	
	/* - PUBLIC OPERATIONS - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public void showMessage(final String text, final String type) {
		Thread messageThread = new Thread(new Runnable(){
			@Override
			public void run() {
				JOptionPane.showMessageDialog(MainWindow.getInstance(), text, type, JOptionPane.ERROR_MESSAGE); } } );
		messageThread.start();
	}
	
	public void changeMode() {
		setMode2D(!mode2d);
	}
	
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
		viewport.apply();
		cameraHandler.updateCamera();
		byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
		BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
		PixmapIO.writePNG(Gdx.files.absolute(filename), pixmap);
		pixmap.dispose();
	}
	
	public void setFont(int fontSize) {
		this.fontSize = fontSize;
		models.parameter.size = (int)(4*fontSize*Math.min(720f/Gdx.graphics.getHeight(), 1280f/Gdx.graphics.getWidth()));
		models.parameter.color = Color.BLACK;
		models.fontChanged = true;
	}
	
	
	/* - CONSTRUCTOR - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public MainEngine(File file, boolean longImport) {
		this.longImport = longImport;
		sourceFile = file;
		name = file.getName();
		mode2d = false;
	}
	
	
	@Override
	public void create() {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		ModelBuilder builder = new ModelBuilder();
		models = new ModelSet(builder);
		atomFactory = new AtomFactory(models);
		Vector3 origin = null;
		try {
			origin = atomFactory.parseFile(sourceFile); }
		catch(Exception e) {
			showMessage(e.getMessage(), "Error");
			e.printStackTrace(); }
		models.createAxes(origin.sub(ONES), 5, 0.8f, 0.1f);
		
		Vector3 center = new Vector3(0,0,0);
		models.atoms.get(0).getCenter(center);
		perspectiveCamera = new PerspectiveCamera(50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		orthographicCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cameraHandler = new CameraHandler(perspectiveCamera, center, defaultUpAxis);
		
		cameraHandler.pointLight = new PointLight().set(Color.WHITE, origin.cpy().add(0, 0, 0), 1200f);
		environment.add(cameraHandler.pointLight);
		cameraHandler.updatePointLight();
		
		listener = new Listener(this, cameraHandler, models);
		Gdx.input.setInputProcessor(listener);
		
		// setting top subframe needs to wait for this thread to have finished; this is the simplest way
		MainWindow.getInstance().resetTopSubframe();

//		Gdx.graphics.setResizable(true);
		System.out.println(Gdx.graphics.getBufferFormat().coverageSampling);
		System.out.println(Gdx.graphics.getBufferFormat().samples);
	}
	
	/* - FORWARD APPLICATION LISTENER- - - - - - - - - - - - - - - - - - - - */
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
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
		// models.dispose(); // it should be here but if it is sometimes app crushes when one multiple subframe is closed
	}
	
	@Override
	public void resize(int width, int height) {
		models.parameter.size = (int)(4*fontSize*Math.min(720f/Gdx.graphics.getHeight(), 1280f/Gdx.graphics.getWidth()));
		models.parameter.color = Color.BLACK;
		models.font = models.generator.generateFont(models.parameter);
		
		viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		listener.resize(width, height);
		cameraHandler.updateCamera();
	}
	
}