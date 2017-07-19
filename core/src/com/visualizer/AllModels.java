package com.visualizer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.util.*;

/**
 * Created by dawid on 11.07.17.
 */
public class AllModels extends ModelBatch {
    ModelBuilder builder;
    Axes axes;
    Set<Atom> atoms;

    public AllModels(ModelBuilder builder) {
        this.builder = builder;
        atoms = new HashSet<Atom>();
        axes = new Axes(builder, 5);
    }

    public void addAtom(float w, float h, float d, Matrix4 transform, Color color, int divisions) {
        atoms.add(new Atom(builder, w, h, d, transform, color, divisions)); }

    public void addAtom(Atom atom) {
        atoms.add(atom); }

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
            this.render(atom.getInstance(), environment); }
        this.end();
    }
}
