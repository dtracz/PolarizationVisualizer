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
    private Camera camera;
    private Vector3 center;  // x, y, z
    private Float phi;
    private Float theta;
    private Field[] fields;
    private Vector3 helper;

    public CameraHandler(Camera camera) {
        camera.near = 0.1f;
        camera.far = 3000f;
        center = new Vector3(0f, 0f, 0f);
        camera.lookAt(center);
        this.camera = camera;
        phi = 270f;
        theta = 0f;
        try {
            fields = new Field[3];
            fields[0] = camera.position.getClass().getField("x");
            fields[1] = camera.position.getClass().getField("y");
            fields[2] = camera.position.getClass().getField("z"); }
        catch (NoSuchFieldException e) {
            e.printStackTrace(); }
        helper = new Vector3(0f, 0f, 0f); }

    public Camera getCamera() {
        return camera; }

    public void setCamera(Camera newCamera, float distanceChange) {
        //System.out.println(camera.position.toString());
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
        camera = newCamera;
        //System.out.println(camera.position.toString());
        //System.gc();
    }

    public void rotArZ(float dir) {
        camera.rotateAround(center, Vector3.Z, dir);
        phi += dir;}

    public void rotUpDown(float dir) {
        if(theta + dir <= -90) {
            camera.position.set(0f, 0f, -camera.position.len());
            camera.lookAt(center);
            theta = -90f; }
        else if(theta + dir >= 90) {
            camera.position.set(0f, 0f, camera.position.len());
            camera.lookAt(center);
            theta = 90f; }
        else if(camera.position.x == 0 && camera.position.y == 0) {
            helper.x = (float) Math.cos(Math.toRadians(phi - 90f));
            helper.y = (float) Math.sin(Math.toRadians(phi - 90f));
            helper.z = 0f;
            camera.rotateAround(center, helper, dir);
            theta += dir; }
        else {
            helper.x = camera.position.y;
            helper.y = -camera.position.x;
            helper.z = 0f;
            camera.rotateAround(center, helper, dir);
            theta += dir; } }

    public void rotArDir(float dir) {
        try {
            for(Field f: fields) {
                f.set(helper, f.get(camera.position)); } }
        catch(IllegalAccessException e) {
            e.printStackTrace(); }
        camera.rotateAround(center, helper, dir); }

    public void move(float displacement) {
        camera.position.scl( Math.abs(displacement + camera.position.len()) / camera.position.len() + 1e-8f ); }

    public void update() {
        camera.update(); }

//	public void update(boolean updateFrustum) {
//		camera.update(updateFrustum); }
}