package com.visualizer.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by dawid on 11.07.17.
 */
public class Axes implements SpatialObject {
	private BlendingAttribute blendingAttribute;
	private final ModelInstance[] instances;
	
	private Vector3 origin;
	private float[] lengths;
	private float headLength;
	private float diameter;
	private Quaternion[] orientations;
	private float scale;
	private float stretch;
	
	private boolean renderable;
	
	public Axes(Model cylinder, Model cone, Vector3 origin, float length, float headLength, float diameter) {
		scale = 1f;
		stretch = 1f;
		this.blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1);
		this.origin = origin;
		lengths = new float[]{length, length, length};
		this.headLength = headLength;
		this.diameter = diameter;
		orientations = new Quaternion[]{new Quaternion().setFromCross(Vector3.Y, Vector3.X),
				new Quaternion(),
				new Quaternion().setFromCross(Vector3.Y, Vector3.Z)};
		instances = new ModelInstance[6];
		createAxes(cylinder, cone);
		setTransforms();
		renderable = true; }
		
	private void createAxes(Model cylinder, Model cone) {
		int i=0;
		for(Color color: new Color[]{Color.RED, Color.GREEN, Color.BLUE}) {
			Material material = new Material(ColorAttribute.createDiffuse(color));
            instances[2*i] = new ModelInstance(cylinder);
            instances[2*i].materials.get(0).set(material);
            instances[2*i+1] = new ModelInstance(cone);
            instances[2*i+1].materials.get(0).set(material);
			i++; } }
	
	private void setTransforms() {
		for(int i=0; i<3; i++) {
			Vector3 position = new Vector3(0,0.5f,0).mul(orientations[i]);
			position.scl(lengths[i]);
			position.add(origin);
			instances[2*i].transform.set(position, orientations[i], new Vector3(diameter, lengths[i], diameter));
            position.sub(origin).scl(2f).add(origin);
            instances[2*i+1].transform.set(position, orientations[i], new Vector3(3*diameter, headLength, 3*diameter)); } }
			
	@Override
	public boolean renderable() {
		return renderable; }
	
	@Override
	public void setRenderable(boolean renderable) {
		this.renderable = renderable; }
	
	@Override
	public void changeOpacity(float opacity) {
		blendingAttribute.opacity = opacity;
		for(ModelInstance instance: instances) {
			instance.materials.get(0).set(blendingAttribute); } }
	
	@Override
	public void scale(float factor) {
		factor = factor <= 0 ? 0.001f : factor;
	    diameter *= factor/scale;
	    headLength *= factor/scale;
	    setTransforms();
		scale = factor; }
	
	@Override
	public void spread(float factor) {
		factor = factor <= 0 ? 0.001f : factor;
	    origin.scl(factor/scale);
	    for(int i=0; i<3; i++) {
	        lengths[i] *= factor/scale; }
        setTransforms();
		scale = factor; }
	
	@Override
	public void translate(Vector3 translation) {
		for(ModelInstance instance: instances) {
			instance.transform.setTranslation(translation); } }
	
	@Override
	public ModelInstance[] getInstances() {
		return instances; }
}
