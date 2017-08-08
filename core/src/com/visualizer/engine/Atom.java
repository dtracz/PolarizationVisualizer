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
public class Atom implements SpatialObject {
    private boolean renderable;
    
    BlendingAttribute blendingAttribute;
    ModelInstance[] instances;
    
    //final public String symbol;
    public String name = "";

    Atom(String name, Model model, Material material, Matrix4 transform) {
        //this.symbol = "";
        this.name = name;
        this.blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1);
        ModelInstance sphereInstance = new ModelInstance(model, transform);
        sphereInstance.materials.get(0).set(material);
        instances = new ModelInstance[] {sphereInstance};
        renderable = true; }
    
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
        for(ModelInstance instance: instances) {
            instance.transform.scl(factor); } }

    @Override
    public void spread(float factor) {
        Vector3 position;
        for(ModelInstance instance: instances) {
            position = instance.transform.getTranslation(Vector3.Zero);
            instance.transform.setTranslation(position.scl(factor)); } }
    
    @Override
    public void translate(Vector3 translation) {
        for(ModelInstance instance: instances) {
            instance.transform.setTranslation(translation); } }
    
    @Override
    public ModelInstance[] getInstances() {
        return instances; }
        
    //@Override
    public Vector3 getCenter() {
        return instances[0].transform.getTranslation(Vector3.Zero).cpy(); }
        
    //@Override
    public Material getMaterial() {
        return instances[0].materials.get(0); }
    
}

//public void changeColor(Color color) {
//    instance.materials.get(0).set(ColorAttribute.createDiffuse(color)); }