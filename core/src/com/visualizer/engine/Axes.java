package com.visualizer.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by dawid on 11.07.17.
 */

public class Axes {
    Model[] models;
    ModelInstance[] instances;

    public Axes(ModelBuilder builder, float startScale) {
        models = new Model[3];
        models[0] = builder.createArrow(-startScale,-startScale,-startScale, startScale,-startScale,-startScale,
                0.06f,0.33f, 16, 4,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal );

        models[1] = builder.createArrow(-startScale,-startScale,-startScale, -startScale,startScale,-startScale,
                0.06f,0.33f, 16, 4,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal );

        models[2] = builder.createArrow(-startScale,-startScale,-startScale, -startScale,-startScale,startScale,
                0.06f,0.33f, 16, 4,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal );
        instances = new ModelInstance[3];
        for(int i=0; i<3; i++) {
            instances[i] = new ModelInstance(models[i]); }
    }

    public void translate(float factor) {
        for(int i=0; i<3; i++) {
            //Vector3 position = instances[i].transform.getTranslation(Vector3.Zero);
            //instances[i].transform.setTranslation(position.scl(0));
            instances[i].transform.scl(factor); } }
}
