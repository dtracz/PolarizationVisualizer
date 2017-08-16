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
	
	private Vector3[] positions;
	private Quaternion orientation;
	private Vector3 sizes;
	
	boolean renderable = true;
	
	public Bound(Model atomCenter, Model halfBound, Atom atom1, Atom atom2, float diameter) {
		this.blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1);
		positions = new Vector3[2];
		positions[0] = atom1.getCenter();
		positions[1] = atom2.getCenter();
		ModelInstance center1 = new ModelInstance(atomCenter, new Matrix4(positions[0], MainEngine.Q_ZERO, new Vector3(diameter, diameter, diameter)));
		ModelInstance center2 = new ModelInstance(atomCenter, new Matrix4(positions[1], MainEngine.Q_ZERO, new Vector3(diameter, diameter, diameter)));
		
		Vector3 quartLength = positions[1].cpy();
		quartLength.sub(positions[0]);
		quartLength.scl(0.25f);
		positions[0] = positions[0].add(quartLength);
		positions[1] = positions[1].sub(quartLength);
		sizes = new Vector3(diameter, 2*quartLength.len(), diameter);
		orientation = new Quaternion().setFromCross(Vector3.Y, quartLength.nor());
		ModelInstance boundPart1 = new ModelInstance(halfBound, new Matrix4(positions[0], orientation, sizes));
		ModelInstance boundPart2 = new ModelInstance(halfBound, new Matrix4(positions[1], orientation, sizes));
		
		Material material1 = atom1.getMaterial();
		center1.materials.get(0).set(material1);
		boundPart1.materials.get(0).set(material1);
		Material material2 = atom2.getMaterial();
		center2.materials.get(0).set(material2);
		boundPart2.materials.get(0).set(material2);
		
		instances = new ModelInstance[]{boundPart1, boundPart2, center1, center2}; }
	
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
		sizes.scl(factor, 1, factor);
		for(int i = 0; i < 2; i++) {
			instances[i].transform.set(positions[i], orientation, sizes); }
		for(int i = 2; i < 4; i++) {
			instances[i].transform.scl(factor); } }
	
	@Override
	public void spread(float factor) {
		sizes.scl(1, factor, 1);
		for(int i = 0; i < 2; i++) {
			instances[i].transform.set(positions[i].scl(factor), orientation, sizes); }
		for(int i = 2; i < 4; i++) {
			Vector3 position = instances[i].transform.getTranslation(Vector3.Zero);
			instances[i].transform.setTranslation(position.scl(factor)); } }
	
	@Override
	public void translate(Vector3 translation) {
		for(ModelInstance instance: instances) {
			instance.transform.setTranslation(translation); } }
	
	@Override
	public ModelInstance[] getInstances() {
		return instances; }
}
