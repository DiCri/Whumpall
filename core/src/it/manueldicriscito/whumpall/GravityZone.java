package it.manueldicriscito.whumpall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.ShortArray;

class GravityZone {
    int gravity;
    Polygon polygon;

    private float[] vertices;
    private ShortArray triangleIndices;

    GravityZone() {
        gravity = 1500;
        polygon = new Polygon();
        vertices = null;
        triangleIndices = null;
    }
    void setVertices(float[] array) {
        this.polygon.setVertices(array);

        vertices = polygon.getTransformedVertices();
        triangleIndices = new EarClippingTriangulator().computeTriangles(vertices);

    }
    void render(ShapeRenderer sr) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ZERO);

        sr.setColor(Assets.gravityZoneColor);
        for (int i = 0; i < triangleIndices.size; i += 3) {
            sr.triangle(
                    vertices[triangleIndices.get(i) * 2], vertices[triangleIndices.get(i) * 2 + 1],
                    vertices[triangleIndices.get(i + 1) * 2], vertices[triangleIndices.get(i + 1) * 2 + 1],
                    vertices[triangleIndices.get(i + 2) * 2], vertices[triangleIndices.get(i + 2) * 2 + 1]
            );
        }
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
