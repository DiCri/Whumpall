package it.manueldicriscito.whumpall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Button {
    public Animations.AnimatableFloat size; // size of the circle
    public Animations.AnimatableColor color; // color of the circle
    public Animations.AnimatableRectangle rect;
    public Color defaultColor;
    public Color shadowColor; // shadowColor
    public Color tapColor;

    public Texture texture;
    private float textureRotation;
    public int textureSize; // size of the texture
    public boolean tap;
    public int dSize; // default size
    public int hSize; // hold size
    private boolean clicked; // if button has been clicked
    public boolean holdDown; // if the button is hold down at the moment
    private boolean selected;
    private boolean hasShadow;
    private boolean jTouch = false;
    public float alpha;

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
    public Button(Button cb) {
        this.size = new Animations.AnimatableFloat(cb.size.get());
        this.color = new Animations.AnimatableColor(cb.color.get());
        this.defaultColor = cb.defaultColor;
        this.shadowColor = cb.shadowColor;
        this.tapColor = cb.tapColor;
        this.rect = new Animations.AnimatableRectangle(cb.rect);
        this.texture = cb.texture;
        this.textureRotation = cb.textureRotation;
        this.textureSize = cb.textureSize;
        this.tap = cb.tap;
        this.dSize = cb.dSize;
        this.hSize= cb.hSize;
        this.holdDown = cb.holdDown;
        this.hasShadow = cb.hasShadow;
        this.alpha = cb.alpha;
    }
    public Button() {
        rect = new Animations.AnimatableRectangle(0, 0, 100, 100);
        textureSize = 50;
        color = new Animations.AnimatableColor(Color.WHITE);
        defaultColor = new Color(Color.WHITE);
        shadowColor = new Color(Color.BLACK);
        tapColor = new Color(Color.WHITE);
        size = new Animations.AnimatableFloat(50);
        dSize = 50;
        hSize = 70;
        clicked = false;
        holdDown = false;
        selected = false;
        textureRotation = 0;
        hasShadow = true;
        alpha = 1f;
    }
    public void toggleShadow() {
        hasShadow = !hasShadow;
    }
    public void drawTexture(SpriteBatch batch) {
        /*
        if(texture!=null) {
            int rad = textureSize/2;
            Color cl = Assets.Colors.get("darkerBlue");
            batch.setColor(cl.r, cl.g, cl.b, alpha);
            batch.draw(texture, pos.x-rad+3, pos.y-rad-5, rad*2, rad*2);
            batch.setColor(Color.WHITE);
            batch.draw(texture, pos.x-rad+3, pos.y-rad, rad*2, rad*2);
        }
         */
    }
    public boolean hover(int x, int y) {
        return x>rect.getX()&&x<rect.getX()+rect.getWidth()&&y>rect.getY()&&y<rect.getY()+rect.getHeight();
    }
    public void select() {
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, size, Animations.AnimationMove.to, hSize, false, 200, 0);
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, color.r, Animations.AnimationMove.to, tapColor.r, false, 200, 0);
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, color.g, Animations.AnimationMove.to, tapColor.g, false, 200, 0);
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, color.b, Animations.AnimationMove.to, tapColor.b, false, 200, 0);
        tap = true;
        jTouch = true;
        if(holdDown) selected = !selected;
    }
    public void deselect() {
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, size, Animations.AnimationMove.to, dSize, false, 200, 0);
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, color.r, Animations.AnimationMove.to, defaultColor.r, false, 200, 0);
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, color.g, Animations.AnimationMove.to, defaultColor.g, false, 200, 0);
        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Back, Animations.AnimationAction.force, color.b, Animations.AnimationMove.to, defaultColor.b, false, 200, 0);
        selected = false;
    }
    public void update(Vector3 touchPos) {
        clicked = false;
        jTouch = false;
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
            } else {
                deselect();
            }
        }
    }
    public boolean isSelected() { return selected; }
    public boolean justClicked() {
        return clicked;
    }
    public boolean justTouched() {
        return jTouch;
    }
    public void drawButton(ShapeRenderer sr) {
        int radius = 50;
        sr.setColor(1, 1, 1, 1);
        sr.rect(rect.getX() + radius, rect.getY() + radius, rect.getWidth() - 2*radius, rect.getHeight() - 2*radius);

        sr.rect(rect.getX() + radius, rect.getY(), rect.getWidth() - 2*radius, radius);
        sr.rect(rect.getX() + rect.getWidth() - radius, rect.getY() + radius, radius, rect.getHeight() - 2*radius);
        sr.rect(rect.getX() + radius, rect.getY() + rect.getHeight() - radius, rect.getWidth() - 2*radius, radius);
        sr.rect(rect.getX(), rect.getY() + radius, radius, rect.getHeight() - 2*radius);

        sr.arc(rect.getX() + radius, rect.getY() + radius, radius, 180f, 90f);
        sr.arc(rect.getX() + rect.getWidth() - radius, rect.getY() + radius, radius, 270f, 90f);
        sr.arc(rect.getX() + rect.getWidth() - radius, rect.getY() + rect.getHeight() - radius, radius, 0f, 90f);
        sr.arc(rect.getX() + radius, rect.getY() + rect.getHeight() - radius, radius, 90f, 90f);
    }
}
