package it.manueldicriscito.whumpall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


public class CircleButton {
    public Animations.AnimatableFloat size; // size of the circle
    public Animations.AnimatableColor color; // color of the circle
    public Vector2 pos; // center position of circle
    public Texture texture;
    float textureRotation;
    public int textureSize; // size of the texture
    public boolean tap;
    public int dSize; // default size
    public int hSize; // hold size
    public boolean clicked; // if button has been clicked
    public boolean holdDown; // if the button is hold down at the moment
    boolean selected;
    boolean hasWhiteShadow;

    public CircleButton() {
        pos = new Vector2(0, 0);
        textureSize = 50;
        color = new Animations.AnimatableColor(Color.WHITE);
        size = new Animations.AnimatableFloat(50);
        dSize = 50;
        hSize = 70;
        clicked = false;
        holdDown = false;
        selected = false;
        textureRotation = 0;
        hasWhiteShadow = true;
    }
    public void toggleWhiteShadow() {
        hasWhiteShadow = !hasWhiteShadow;
    }
    public void drawCircle(ShapeRenderer sr) {
        sr.setColor(Color.WHITE);
        sr.circle(pos.x, pos.y-5, size.get(), 100);
        sr.setColor(color.get());
        sr.circle(pos.x, pos.y, size.get(), 100);
    }
    public void drawTexture(SpriteBatch batch) {
        if(texture!=null) {
            int rad = (int)textureSize/2;
            batch.setColor(Assets.darkerBlueColor);
            batch.draw(texture, pos.x-rad+3, pos.y-rad-5, rad*2, rad*2);
            batch.setColor(Color.WHITE);
            batch.draw(texture, pos.x-rad+3, pos.y-rad, rad*2, rad*2);
        }
    }
    public boolean hover(int x, int y) {
        return Math.sqrt(Math.pow(x-pos.x, 2) + Math.pow(y-pos.y, 2))<size.get();
    }
    public void select() {
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, size, Animations.AnimationMove.to, hSize, false, 200, 0);
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, color.r, Animations.AnimationMove.to, Assets.darkerBlueColor.r, false, 200, 0);
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, color.g, Animations.AnimationMove.to, Assets.darkerBlueColor.g, false, 200, 0);
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, color.b, Animations.AnimationMove.to, Assets.darkerBlueColor.b, false, 200, 0);
        tap = true;
        if(holdDown) selected = !selected;
    }
    public void deselect() {
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, size, Animations.AnimationMove.to, dSize, false, 200, 0);
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, color.r, Animations.AnimationMove.to, Assets.darkBlueColor.r, false, 200, 0);
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, color.g, Animations.AnimationMove.to, Assets.darkBlueColor.g, false, 200, 0);
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, color.b, Animations.AnimationMove.to, Assets.darkBlueColor.b, false, 200, 0);
        selected = false;
    }
    public void update(Vector3 touchPos) {
        clicked = false;
        if (Gdx.input.justTouched()) {
            if (hover((int) touchPos.x, (int) touchPos.y)) {
                select();
            }
        } else if (Gdx.input.isTouched()) {

        } else if (tap) {
            tap = false;
            if(!selected) deselect();
            if(hover((int)touchPos.x, (int)touchPos.y)) {
                clicked = true;
            }
        }
    }
    public boolean isSelected() { return selected; }
    public boolean justClicked() {
        return clicked;
    }
}

