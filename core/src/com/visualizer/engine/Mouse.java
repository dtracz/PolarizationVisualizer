package com.visualizer.engine;

/**
 * Created by dawid on 06.03.17.
 */
public class Mouse {
    private float halfWidth;
    private float halfHeight;
    private float oldX;
    private float oldY;
    public float dy;
    public float dPhi;
    public float dx;
//	public float r;

    public Mouse(int width, int height) {
        halfWidth = width/2;
        halfHeight = height/2;
        oldX = 0f;
        oldY = 0f;
        dx = 0f;
        dy = 0f; }

    public void setCenter(int width, int height) {
        halfWidth = width/2;
        halfHeight = height/2; }

    public void setPos(float x, float y) {
        oldX = x - halfWidth;
        oldY = halfHeight - y; }

    public void move(float x, float y) {
        x = x - halfWidth;
        y = halfHeight - y;
        dx = x - oldX;
        dy = y - oldY;
        dPhi = (float) ((180f/Math.PI)*(x*dy - y*dx)/(Math.pow(x,2) + Math.pow(y,2)));
//		r = (float) Math.sqrt(Math.pow(x, 2) +  Math.pow(y, 2));
        oldX = x;
        oldY = y; }
}