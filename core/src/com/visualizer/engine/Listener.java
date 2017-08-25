package com.visualizer.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by dawid on 14.03.17.
 */
public class Listener implements InputProcessor {
	private MainEngine engine;
	private CameraHandler cameraHandler;
	private ModelSet models;
	private Mouse mouse;
	
	private float angleSpeed;
	private float moveSpeed;
	//private final float scrollParam;
	
	public Listener(MainEngine engine, CameraHandler handler, ModelSet models) {
		this.engine = engine;
		this.cameraHandler = handler;
		this.models = models;
		mouse = new Mouse(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//scrollParam = 0.95f;
		angleSpeed = 1f;
		moveSpeed = 1f; }
	
	public void resize(int width, int height) {
		mouse.setCenter(width, height); }

	/*---InputProcessor-------------------------------------------------------------------------------*/

	@Override
	public boolean keyDown(int keycode) {
		System.out.println(keycode);
		if(keycode == Input.Keys.LEFT) {
			cameraHandler.rotArUp(-1); }
		if(keycode == Input.Keys.RIGHT) {
			cameraHandler.rotArUp(1); }
		if(keycode == Input.Keys.UP) {
			cameraHandler.rotUpDown(1); }
		if(keycode == Input.Keys.DOWN) {
			cameraHandler.rotUpDown(-1); }
		if(keycode == Input.Keys.NUM_5) {
			for(Atom at: models.atoms) {
				at.changeOpacity(0.5f); } }
		if(keycode == Input.Keys.NUM_7) {
			engine.changeMode(); }
		if(keycode == Input.Keys.NUM_1) {
			models.scaleAtoms(1f/1.2f); }
		if(keycode == Input.Keys.NUM_2) {
			models.scaleAtoms(1.2f); }
		if(keycode == Input.Keys.NUM_3) {
			models.scaleBounds(1f/1.2f); }
		if(keycode == Input.Keys.NUM_4) {
			models.scaleBounds(1.2f); }
		if(keycode == Input.Keys.X) {
			cameraHandler.setUpAxis(Vector3.Z); }
		if(keycode == Input.Keys.Z) {
			cameraHandler.lookAt(Vector3.Zero, Vector3.X, Vector3.Y); }
		return true; }

	@Override
	public boolean keyUp(int keycode) {
		return false; }

	@Override
	public boolean keyTyped(char character) {
		return false; }

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		mouse.setPos(screenX, screenY);
		return true; }

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false; }

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		mouse.move(screenX, screenY);
		if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
			cameraHandler.rotArUp(-mouse.dx*angleSpeed);
			cameraHandler.rotUpDown(mouse.dy*angleSpeed); }
		else if(Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
			cameraHandler.rotArDir(mouse.dPhi*angleSpeed);
		}
		else if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			cameraHandler.movePlanar(mouse.dx*0.0286f, mouse.dy*0.0286f); }
		return true; }

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false; }
	
	@Override
	public boolean scrolled(int amount) {
		cameraHandler.moveForward(amount*moveSpeed);
		return true; }
}