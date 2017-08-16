package com.visualizer.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by dawid on 14.03.17.
 */
public class Listener implements InputProcessor {
	private MainEngine engine;
	private CameraHandler cameraHandler;
	private Mouse mouse;
	
	private float angleSpeed;
	private float moveSpeed;
	private final float scrollParam;

	public Listener(CameraHandler handler, MainEngine engine) {
		this.engine = engine;
		cameraHandler = handler;
		mouse = new Mouse(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		angleSpeed = 1f;
		moveSpeed = 1f;
		scrollParam = 0.95f; }

	public void resize(int width, int height) {
		mouse.setCenter(width, height); }

	/*---InputProcessor-------------------------------------------------------------------------------*/

	@Override
	public boolean keyDown(int keycode) {
		System.out.println(keycode);
		if(keycode == Input.Keys.LEFT) {
			cameraHandler.rotArZ(-1); }
		if(keycode == Input.Keys.RIGHT) {
			cameraHandler.rotArZ(1); }
		if(keycode == Input.Keys.UP) {
			cameraHandler.rotUpDown(1); }
		if(keycode == Input.Keys.DOWN) {
			cameraHandler.rotUpDown(-1); }
		if(keycode == Input.Keys.NUM_5) {
			for(Atom at: engine.models.atoms) {
				at.changeOpacity(0.5f); } }
		if(keycode == Input.Keys.NUM_6) {
			engine.models.scaleAll(2); }
		if(keycode == Input.Keys.NUM_7) {
			engine.changeMode(); }
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
		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			cameraHandler.rotArZ(-mouse.dx*angleSpeed);
			cameraHandler.rotUpDown(-mouse.dy*angleSpeed); }
		else if(Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
			cameraHandler.rotArDir(-mouse.dPhi*angleSpeed); }
		return true; }

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false; }
	
	@Override
	public boolean scrolled(int amount) {
		float factor = cameraHandler.move(amount*moveSpeed);
		engine.m2dFactor *= factor;
		return true; }
}