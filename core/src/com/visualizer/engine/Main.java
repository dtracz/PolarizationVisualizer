package com.visualizer.engine;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.*;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Main implements ApplicationListener {
	private String sourcePath;
	
	private Environment environment;
	private Viewport viewport;
	
	private Controller controller;
	
	private AllModels modelBatch;
	//private ModelBuilder modelBuilder;
	
	private void atomFactory() {
		System.out.println("DUPPA");
		Scanner scanner;
		try {
			scanner = new Scanner(new File(sourcePath));
			Queue<String> names = new LinkedList<String>();
			Queue<Vector3> sizes = new LinkedList<Vector3>();
			Queue<Matrix4> transforms = new LinkedList<Matrix4>();
			final Vector3 ONES = new Vector3(1,1,1);
			while (scanner.hasNext()) {
				names.add(scanner.next());
				sizes.add(new Vector3(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat()));
				Vector3 postion = new Vector3(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());
				Quaternion rotation = new Quaternion(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());
				transforms.add(new Matrix4(postion, rotation, ONES));
			}
			while(!transforms.isEmpty()) {
				modelBatch.addAtom(names.poll(), sizes.poll(), transforms.poll(), Color.RED, 40);
			}
		}
		catch(Exception e) {
			e.printStackTrace(); }
		
	}
	
	public Main(String filePath) {
		sourcePath = filePath; }
	
	@Override
	public void create() {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
		environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, -10f, -10f, 10f, 200f));
		
		//modelBuilder = new ModelBuilder();
		//modelBatch = new ModelBatch();
		modelBatch = new AllModels(new ModelBuilder());
		
		Camera camera = new PerspectiveCamera(50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//Camera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0f, -20f, 0f);
		//camera.near = 0.1f;
		//camera.far = 30f;
		
		controller = new Controller(new Mouse(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), camera, modelBatch);
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), controller.getCamera());
		Gdx.input.setInputProcessor(controller);
		
		/*-----------------------------------------------------------------------------------------------------------*/
		
		atomFactory();
		//modelBatch.addAtom(4,5,6, new Matrix4(new Vector3(0,0,0), new Quaternion(), new Vector3(1,1,1)), Color.RED, 40);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	//	viewport.apply();
		controller.updateCamera();
		modelBatch.renderAll(controller.getCamera(), environment);
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
		controller.resize(width, height);
		//System.out.println(width + " " + height);
	}
}