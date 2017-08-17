package com.visualizer.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dawid on 11.07.17.
 */
public class ModelSet extends ModelBatch {
	private float boundDiameter = 0.4f;
	private int cylindricDivisions = 16;
	private int sphereDivisionsU = 32;
	private int sphereDivisionsV = 32;
	
	private Model sphere;
	private Model cylinder;
	private Model cone;
	private Model arrow;
	
	public Axes axes;
	public List<Bound> bounds;
	public List<Atom> atoms;
	
	public ModelSet(ModelBuilder builder) {
		atoms = new LinkedList<Atom>();
		bounds = new LinkedList<Bound>();
		//axes = new Axes(builder, 5);
		sphere = builder.createSphere(1,1,1, sphereDivisionsU, sphereDivisionsV, new Material(),
									  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		cylinder = builder.createCylinder(1, 1, 1, cylindricDivisions, new Material(),
										  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		//arrow = builder.createArrow(0,0,0,0,1,0,1,0, cylindricalDivisions,
		//							4,new Material(),
		//							VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		cone = builder.createCone(1, 1, 1, cylindricDivisions, new Material(),
								  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		axes = new Axes(cylinder, cone, new Vector3(-5, -5, -5), 10, 1, 0.2f);
	}
	
	/* - ADDITIONS - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

	public void addAtom(String name, Color color, Vector3 position, Quaternion orientation, Vector3 scale) {
		atoms.add(new Atom(name, sphere, new Material(ColorAttribute.createDiffuse(color)), position, orientation, scale)); }
		
	public void addBound(int atomIndex1, int atomIndex2) {
		bounds.add(new Bound(sphere, cylinder, atoms.get(atomIndex1), atoms.get(atomIndex2), boundDiameter)); }
	
	/* - CONFIGURATIONS- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public int getNumberOfAtoms() {
		return atoms.size(); }
	
	public void scaleAtoms(float factor) {
		for(SpatialObject atom: atoms) {
			atom.scale(factor); } }
			
	public void scaleBounds(float factor) {
		for(SpatialObject atom: bounds) {
			atom.scale(factor); } }
	
	public void spreadAll(float factor) {
	
	}
	
	public void translateAll(Vector3 vector) {
	
	}
	
	public void renderAll(Camera camera, Environment environment) {
		this.begin(camera);
		if(axes.renderable()) {
			for(ModelInstance instance: axes.getInstances()) {
				this.render(instance, environment); } }
		for(Atom atom: atoms) {
			if(atom.renderable()) {
				for(ModelInstance instance: atom.getInstances()) {
					this.render(instance, environment); } } }
		for(Bound bound: bounds) {
			if(bound.renderable) {
				for(ModelInstance instance: bound.getInstances()) {
					this.render(instance, environment); } } }
		this.end(); }
}