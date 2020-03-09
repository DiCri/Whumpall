package it.manueldicriscito.whumpall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import static it.manueldicriscito.whumpall.Whumpall.PAD_DIR_NONE;
import static it.manueldicriscito.whumpall.Whumpall.globalVars;


public class Platform {

    public int dir;
    public boolean falling;
    public Rectangle rect;
    public Animations.AnimatableRectangle arect;
    public Vector2 vel;
    public int type;
    public boolean added;
    public boolean fixed;
    private int gravity;
    public int gravityChange;


    public Platform() {
        rect = new Rectangle();
        vel = new Vector2(0, 0);
        dir = PAD_DIR_NONE;
        arect = null;
        falling = false;
        gravity = 1500;
        fixed = false;
        gravityChange = 1;
    }
    public void update(float delta) {
        if(added) {
            if(arect!=null) {
                if(falling) {
                    arect.y.sub(vel.y*delta);
                    vel.y += gravity*delta;
                }
                rect.set(arect.getX(), arect.getY(), arect.getWidth(), arect.getHeight());
            } else if(falling) {
                rect.y -= vel.y*delta;
                vel.y += gravity*delta;
            }
        }
    }
    public void fall() {
        if(!this.fixed) this.falling = true;
    }

    public void fix() {
        if(rect.width<0) {
            rect.x+=rect.width;
            rect.width=-rect.width;
        }
    }
    public void add() {
        if(!added) {
            globalVars.put("lastTapTime", System.currentTimeMillis());
            added = true;
            if(arect!=null) arect.set(rect);
        }
    }
    public void dispose(float delta) {

    }
    float getLeft() {
        return rect.width>0?rect.x:rect.x+rect.width;
    }
    float getRight() {
        return rect.width>0?rect.x+rect.width:rect.x;
    }
    float getTop() {
        return rect.y;
    }
    float getBottom() {
        return getTop()+rect.height;
    }
    public void makeAnimatable() {
        arect = new Animations.AnimatableRectangle(rect);
    }
}

