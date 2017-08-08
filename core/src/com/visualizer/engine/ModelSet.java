package com.visualizer.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.visualizer.userInterface.MainWindow;

import javax.swing.*;
import javax.xml.bind.ValidationEvent;
import java.util.*;

/**
 * Created by dawid on 11.07.17.
 */
public class ModelSet extends ModelBatch {
	private float boundDiameter = 0.4f;
	private int boundDivisions = 16;
	private int sphereDivisionsU = 32;
	private int sphereDivisionsV = 32;
	
	private ModelBuilder builder;
	private Model sphere;
	private Model cylinder;
	
	public Axes axes;
	public List<Bound> bounds;
	public List<Atom> atoms;
	
	public ModelSet(ModelBuilder builder) {
		this.builder = builder;
		atoms = new LinkedList<Atom>();
		bounds = new LinkedList<Bound>();
		axes = new Axes(builder, 5);
		sphere = builder.createSphere(1,1,1, sphereDivisionsU, sphereDivisionsV, new Material(),
									  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		cylinder = builder.createCylinder(1, 1, 1, boundDivisions, new Material(),
										  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
	}
	
	/* - ADDITIONS - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
//	public void addAtom(String name, float w, float h, float d, Matrix4 transform, Color color, int divisions) {
//		atoms.add(new Atom(builder, name, w, h, d, transform, color, divisions)); }
	
//	public void addAtom(String name, Vector3 sizes, Matrix4 transform, Color color, int divisions) {
//		atoms.add(new Atom(builder, name, sizes.x, sizes.y, sizes.z, transform, color, divisions)); }

	public void addAtom(String name, Matrix4 transform) {
		atoms.add(new Atom(name, sphere, new Material(ColorAttribute.createDiffuse(Color.RED)), transform)); }
		
	public void addBound(int atomIndex1, int atomIndex2) {
		//try {
			bounds.add(new Bound(sphere, cylinder, atoms.get(atomIndex1), atoms.get(atomIndex2), boundDiameter)); }
		//catch(IndexOutOfBoundsException ioobe) {
		//	ioobe.printStackTrace();
		//	JOptionPane.showMessageDialog(MainWindow.getInstance(),
		//								  "Atom index out of range during bounds creation",
		//								  "Error", JOptionPane.ERROR_MESSAGE);
	//	}
	//}
	
	/* - CONFIGURATIONS- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public int getNumberOfAtoms() {
		return atoms.size(); }
	
	public void scaleAll(float factor) {
		for(Atom atom: atoms) {
			atom.scale(factor);
			atom.spread(factor); }
		axes.translate(factor);
	}
	
	public void spreadAll(float factor) {
	
	}
	
	public void translateAll(Vector3 vector) {
	
	}
	
	public void renderAll(Camera camera, Environment environment) {
		this.begin(camera);
		for(ModelInstance axis: axes.instances) {
			this.render(axis, environment); }
		for(Atom atom: atoms) {
			if(atom.renderable()) {
				for(ModelInstance instance: atom.getInstances()) {
					this.render(instance, environment); } } }
		for(Bound bound: bounds) {
			if(bound.renderable) {
				for(ModelInstance instance: bound.getInstances()) {
					this.render(instance, environment); } } }
		this.end();
	}
}