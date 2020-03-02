package it.manueldicriscito.whumpall;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public class Image {
    private Color color;
    public Rectangle rect;
    public Animations.AnimatableRectangle arect;
    private Texture texture;
    public Image() {
        color = new Color();
        rect = new Rectangle();
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public void setRect(Rectangle rect) {
        this.rect.set(rect);
        if(this.arect!=null) {
            this.arect.set(rect);
        }
    }
    public void makeAnimatable() {
        this.arect = new Animations.AnimatableRectangle(rect);
    }
    public Animations.AnimatableRectangle getAnimatableRect() {
        return this.arect;
    }
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setColor(color);
        if(arect!=null) {
            batch.draw(this.texture, this.arect.getX(), this.arect.getY(), this.arect.getWidth(), this.arect.getHeight());
        } else {
            batch.draw(this.texture, this.rect.getX(), this.rect.getY(), this.rect.getWidth(), this.rect.getHeight());
        }
        batch.end();
    }
}

