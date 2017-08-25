package com.visualizer.engine;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public interface SpatialObject {
	
	boolean renderable();
	
	void setRenderable(boolean renderable);
	
	void changeOpacity(float opacity);
	
	void scale(float factor);
	
	void spread(float factor);
	
	void translateTo(Vector3 vector);
	
	ModelInstance[] getInstances();
	
	//Vector3 getCenter();
	
	//Material getMaterial();
}
