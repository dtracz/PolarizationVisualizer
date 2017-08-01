package com.visualizer.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.util.*;

/**
 * Created by dawid on 11.07.17.
 */
public class AtomBatch extends ModelBatch implements Iterable<Atom> {
    ModelBuilder builder;
    
    public Axes axes;
    public List<Atom> atoms;

    public AtomBatch(ModelBuilder builder) {
        this.builder = builder;
        atoms = new LinkedList<Atom>();
        axes = new Axes(builder, 5);
    }
    
    public void addAtom(String name, float w, float h, float d, Matrix4 transform, Color color, int divisions) {
        atoms.add(new Atom(builder, name, w, h, d, transform, color, divisions)); }
        
    public void addAtom(String name, Vector3 sizes, Matrix4 transform, Color color, int divisions) {
        atoms.add(new Atom(builder, name, sizes.x, sizes.y, sizes.z, transform, color, divisions)); }

    public void addAtom(Atom atom) {
        atoms.add(atom); }

    @Override
    public Iterator<Atom> iterator() {
        return atoms.iterator(); }
    
    public int size() {
        return atoms.size(); }
    
    public void scaleAll(float factor) {
        for(Atom atom: atoms) {
            atom.scale(factor);
            atom.translate(factor); }
        axes.translate(factor);
    }

    public void renderAll(Camera camera, Environment environment) {
        this.begin(camera);
        for(ModelInstance axis: axes.instances) {
            this.render(axis, environment); }
        for(Atom atom: atoms) {
            if(atom.renderable) {
                this.render(atom.getInstance(), environment); } }
        this.end();
    }
}
