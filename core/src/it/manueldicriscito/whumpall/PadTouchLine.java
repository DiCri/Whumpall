package it.manueldicriscito.whumpall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import static it.manueldicriscito.whumpall.Whumpall.PAD_TYPE_HORIZONTAL;
import static it.manueldicriscito.whumpall.Whumpall.getScreenBottom;
import static it.manueldicriscito.whumpall.Whumpall.getScreenTop;

public class PadTouchLine {
    void spawn(Player player, Platform p) {
        this.p = p;
        pos = new Vector2(player.pos.x+player.size/2, p.rect.y+p.rect.height);
        this.width = new Animations.AnimatableFloat(0f);
        this.alpha = new Animations.AnimatableFloat(1f);
        Animations.animate(
                Animations.AnimationEase.inOut,
                Animations.AnimationTiming.Linear,
                Animations.AnimationAction.force,
                this.alpha,
                Animations.AnimationMove.to,
                0f, false, 400, 200
        );
        Animations.animate(
                Animations.AnimationEase.inOut,
                Animations.AnimationTiming.Linear,
                Animations.AnimationAction.waitPrev,
                this.width,
                Animations.AnimationMove.to,
                p.rect.width*2, false, 200, 0
        );
    }
    void render(OrthographicCamera cam, ShapeRenderer sr) {
        if(p.type==PAD_TYPE_HORIZONTAL) {
            sr.setColor(1, 1, 1, this.alpha.get());
            Rectangle clip = new Rectangle(pos.x-width.get()/2, p.rect.y+p.rect.height-3, width.get(), 5);
            if(clip.x < p.rect.x) {
                clip.x = p.rect.x;
            }
            if(clip.width+clip.x>p.rect.x+p.rect.width) {
                clip.width = p.rect.width+p.rect.x-clip.x;
            }
            sr.rect(clip.x, clip.y, clip.width, clip.height);
        }
    }
    public Vector2 pos;
    public Animations.AnimatableFloat width;
    public Animations.AnimatableFloat alpha;
    public Platform p;
}
