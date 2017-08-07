package com.visualizer.engine;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Bound {
	private BlendingAttribute blendingAttribute;
	private final ModelInstance[] instances;
	
	private Atom atom1;
	private Atom atom2;
	
	private Model atomCenter;
	//private ModelInstance center1;
	//private ModelInstance center2;
	
	private Model halfBound;
	//private ModelInstance boundPart1;
	//private ModelInstance boundPart2;
	
	boolean renderable = true;
	
	private Matrix4 getTransform(Vector3 targetAxis, Vector3 position) {
		Quaternion rotation = new Quaternion();
		rotation = rotation.setFromCross(Vector3.Y, targetAxis.nor());
		return new Matrix4(position, rotation, MainEngine.ONES); }
	
	public Bound(Atom atom1, Atom atom2, float diameter, ModelBuilder builder, int divisions) {
		this.blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1);
		this.atom1 = atom1;
		this.atom2 = atom2;
		Vector3 position1 = atom1.instance.transform.getTranslation(Vector3.Zero).cpy();
		Vector3 position2 = atom2.instance.transform.getTranslation(Vector3.Zero).cpy();
		atomCenter = builder.createSphere(diameter, diameter, diameter, divisions, divisions,
										  new Material(blendingAttribute), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		ModelInstance center1 = new ModelInstance(atomCenter, position1);
		ModelInstance center2 = new ModelInstance(atomCenter, position2);
		
		Vector3 quartLength = position2.cpy();
		quartLength.sub(position1);
		quartLength.scl(0.25f);
		position1 = position1.add(quartLength);
		position2 = position2.sub(quartLength);
		halfBound = builder.createCylinder(diameter, 2*quartLength.len(), diameter, divisions,
										   new Material(blendingAttribute), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		ModelInstance boundPart1 = new ModelInstance(halfBound, getTransform(quartLength, position1));
		ModelInstance boundPart2 = new ModelInstance(halfBound, getTransform(quartLength, position2));
		
		Material material1 = atom1.instance.materials.get(0).copy();
		center1.materials.get(0).set(material1);
		boundPart1.materials.get(0).set(material1);
		Material material2 = atom2.instance.materials.get(0).copy();
		center2.materials.get(0).set(material2);
		boundPart2.materials.get(0).set(material2);
		
		instances = new ModelInstance[]{center1, center2, boundPart1, boundPart2}; }
	
	public void changeOpacity(float opacity) {
		blendingAttribute.opacity = opacity;
		for(ModelInstance instance: instances) {
			instance.materials.get(0).set(blendingAttribute); } }
		
	public ModelInstance[] getInstances() {
		return instances; }
}
