package it.manueldicriscito.whumpall;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import static it.manueldicriscito.whumpall.Whumpall.getScreenLeft;
import static it.manueldicriscito.whumpall.Whumpall.getScreenRight;

public class Player {
    public Vector2 pos;
    public Vector2 lpos;
    Vector2 vel;
    public float size;
    int gravity;
    int j;
    private boolean paused;
    float alpha;
    Vector2 offset;

    public Player() {
        pos = new Vector2(500, 700);
        lpos = new Vector2(500, 700);
        vel = new Vector2(300, -50);
        gravity = 1500;
        size = 40;
        j = 750;
        paused = false;
        alpha = 1f;
        offset = new Vector2(0, 0);
    }
    void jump() {
        vel.y = -(75*vel.y/100);
        if(gravity>0) {
            if(vel.y>-j) vel.y=-j;
        } else {
            if(vel.y<j) vel.y = j;
        }
    }
    private void handleInput() {

    }
    void update(float delta, OrthographicCamera cam) {
        handleInput();
        if(!paused) {
            lpos.set(pos);
            pos.y -= vel.y * delta;
            vel.y += gravity * delta;
            if (pos.x + size/2 > getScreenRight(cam)) {
                vel.x = -vel.x;
                pos.x = getScreenRight(cam) - size/2;
            } else if (pos.x - size/2 < getScreenLeft(cam)) {
                vel.x = -vel.x;
                pos.x = getScreenLeft(cam)+size/2;
            }
            pos.x += vel.x * delta;
        }
    }
    public void dispose() {

    }
    public void pause() {
        paused = true;
    }
    public void resume() {
        paused = false;
    }

    float getLeft() {
        return pos.x-size/2;
    }
    float getRight() {
        return getLeft()+size;
    }
    float getTop() {
        return pos.y-size/2;
    }
    float getBottom() {
        return getTop()+size;
    }
    private float getOldLeft() {
        return lpos.x-size/2;
    }
    public float getOldRight() {
        return getOldLeft() + size;
    }
    float getOldTop() {
        return lpos.y-size/2;
    }
    float getOldBottom() {
        return getOldTop() + size;
    }
}

