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
	private Vector3 center;	// x, y, z
	private double phi;		// radians
	private float theta;	// degrees
	
	private float scrollParam2D;

	public CameraHandler(Camera camera, Vector3 center) throws IllegalArgumentException {
		helper = camera.position.cpy();
		helper.sub(center);
		if(helper.isOnLine(Vector3.Z)) {
			phi = 3*Math.PI/2; }
		this.camera = camera;
		this.center = center;
		camera.near = 0.1f;
		camera.far = 3000f;
		camera.lookAt(center);
		countAngles();
		scrollParam2D = 0.962f; }
	
	private boolean countAngles() {
		theta = (float)Math.toDegrees(Math.asin(camera.direction.z/camera.direction.len()));
		if(!camera.direction.isOnLine(Vector3.Z)) {
			phi = Math.atan2(camera.direction.y, camera.direction.x) + Math.PI;
			return true; }
		return false; }
	
	public void setCamera(Camera newCamera, float distanceChange) {
		if(newCamera instanceof OrthographicCamera) {
			((OrthographicCamera) newCamera).setToOrtho(true); }
		for(Field field: Camera.class.getFields()) {
			try {
				if(field.getType() == Matrix4.class) {
					((Matrix4)field.get(newCamera)).set((Matrix4) field.get(camera)); }
				else if(field.getType() == Vector3.class) {
					((Vector3)field.get(newCamera)).set((Vector3) field.get(camera)); }
				else if(field.getType().isPrimitive()){
					field.set(newCamera, field.get(camera)); } }
			catch(IllegalAccessException iae) {
				iae.printStackTrace(); } }
		newCamera.position.add(camera.position.nor().scl(distanceChange));
		newCamera.lookAt(center);
		camera = newCamera;
		countAngles(); }

	public Camera getCamera() {
		return camera; }
	
	public void lookAt(Vector3 center) {
		//System.out.println(Math.toDegrees(phi) +" "+ theta);
		helper.set(center).sub(camera.position);
		float dPhi = (float)Math.toDegrees(Math.atan2(helper.y, helper.x) + Math.PI - phi);
		float dTheta = (float)Math.toDegrees(Math.asin(helper.z/helper.len())) - theta;
		camera.rotate(Vector3.Z, dPhi);
		//if(!countAngles()) { phi += Math.toRadians(dPhi); }
		phi += Math.toRadians(dPhi);
		theta += dTheta;
		camera.rotate(dTheta, -camera.position.y, camera.position.x, 0);
		//System.out.println(dPhi +" "+ dTheta);
		//System.out.println(Math.toDegrees(phi) +" "+ theta);
		camera.lookAt(center);
		this.center.set(center); }
		
	public void setCenter(float x, float y, float z) {
		this.center.set(x, y, z);
		camera.lookAt(center); }

	public void rotArZ(float dir) {
		helper.set(Vector3.Z);
		camera.rotateAround(center, helper, dir);
		phi += Math.toRadians(dir);}

	public void rotUpDown(float dir) {
		if(theta + dir <= -90) {
			helper.set(camera.position).sub(center);
			camera.position.set(0f, 0f, helper.len()).add(center);
			camera.lookAt(center);
			theta = -90f; }
		else if(theta + dir >= 90) {
			helper.set(camera.position).sub(center);
			camera.position.set(0f, 0f, -helper.len()).add(center);
			camera.lookAt(center);
			theta = 90f; }
		else if(camera.position.x == center.x && camera.position.y == center.y) {
			helper.x = (float)Math.cos(phi + Math.PI/2);
			helper.y = (float)Math.sin(phi + Math.PI/2);
			helper.z = 0f;
			camera.rotateAround(center, helper, dir);
			theta += dir; }
		else {
			helper.x = camera.direction.y;
			helper.y = -camera.direction.x;
			helper.z = 0;
			camera.rotateAround(center, helper, dir);
			theta += dir; } }

	public void rotArDir(float dir) {
		camera.rotateAround(center, camera.direction, dir); }

	public float moveForward(float displacement) {
		float factor = Math.abs( displacement > 0 ? (displacement*scrollParam2D) : 1/(displacement*scrollParam2D) );
		camera.position.add(helper.set(camera.direction).scl(-displacement));
		if(camera instanceof OrthographicCamera) {
			((OrthographicCamera)camera).zoom /= factor; }
		return factor; }
		
	public void movePlanar(float dx, float dy) {
		float dX = (float)Math.cos(phi - (Math.PI/2))*dx;
		float dY = (float)Math.sin(phi - (Math.PI/2))*dx;
		dX += Math.cos(phi - Math.PI)*Math.sin(Math.toRadians(theta))*dy;
		dY += Math.sin(phi - Math.PI)*Math.sin(Math.toRadians(theta))*dy;
		float dZ = -(float)Math.cos(Math.toRadians(theta))*dy;
		camera.position.add(dX, dY, dZ);
		center.add(dX, dY, dZ);
		camera.lookAt(center); }
		
	public void updateCamera() {
		camera.update(); }

//	public void update(boolean updateFrustum) {
//		camera.update(updateFrustum); }
}