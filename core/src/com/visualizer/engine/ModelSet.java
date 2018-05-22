package com.visualizer.engine;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.visualizer.userInterface.MainWindow;

import javax.swing.text.DefaultCaret;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Created by dawid on 11.07.17.
 */
public class ModelSet extends ModelBatch {
	final static Attribute GRAY = ColorAttribute.createDiffuse(0.5f,0.5f,0.5f,1);
	final static Attribute GREEN = ColorAttribute.createDiffuse(093f/255f, 198f/255f, 023f/255f, 1f);
	final TextureAttribute dashedTexture = TextureAttribute.createDiffuse(new Texture(Gdx.files.getFileHandle(MainWindow.selfPath+"/stripes.jpg", Files.FileType.Internal)));
	
	private final Vector3 helper = new Vector3();
	final static Material BLACK_MATERIAL = new Material(ColorAttribute.createDiffuse(0,0,0,1));
	private float boundDiameter = 0.064f;										// parameters !!!
	private int cylindricalDivisions = 16;
	private int sphereDivisionsU = 32;
	private int sphereDivisionsV = 32;
	
	private Model sphere;
	private Model cylinder;
	private Model cone;
	//private Model arrow;
	
	public Axes axes;
	public ArrayList<Bond> bonds;
	public boolean visibleBonds;
	public ArrayList<Atom> atoms;
	public boolean visibleAtoms;
	
	private final SpriteBatch batch = new SpriteBatch();
	BitmapFont font;
	public boolean visibleLabels;
	
	private float startWidth;
	private float startHeight;
	
	volatile boolean fontChanged = false;
	
	FreeTypeFontGenerator generator;
	FreeTypeFontGenerator.FreeTypeFontParameter parameter;
	
