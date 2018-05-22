package com.visualizer.engine;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.sun.org.apache.xpath.internal.operations.Mod;

/**
 * Created by dawid on 04.04.17.
 */
public class Atom implements SpatialObject {
	private final Vector3 helper = new Vector3();
	private Attribute mainColour;
	private BlendingAttribute blendingAttribute;
	private final ModelInstance[] instances;
	final ModelInstance[] axInstances;
	
	private Vector3 position;
	private Quaternion orientation;
	private Vector3 sizes;
	private float scale;
	private float stretch;
	
	private boolean renderable;
	
	public final String name;
	public final String description;
	public boolean visibleLabel;
	public boolean visibleAxes;
	public float opacity = 1;
	public String currentColour = "default";

	Atom(String name, Model sphereModel, Model axisModel, Attribute colour, Vector3 position, Quaternion orientation, Vector3 sizes, String alpha) {
		this.mainColour = colour;
		this.blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1);
		this.position = position;
		this.orientation = orientation;
		this.sizes = sizes;
		this.name = name;
		this.description = name + String.format(" (%.3f, %.3f, %.3f)\n\nalpha:\n", position.x, position.y, position.z) + alpha.substring(13);
		scale = 1f;
		stretch = 1f;
		ModelInstance sphereInstance = new ModelInstance(sphereModel, new Matrix4(position, orientation, sizes));
		if(name.equals("MOL")) {
			sphereInstance.userData = "MOL"; }
		ModelInstance centerInstance = new ModelInstance(sphereModel, new Matrix4(position, new Quaternion(), new Vector3(0.15f,0.15f,0.15f)));
		
		float axThickness = 0.015f;
		ModelInstance xAxis = new ModelInstance(axisModel, new Matrix4(position, orientation, new Vector3(0.995f*sizes.x, axThickness, axThickness)).rotate(new Quaternion().setFromCross(Vector3.Y, Vector3.X)));
		ModelInstance yAxis = new ModelInstance(axisModel, new Matrix4(position, orientation, new Vector3(axThickness, 0.995f*sizes.y, axThickness)));
		ModelInstance zAxis = new ModelInstance(axisModel, new Matrix4(position, orientation, new Vector3(axThickness, axThickness, 0.995f*sizes.z)).rotate(new Quaternion().setFromCross(Vector3.Y, Vector3.Z)));
		
		instances = new ModelInstance[]{sphereInstance, centerInstance};
		axInstances = new ModelInstance[]{xAxis, yAxis, zAxis};
		Material material = new Material(colour);
		instances[0].materials.get(0).set(material);
		instances[1].materials.get(0).set(material);
		axInstances[0].materials.get(0).set(ModelSet.BLACK_MATERIAL);
		axInstances[1].materials.get(0).set(ModelSet.BLACK_MATERIAL);
		axInstances[2].materials.get(0).set(ModelSet.BLACK_MATERIAL);
		
		renderable = true;
		visibleLabel = true;
		visibleAxes = true;
		this.scale(0.2f);
	}
	
	void setTexture(boolean showGrid) {
		if(showGrid) {
			instances[0].materials.get(0).set(TextureAttribute.createDiffuse(new Texture(Gdx.files.getFileHandle("grid.png", Files.FileType.Internal))));
		}
	}
	
	// @Override
	public String getDescription() {
		return description; }
	
	@Override
	public boolean renderable() {
		return renderable; }
		
	@Override
	public void setRenderable(boolean renderable) {
		this.renderable = renderable; }
	
	@Override
	public void changeOpacity(float opacity) {
		this.opacity = opacity;
		blendingAttribute.opacity = opacity;
		instances[0].materials.get(0).set(blendingAttribute);
		//for(ModelInstance instance: instances) {
		//	instance.materials.get(0).set(blendingAttribute); }
	}
	
	@Override
	public void scale(float factor) {
		factor = factor <= 0 ? 0.001f : factor;
		sizes.scl(factor/scale);
		instances[0].transform.set(position, orientation, sizes);
		
		axInstances[0].transform.scale(1, factor/scale, 1);
		axInstances[1].transform.scale(1, factor/scale, 1);
		axInstances[2].transform.scale(1, factor/scale, 1);
		
		scale = factor; }

	@Override
	public void spread(float factor) {
		factor = factor <= 0 ? 0.001f : factor;
		for(ModelInstance instance: instances) {
            Vector3 position = instance.transform.getTranslation(helper.set(0,0,0));
			instance.transform.setTranslation(position.scl(factor/stretch)); }
		stretch = factor; }
	
	@Override
	public void translateTo(Vector3 translation) {
		for(ModelInstance instance: instances) {
			instance.transform.setTranslation(translation); } }
	
	@Override
	public ModelInstance[] getInstances() {
		return instances; }
		
	public Vector3 getCenter(Vector3 origin) {
		return instances[0].transform.getTranslation(origin); }
		
	public Material getMaterial() {
		return instances[0].materials.get(0); }
		
	public void setColour(String colour) {
		if(colour.equals("gray")) {
			instances[0].materials.get(0).set(ModelSet.GRAY);
			instances[1].materials.get(0).set(ModelSet.GRAY);
		}
		else if(colour.equals("green")) {
			instances[0].materials.get(0).set(ModelSet.GREEN);
			instances[1].materials.get(0).set(ModelSet.GREEN);
		}
		else {
			instances[0].materials.get(0).set(mainColour);
			instances[1].materials.get(0).set(mainColour);
		}
		this.currentColour = colour;
	}
		
}

//public void changeColor(Color color) {
//	instance.materials.get(0).set(ColorAttribute.createDiffuse(color)); }