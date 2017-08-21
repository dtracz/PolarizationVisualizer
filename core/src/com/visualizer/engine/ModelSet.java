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
import java.util.NoSuchElementException;

/**
 * Created by dawid on 11.07.17.
 */
public class ModelSet extends ModelBatch {
	private float boundDiameter = 0.4f;										// parameters !!!
	private int cylindricalDivisions = 16;
	private int sphereDivisionsU = 32;
	private int sphereDivisionsV = 32;
	
	private Model sphere;
	private Model cylinder;
	private Model cone;
	//private Model arrow;
	
	public Axes axes;
	public List<Bond> bonds;
	public List<Atom> atoms;
	
	public ModelSet(ModelBuilder builder) {
		atoms = new LinkedList<Atom>();
		bonds = new LinkedList<Bond>();
		sphere = builder.createSphere(1,1,1, sphereDivisionsU, sphereDivisionsV, new Material(),
									  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		cylinder = builder.createCylinder(1, 1, 1, cylindricalDivisions, new Material(),
										  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		//arrow = builder.createArrow(0,0,0,0,1,0,1,0, cylindricalDivisions,
		//							4,new Material(),
		//							VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		cone = builder.createCone(1, 1, 1, cylindricalDivisions, new Material(),
								  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
	}
	
	/* - ADDITIONS - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public void addAtom(String name, Color color, Vector3 position, Quaternion orientation, Vector3 scale) {
		atoms.add(new Atom(name, sphere, new Material(ColorAttribute.createDiffuse(color)), position, orientation, scale));
	}
	
	public void addBond(String atomName1, String atomName2) throws NoSuchElementException {
		bonds.add(new Bond(sphere, cylinder, getAtom(atomName1), getAtom(atomName2), boundDiameter)); }
	
	public void createAxes(Vector3 origin, float length, float headLength, float diameter) {
		axes = new Axes(cylinder, cone, origin, length, headLength, diameter); }
	
	/* - GETTERS - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public Atom getAtom(String atomName) throws NoSuchElementException {
		for(Atom atom: atoms) {
			if(atom.name.equals(atomName)) {
				return atom; } }
		throw new NoSuchElementException("No atom with given Name"); }
	
	public int getNumberOfAtoms() {
		return atoms.size(); }
	
	public Vector3 getMoleculeCenter() {
		Vector3 center = new Vector3();
		Vector3 tmp = MainEngine.ONES;
		for(Atom atom: atoms) {
			tmp.set(0,0,0);
			atom.getCenter(tmp);
			center.add(tmp); }
		center.scl(1f/atoms.size());
		MainEngine.ONES.set(1,1,1);
		return center; }
		
	/* - CONFIGURATIONS- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public void scaleAtoms(float factor) {
		for(SpatialObject atom: atoms) {
			atom.scale(factor); } }
			
	public void scaleBounds(float factor) {
		for(SpatialObject atom: bonds) {
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
		for(Bond bond : bonds) {
			if(bond.renderable) {
				for(ModelInstance instance: bond.getInstances()) {
					this.render(instance, environment); } } }
		this.end(); }
}