	public ModelSet(ModelBuilder builder) {
		super(new DefaultRenderableSorter() { // make last instance (molecular sphere being render at the end, co everything inside could be visible;
			@Override
			public void sort(Camera camera, Array<Renderable> renderables) {
				Renderable last = renderables.pop();
				if(last.userData != null && last.userData.equals("MOL")) {
					super.sort(camera, renderables);
					renderables.add(last);
				}
				else {
					renderables.add(last);
					super.sort(camera, renderables);
				}
			}
		});
		
		atoms = new ArrayList<Atom>();
		bonds = new ArrayList<Bond>();
		sphere = builder.createSphere(1,1,1, sphereDivisionsU, sphereDivisionsV, new Material(),
									  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
		cylinder = builder.createCylinder(1, 1, 1, cylindricalDivisions, new Material(),
										  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
		//arrow = builder.createArrow(0,0,0,0,1,0,1,0, cylindricalDivisions,
		//							4,new Material(),
		//							VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		cone = builder.createCone(1, 1, 1, cylindricalDivisions, new Material(),
								  VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		
		generator = new FreeTypeFontGenerator(Gdx.files.internal(MainWindow.selfPath + "/font.ttf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 24;
		font = generator.generateFont(parameter);
		
//		font = new BitmapFont(Gdx.files.internal("TitilliumWeb-SemiBold.fnt"));
		font.setColor(Color.BLACK);
		startWidth = Gdx.graphics.getWidth();
		startHeight = Gdx.graphics.getHeight();
		visibleAtoms = true;
		visibleBonds = true;
		visibleLabels = true;
		
	}
	
	/* - ADDITIONS - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public void addAtom(String name, Color color, Vector3 position, Quaternion orientation, Vector3 scale, String alpha) {
		atoms.add(new Atom(name, sphere, cylinder, ColorAttribute.createDiffuse(color), position, orientation, scale, alpha));
	}
	
	public void addBond(String atomName1, String atomName2, boolean striped, String bondpol) throws NoSuchElementException {
		bonds.add(new Bond(sphere, cylinder, getAtom(atomName1), getAtom(atomName2), boundDiameter, striped, bondpol, dashedTexture)); }
	
	public void createAxes(Vector3 origin, float length, float headLength, float diameter) {
		axes = new Axes(cylinder, cone, origin, length, headLength, diameter); }
	
	/* - GETTERS - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public Atom getAtom(String atomName) throws NoSuchElementException {
		for(Atom atom: atoms) {
			if(atom.name.equals(atomName)) {
				return atom; } }
		throw new NoSuchElementException("No atom with given name: " + atomName); }
	
	public int getNumberOfAtoms() {
		return atoms.size(); }
	
//	public Vector3 getMoleculeCenter() {
//		Vector3 center = new Vector3();
//		for(Atom atom: atoms) {
//			helper.set(0,0,0);
//			atom.getCenter(helper);
//			center.add(helper); }
//		center.scl(1f/atoms.size());
//		return center; }
		
	/* - CONFIGURATIONS- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	public void show(boolean isVisible, String objects) {
		if(objects.equals("ATOMS")) {
			visibleAtoms = isVisible; }
		if(objects.equals("BONDS")) {
			visibleBonds = isVisible; }
		if(objects.equals("AXES")) {
			axes.setRenderable(isVisible); }
	}
	
	public void scale(float factor, String objects) {
		if(objects.equals("ATOMS")) {
			for(int i=1; i<atoms.size(); i++) {
				atoms.get(i).scale(factor); } }
		if(objects.equals("BONDS")) {
			for(SpatialObject bond: bonds) {
				bond.scale(factor); } }
		if(objects.equals("AXES")) {
			axes.scale(factor); }
	}
	
	public void changeOpacity(float opacity, String objects) {
		if(objects.equals("ATOMS")) {
			for(int i=1; i<atoms.size(); i++) {
				atoms.get(i).changeOpacity(opacity); } }
		if(objects.equals("BONDS")) {
			for(SpatialObject bond: bonds) {
				bond.changeOpacity(opacity); } }
	}
	
	public void scaleAtoms(float factor) {
		for(SpatialObject atom: atoms) {
			atom.scale(factor); } }
	
	public void scaleBounds(float factor) {
		for(SpatialObject atom: bonds) {
			atom.scale(factor); }
	}

	public void setAxesPosition(Vector3 position) {
		axes.translateTo(position);
	}
	
	public void spreadAll(float factor) {
	
	}
	
	public void translateAll(Vector3 vector) {
	
	}
	
	public void renderAll(Camera camera, Environment environment) {
		this.begin(camera);
		if(axes.renderable()) {
			for(ModelInstance instance: axes.getInstances()) {
				this.render(instance, environment); }
		}
		if(visibleBonds) {
			for(Bond bond : bonds) {
				if(bond.renderable) {
					for(ModelInstance instance: bond.getInstances()) {
						this.render(instance, environment); } } }
		}
		if(visibleAtoms) {
			// all except first (MOL)
			for(int i=1; i<atoms.size(); i++) {
				if(atoms.get(i).renderable()) {
					for(ModelInstance instance: atoms.get(i).getInstances()) {
						this.render(instance, environment); }
					if(atoms.get(i).visibleAxes) {
						for(ModelInstance instance: atoms.get(i).axInstances) {
							this.render(instance, environment); } }
				}
			}
		}
		if(atoms.get(0).renderable()) {
			if(atoms.get(0).visibleAxes) {
				for(ModelInstance instance: atoms.get(0).axInstances) {
					this.render(instance, environment); } }
			// // set molecular sphere as last element to simplify popping it in RenderableSorter;
			// ModelInstance[] instances = atoms.get(0).getInstances();
			// for(int i=instances.length-1; i>=0; i--) {
			// 	this.render(instances[i], environment); }
			this.render(atoms.get(0).getInstances()[0], environment);
		}
		this.end();
		
		if(visibleLabels) {
			if(fontChanged) {
				font = generator.generateFont(parameter);
				fontChanged = false;
			}
			batch.begin();
			for(Atom atom: atoms) {
				if(atom.visibleLabel) {
					atom.getCenter(helper.set(0,0,0));
					camera.project(helper, 0, 0, startWidth, startHeight);
					font.draw(batch, atom.name, helper.x, helper.y); }
			}
			if(axes.renderable()) {
				helper.set(axes.origin);
				helper.add(axes.lengths[0], 0, 0);
				camera.project(helper, 0, 0, startWidth, startHeight);
				font.draw(batch, "X", helper.x, helper.y);
				helper.set(axes.origin);
				helper.add(0, axes.lengths[1], 0);
				camera.project(helper, 0, 0, startWidth, startHeight);
				font.draw(batch, "Y", helper.x, helper.y);
				helper.set(axes.origin);
				helper.add(0, 0, axes.lengths[2]);
				camera.project(helper, 0, 0, startWidth, startHeight);
				font.draw(batch, "Z", helper.x, helper.y);
			}
			batch.end();
		}
	}

}