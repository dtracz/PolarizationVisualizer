package com.visualizer.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.lang.reflect.Field;

/**
 * Created by dawid on 04.03.17.
 */
public class CameraHandler {
	private final Vector3 helper;
	private Camera camera;
	private Vector3 center;  // x, y, z
	private Float phi;
	private Float theta;
	
	private float scrollParam;

	public CameraHandler(Camera camera) {
		camera.near = 0.1f;
		camera.far = 3000f;
		center = new Vector3(0f, 0f, 0f);
		camera.lookAt(center);
		this.camera = camera;
		phi = 270f;
		theta = 0f;
		helper = new Vector3(0f, 0f, 0f);
		scrollParam = 0.962f; }
	
	private void setPhi() {
	
	}
	
	public void setCamera(Camera newCamera, float distanceChange) {
		if(newCamera instanceof OrthographicCamera) {
			((OrthographicCamera) newCamera).setToOrtho(true); }
		for(Field field: Camera.class.getFields()) {
			try {
				if(field.getType() == Matrix4.class) {
					((Matrix4) field.get(newCamera)).set((Matrix4) field.get(camera)); }
				else if(field.getType() == Vector3.class) {
					((Vector3) field.get(newCamera)).set((Vector3) field.get(camera)); }
				else if(field.getType().isPrimitive()){
					field.set(newCamera, field.get(camera)); } }
			catch(IllegalAccessException iae) {
				iae.printStackTrace(); } }
		newCamera.position.add(camera.position.nor().scl(distanceChange));
		newCamera.lookAt(center);
		camera = newCamera; }

	public Camera getCamera() {
		return camera; }
	
	public void setCenter(Vector3 center) {
		this.center = center;
		camera.lookAt(center); }
		
	public void setCenter(float x, float y, float z) {
		this.center.set(x, y, z);
		camera.lookAt(center); }

	public void rotArZ(float dir) {
		helper.set(Vector3.Z);
		camera.rotateAround(center, helper, dir);
		phi += dir;}

	public void rotUpDown(float dir) {
		if(theta + dir <= -90) {
			helper.set(camera.position).sub(center);
			camera.position.set(0f, 0f, -helper.len()).add(center);
			camera.lookAt(center);
			theta = -90f; }
		else if(theta + dir >= 90) {
			helper.set(camera.position).sub(center);
			camera.position.set(0f, 0f, helper.len()).add(center);
			camera.lookAt(center);
			theta = 90f; }
		else if(camera.position.x == center.x && camera.position.y == center.y) {
			helper.x = (float) Math.cos(Math.toRadians(phi - 90f));
			helper.y = (float) Math.sin(Math.toRadians(phi - 90f));
			helper.z = 0f;
			camera.rotateAround(center, helper, dir);
			theta += dir; }
		else {
			helper.x = -camera.direction.y;
			helper.y = camera.direction.x;
			helper.z = 0;
			camera.rotateAround(center, helper, dir);
			theta += dir; } }

	public void rotArDir(float dir) {
		helper.set(camera.position);
		camera.rotateAround(center, helper.sub(center), dir); }

	public float move(float displacement) {
		float factor = Math.abs( displacement > 0 ? (displacement*scrollParam) : 1/(displacement*scrollParam) );
		camera.position.add(helper.set(camera.direction).scl(-displacement));
		if(camera instanceof OrthographicCamera) {
			((OrthographicCamera)camera).zoom /= factor; }
		return factor; }

	public void updateCamera() {
		camera.update(); }

//	public void update(boolean updateFrustum) {
//		camera.update(updateFrustum); }
}