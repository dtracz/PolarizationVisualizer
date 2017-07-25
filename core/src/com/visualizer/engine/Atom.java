package com.visualizer.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by dawid on 04.04.17.
 */
public class Atom {
    BlendingAttribute blendingAttribute;
    Model atom;
    ModelInstance instance;
    
    public String name = "";
    public boolean renderable;

    public Atom(ModelBuilder builder, float w, float h, float d, Matrix4 transform, Color color, int divisions) {
        this.blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1);
        this.atom = builder.createSphere(w, h, d, divisions, divisions,
                new Material(ColorAttribute.createDiffuse(color), blendingAttribute),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal );
        this.instance = new ModelInstance(atom, transform);
        renderable = true; }

    public void changeOpacity(float opacity) {
        instance.materials.get(0).set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, opacity)); }

    public void changeColor(Color color) {
        instance.materials.get(0).set(ColorAttribute.createDiffuse(color)); }

    public void scale(float factor) {
        instance.transform.scl(factor); }

    public void translate(float factor) {
        Vector3 position = instance.transform.getTranslation(Vector3.Zero);
        instance.transform.setTranslation(position.scl(factor)); }

    public ModelInstance getInstance() {
        return instance; }
}