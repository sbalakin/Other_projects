package com.mygdx.ShapeTest;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by DAT on 3/4/2015.
 */
public interface Shape {
    public abstract boolean isVisible(Matrix4 transform, Camera cam);

    public abstract float intersects(Matrix4 transform, Ray ray);
}