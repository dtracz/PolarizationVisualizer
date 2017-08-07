package com.visualizer.engine;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public interface SpatialObject {
	
	boolean renderable();
	
	void changeOpacity(float opacity);
	
	void scale(float factor);
	
	void spread(float factor);
	
	void translate(Vector3 vector);
	
	ModelInstance[] getInstances();
}
