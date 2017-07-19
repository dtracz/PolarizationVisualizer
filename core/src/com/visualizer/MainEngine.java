package com.visualizer;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.*;

public class MainEngine implements ApplicationListener {
	private Environment environment;
	private Viewport viewport;
	
	private Controller controller;
	
	private AllModels modelBatch;
	//private ModelBuilder modelBuilder;
	
	private Stage stage;
	private Skin skin;
	private TextButton button;
	
	@Override
	public void create () {
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
		//Gdx.input.setInputProcessor(controller);
		
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		//stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())));
		//stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		//stage = new Stage(new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		stage = new Stage(new ScreenViewport());
		//((ScalingViewport)stage.getViewport()).setScaling(Scaling.none);
//*
		Table main = new Table();
		main.setFillParent(true);
		
		HorizontalGroup group = new HorizontalGroup();
		//group.expand(true);
		final Button tab1 = new TextButton("Tab1", skin, "toggle");
		//tab1.setWidth(50);
		//tab1.setHeight(20);
		final Button tab2 = new TextButton("Tab2", skin, "toggle");
		final Button tab3 = new TextButton("Tab3", skin, "toggle");
		group.addActor(tab1);
		group.addActor(tab2);
		group.addActor(tab3);
		group.rowTop();
		main.add(group).width(Gdx.graphics.getWidth()).height(Gdx.graphics.getHeight());//.height(100);
		//main.row();
		//main.setSize(120, 40);
		stage.addActor(main);
/*/
		button = new TextButton("click", skin, "default");
		button.setWidth(200);
		button.setHeight(50);

		stage.addActor(button);
//*/
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(controller);
		Gdx.input.setInputProcessor(inputMultiplexer);
		//Gdx.input.setInputProcessor(stage);
		/*-----------------------------------------------------------------------------------------------------------*/
		
		modelBatch.addAtom(4,5,6, new Matrix4(new Vector3(0,0,0), new Quaternion(), new Vector3(1,1,1)), Color.RED, 40);
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		viewport.apply();
		controller.updateCamera();
		modelBatch.renderAll(controller.getCamera(), environment);
		
		stage.getViewport().apply();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
	
	@Override
	public void pause() {
	
	}
	
	@Override
	public void resume() {
	
	}
	
	@Override
	public void dispose() {
	
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		viewport.update(width, height);
		controller.resize(width, height);
	}
}