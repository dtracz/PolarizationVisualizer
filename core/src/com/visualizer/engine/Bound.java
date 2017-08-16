package com.visualizer.engine;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Bound implements SpatialObject {
	private BlendingAttribute blendingAttribute;
	private final ModelInstance[] instances;
	
	private Atom atom1;
	private Atom atom2;
	
	boolean renderable = true;
	
	private Matrix4 getTransform(Vector3 targetAxis, Vector3 position, float length, float diameter) {
		Quaternion rotation = new Quaternion();
		rotation = rotation.setFromCross(Vector3.Y, targetAxis.nor());
		return new Matrix4(position, rotation, new Vector3(diameter, length, diameter)); }
		
	private Matrix4 getTransform(Vector3 position, float diameter) {
		return new Matrix4(position, new Quaternion(), new Vector3(diameter, diameter, diameter)); }
	
	public Bound(Model atomCenter, Model halfBound, Atom atom1, Atom atom2, float diameter) {
		this.blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1);
		this.atom1 = atom1;
		this.atom2 = atom2;
		Vector3 position1 = atom1.getCenter();
		Vector3 position2 = atom2.getCenter();
		ModelInstance center1 = new ModelInstance(atomCenter, getTransform(position1, diameter));
		ModelInstance center2 = new ModelInstance(atomCenter, getTransform(position2, diameter));
		
		Vector3 quartLength = position2.cpy();
		quartLength.sub(position1);
		quartLength.scl(0.25f);
		position1 = position1.add(quartLength);
		position2 = position2.sub(quartLength);
		float halfLength = 2*quartLength.len();
		ModelInstance boundPart1 = new ModelInstance(halfBound, getTransform(quartLength, position1, halfLength, diameter));
		ModelInstance boundPart2 = new ModelInstance(halfBound, getTransform(quartLength, position2, halfLength, diameter));
		
		Material material1 = atom1.getMaterial();
		center1.materials.get(0).set(material1);
		boundPart1.materials.get(0).set(material1);
		Material material2 = atom2.getMaterial();
		center2.materials.get(0).set(material2);
		boundPart2.materials.get(0).set(material2);
		
		instances = new ModelInstance[]{center1, center2, boundPart1, boundPart2}; }
	
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
	//	Quaternion rotation = new Quaternion();
	//	rotation.setFromCross(atom2.getCenter().sub(atom1.getCenter()), Vector3.Y);
	//	instances[2].transform.rotate(rotation);
	//	instances[3].transform.rotate(rotation);
		
	//	for(ModelInstance instance: instances) {
	//		instance.transform.scl(factor); }

		instances[0].transform.scl(factor);
		instances[1].transform.scl(factor);
		instances[2].transform.scl(factor, factor, factor);
		instances[3].transform.scl(factor, factor, factor);
	}
	
	@Override
	public void spread(float factor) {
		Vector3 position;
		for(ModelInstance instance: instances) {
			position = instance.transform.getTranslation(Vector3.Zero);
			instance.transform.setTranslation(position.scl(factor)); }
		
	//	Quaternion rotation = new Quaternion();
	//	rotation.setFromCross(Vector3.Y, atom2.getCenter().sub(atom1.getCenter()));
	//	instances[2].transform.rotate(rotation);
	//	instances[3].transform.rotate(rotation);
	//	instances[2].transform.scl(1, factor, 1);
	//	instances[3].transform.scl(1, factor, 1);
	}
	
	@Override
	public void translate(Vector3 translation) {
		for(ModelInstance instance: instances) {
			instance.transform.setTranslation(translation); } }
	
	@Override
	public ModelInstance[] getInstances() {
		return instances; }
}
