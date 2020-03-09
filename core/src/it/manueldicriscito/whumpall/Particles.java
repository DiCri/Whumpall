package it.manueldicriscito.whumpall;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Particles {
    public static class Particle {
        Vector2 pos;
        float width, widthChange;
        float height, heightChange;
        float velx, vely;
        float alpha, alphaChange;
        Color color;
        float gravity;
        int lifeTime;
        long bornTime;

        public Particle() {
            bornTime = System.currentTimeMillis();
        }

        public Particle(float width, float widthChange, float height, float heightChange, float velx, float vely, float alpha, float alphaChange, Color color, float gravity) {
            this.width = width;
            this.widthChange = widthChange;
            this.height = height;
            this.heightChange = heightChange;
            this.velx = velx;
            this.vely = vely;
            this.alpha = alpha;
            this.alphaChange = alphaChange;
            this.color = color;
            this.gravity = gravity;

            bornTime = System.currentTimeMillis();
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public void setWidthChange(float widthChange) {
            this.widthChange = widthChange;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public void setHeightChange(float heightChange) {
            this.heightChange = heightChange;
        }

        public void setVelx(float velx) {
            this.velx = velx;
        }

        public void setVely(float vely) {
            this.vely = vely;
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
        }

        public void setAlphaChange(float alphaChange) {
            this.alphaChange = alphaChange;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void setGravity(float gravity) {
            this.gravity = gravity;
        }

        public void setLifeTime(int lifeTime) { this.lifeTime = lifeTime; }
    }
    static List<Particle> particles = new ArrayList<Particle>();

    public static void render(ShapeRenderer sr, float delta) {
        update(delta);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for(Particle p : particles) {
            sr.setColor(p.color.r, p.color.g, p.color.b, p.alpha);
            sr.rect(p.pos.x-p.width/2, p.pos.y-p.height/2, p.width, p.height);
        }
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    public static void update(float dt) {
        Iterator<Particle> i = particles.iterator();
        while(i.hasNext()) {
            Particle p = i.next();
            p.width += p.widthChange*dt;
            p.height += p.heightChange*dt;
            p.alpha += p.alphaChange*dt;
            p.pos.x += p.velx*dt;
            p.pos.y += p.vely*dt;
            p.vely -= p.gravity*dt;
            if(System.currentTimeMillis()-p.bornTime>p.lifeTime || p.alpha<=0) {
                i.remove();
            }
        }
    }
    public static void trigger(float nparticles, float x, float y, Range width, Range height, Range widthChange, Range heightChange, Range velx, Range vely, Range alpha, Range alphaChange, Range gravity, Color color, int lifeTime) {
        for(int k=0; k<nparticles; k++) {
            Particle particle = new Particle();
            particle.setWidth(width.getRandom());
            particle.setHeight(height.getRandom());
            particle.setWidthChange(widthChange.getRandom());
            particle.setHeightChange(heightChange.getRandom());
            particle.setVelx(velx.getRandom());
            particle.setVely(vely.getRandom());
            particle.setAlpha(alpha.getRandom());
            particle.setAlphaChange(alphaChange.getRandom());
            particle.setGravity(gravity.getRandom());
            particle.setColor(color);
            particle.setLifeTime(lifeTime);
            particle.pos = new Vector2(x, y);
            particles.add(particle);
        }
    }
}
