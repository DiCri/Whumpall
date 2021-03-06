package it.manueldicriscito.whumpall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import static it.manueldicriscito.whumpall.Whumpall.PAD_DIR_NONE;
import static it.manueldicriscito.whumpall.Whumpall.PAD_TYPE_HORIZONTAL;
import static it.manueldicriscito.whumpall.Whumpall.PAD_TYPE_VERTICAL;
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

    public boolean superJump = false;
    public Animations.AnimatableFloat upperPiece;

    public Platform() {
        rect = new Rectangle();
        vel = new Vector2(0, 0);
        dir = PAD_DIR_NONE;
        arect = null;
        falling = false;
        gravity = 1500;
        fixed = false;
        gravityChange = 1;
        superJump = false;
    }
    public void activateSuperJump() {
        superJump = true;
        upperPiece = new Animations.AnimatableFloat(0);
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
        if(rect.width<0 && type==PAD_TYPE_HORIZONTAL) {
            rect.x+=rect.width;
            rect.width=-rect.width;
        } else if(rect.height<0 && type==PAD_TYPE_VERTICAL) {
            rect.y+=rect.height;
            rect.height=-rect.height;
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
        return getBottom()+rect.height;
    }
    float getBottom() {
        return rect.y;
    }
    public void makeAnimatable() {
        arect = new Animations.AnimatableRectangle(rect);
    }
    public void triggerSuperJump() {
        if(upperPiece==null) activateSuperJump();
        Animations.animate(
                Animations.AnimationEase.out,
                Animations.AnimationTiming.Elastic,
                Animations.AnimationAction.endPrev,
                this.upperPiece,
                Animations.AnimationMove.to,
                20, false, 200, 0);
        Animations.animate(
                Animations.AnimationEase.inOut,
                Animations.AnimationTiming.Back,
                Animations.AnimationAction.waitPrev,
                this.upperPiece,
                Animations.AnimationMove.to,
                0, false, 1000, 0);

    }
}

