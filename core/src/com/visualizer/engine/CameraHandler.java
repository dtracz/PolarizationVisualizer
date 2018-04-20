package com.visualizer.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.lang.reflect.Field;

/**
 * Created by dawid on 04.03.17.
 */
public class CameraHandler {
	final Quaternion rotRight = new Quaternion();
	PointLight pointLight;
	
	private final Vector3 helper;
	private final Vector3 center;                   // x, y, z
	private final Vector3 upAxis;                   // always normalized!!!
	private Camera camera;
	private float theta;                            // degrees
	
	private float scroll2D_a =  5.855f;             // HARDCODED!!!
	private float scroll2D_b = -0.062f;             // HARDCODED!!!
	private float scroll2D_c =  4.56f;              // HARDCODED!!!
	private float scroll2D_d =  8.0f;               // HARDCODED!!!
	
	private void recalculateTheta() {
		float dot = upAxis.dot(camera.direction);
		float den = (float)Math.sqrt(upAxis.len2()*camera.direction.len2());
		theta = (float)Math.toDegrees(Math.asin(dot/den)); }
	
	private void setZoom2D() {
		if(camera instanceof OrthographicCamera) {
			float dist = camera.position.dst(center);
			float zoom = (float)(scroll2D_a * Math.exp(scroll2D_b*dist + scroll2D_c) + scroll2D_d);
			((OrthographicCamera)camera).zoom = 1f/zoom; } }
	
	public CameraHandler(Camera camera, Vector3 center, Vector3 upAxis) {
		this.camera = camera;
		this.center = center;
		this.upAxis = new Vector3(upAxis).nor();
		helper = Vector3.Zero.cpy();
		camera.position.set(this.upAxis);
		camera.position.mul(MainEngine.rotationMatrix).scl(-20);
		camera.position.add(center);
		camera.up.set(this.upAxis);
		camera.near = 0.1f;
		camera.far = 3000f;
		camera.lookAt(center);
		recalculateTheta();
	}
	
	public void setCamera(Camera newCamera) {
		if(newCamera instanceof OrthographicCamera) {
			((OrthographicCamera)newCamera).setToOrtho(true); }
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
		camera = newCamera;
		setZoom2D(); }
	
	public Camera getCamera() {
		return camera; }
	
	public void updateCamera() {
		camera.update();
	}
	
	void updatePointLight() {
		helper.set(camera.direction);
		pointLight.position.set(camera.position);
		pointLight.position.sub(helper.nor().scl(10));
		rotRight.set(helper, 300);
		helper.set(camera.up).nor().scl(50);
		pointLight.position.add(rotRight.transform(helper));
	}
	
	/* - SETTINGS- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public void setUpAxis(Vector3 upAxis) {
		this.upAxis.set(upAxis).nor();
		if(!camera.direction.isOnLine(upAxis)) {
			camera.up.set(upAxis); }
		updatePointLight();
	}
	
	public void setPosition(Vector3 position) {
		camera.position.set(position);
		camera.lookAt(center);
		recalculateTheta();
		if(!camera.direction.isOnLine(upAxis)) {
			camera.up.set(upAxis); }
		setZoom2D();
		updatePointLight();
	}
	
	public void lookAt(Vector3 center) {
		this.center.set(center);
		camera.lookAt(center);
		recalculateTheta();
		if(!camera.direction.isOnLine(upAxis)) {
			camera.up.set(upAxis); }
		setZoom2D();
		updatePointLight();
	}
	
	public void lookAt(Vector3 x1, Vector3 x2, Vector3 x3) throws IllegalArgumentException {
		center.set(x3).sub(x1);
		helper.set(x2).sub(x1);
		if(helper.isOnLine(center)) {
			throw new IllegalArgumentException("Given points does not determinate a plane"); }
		helper.crs(center).nor().scl(-20);                                      // hardcoded distance!!
		center.set(x1).add(x2).add(x3).scl(1f/3f);
		setPosition(helper.add(center));
		setZoom2D();
		updatePointLight();
	}
		
	public void lookAlong(Vector3 x1, Vector3 x2) throws IllegalArgumentException {
		if(x1.equals(x2)) {
			throw new IllegalArgumentException("Given points does not determinate an axis"); }
		center.set(x2);
		setPosition(x1);
		moveForward(20);
		updatePointLight();		                                             // hardcoded distance!!
	}
	
	public void lookAlong(Vector3 direction) {
		helper.set(direction).nor().scl(-20);
		helper.add(center);
		setPosition(helper);
		updatePointLight();
	}
	
	/* - ROTATIONS - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public void rotArUp(float dir) {
		camera.rotateAround(center, upAxis, dir);
		updatePointLight();
	}
	
	public void rotUpDown(float dir) {
		if(theta + dir <= -90) {									// lock on down
			helper.set(camera.position);
			camera.position.set(upAxis).scl(helper.dst(center)).add(center);
			camera.lookAt(center);
			theta = -90f; }
		else if(theta + dir >= 90) {								// lock on up
			helper.set(camera.position);
			camera.position.set(upAxis).scl(-helper.dst(center)).add(center);
			camera.lookAt(center);
			theta = 90f; }
		else if(camera.direction.isCollinear(upAxis)) {				// unlock from down
			helper.set(upAxis).rotate(camera.up, 90f).scl(-1);
			camera.rotateAround(center, helper, dir);
			theta += dir; }
		else if(camera.direction.isCollinearOpposite(upAxis)) {		// unlock from up
			helper.set(upAxis).rotate(camera.up, 90f);
			camera.rotateAround(center, helper, dir);
			theta += dir; }
		else {
			helper.set(upAxis).crs(camera.direction).scl(-1);
			camera.rotateAround(center, helper, dir);
			theta += dir; }
		updatePointLight();
	}
	
	public void moveForward(float displacement) {
		camera.position.add(helper.set(camera.direction).scl(-displacement));
		setZoom2D();
		updatePointLight();
	}
	
	public void movePlanar(float dx, float dy) {
		helper.set(camera.up).scl(-dy);
		camera.position.add(helper);
		center.add(helper);
		helper.set(camera.direction).crs(camera.up).scl(-dx);
		camera.position.add(helper);
		center.add(helper);
		updatePointLight();
	}
		
	public void rotArDir(float dir) {
		camera.rotate(camera.direction, dir);
		upAxis.rotate(camera.direction, dir);
		updatePointLight();
	}

}