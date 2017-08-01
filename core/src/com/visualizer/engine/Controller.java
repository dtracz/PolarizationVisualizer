package com.visualizer.engine;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;

/**
 * Created by dawid on 14.03.17.
 */
public class Controller implements InputProcessor {
    private CameraHandler cameraHandler;
    private boolean mode2d;
    private float m2dFactor;
    private AtomBatch modelBatch;
    private Mouse mouse;
    private float angleSpeed;
    private float moveSpeed;
    private final float scrollParam;


    public Controller(Mouse mouse, Camera camera, AtomBatch modelBatch) {
        cameraHandler = new CameraHandler(camera);
        this.mouse = mouse;
        this.modelBatch = modelBatch;
        mode2d = false;
        m2dFactor = 30;
        angleSpeed = 1f;
        moveSpeed = 1f;
        scrollParam = 0.95f; }

    public void resize(int width, int height) {
        cameraHandler.update();
        mouse.setCenter(width, height); }

    public Camera getCamera() {
        return cameraHandler.getCamera(); }

    public void updateCamera() {
        cameraHandler.update(); }

    public void changeMode() {
        setMode(!mode2d); }
        
    public void setMode(boolean m2d) {
        if(mode2d^m2d) {
            mode2d = m2d; }
        else return;
        if(mode2d) {
            cameraHandler.setCamera(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), 500);
            modelBatch.scaleAll(m2dFactor); }
        else {
            cameraHandler.setCamera(new PerspectiveCamera(50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), -500);
            modelBatch.scaleAll(1f/m2dFactor); }
    }

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
            for(Atom at: modelBatch.atoms) {
                at.changeOpacity(0.5f);
            }
        }
        if(keycode == Input.Keys.NUM_6) {
            modelBatch.scaleAll(2);
        }
        if(keycode == Input.Keys.NUM_7) {
            setMode(!mode2d);
        }
//		if(keycode == Input.Keys.NUM_2) {
//			cameraHandler.setCamera(new OrthographicCamera(Gdx.graphics.getWidth()/50, Gdx.graphics.getHeight()/50)); }
//		if(keycode == Input.Keys.NUM_2) {
//			cameraHandler.setCamera(new PerspectiveCamera(95, Gdx.graphics.getWidth(), Gdx.graphics.getHeight())); }
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
        float param = amount==1f ? moveSpeed*scrollParam : 1/(moveSpeed*scrollParam);
        if(mode2d) {
            modelBatch.scaleAll(param); }
        else { }
            m2dFactor *= param;
            cameraHandler.move(amount*moveSpeed);
        return true; }
}