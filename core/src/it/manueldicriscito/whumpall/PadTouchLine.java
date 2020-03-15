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
    public final static int PADTOUCH_TOP = 0;
    public final static int PADTOUCH_BOTTOM = 1;
    public final static int PADTOUCH_LEFT = 2;
    public final static int PADTOUCH_RIGHT = 3;
    void spawn(Player player, Platform p, int loc) {
        this.p = p;
        this.pos = new Vector2(0, 0);
        switch(loc) {
            case PADTOUCH_TOP:
                pos.x = player.pos.x;
                pos.y = p.rect.y+p.rect.height;
                break;
            case PADTOUCH_BOTTOM:
                pos.x = player.pos.x;
                pos.y = p.rect.y;
                break;
            case PADTOUCH_LEFT:
                pos.x = p.rect.x;
                pos.y = player.pos.y;
                break;
            case PADTOUCH_RIGHT:
                pos.x = p.rect.x+p.rect.width;
                pos.y = player.pos.y;
                break;
        }
        this.loc = loc;
        this.width = new Animations.AnimatableFloat(0f);
        this.alpha = new Animations.AnimatableFloat(1f);
        Animations.animate(
                Animations.AnimationEase.inOut,
                Animations.AnimationTiming.Linear,
                Animations.AnimationAction.force,
                this.alpha,
                Animations.AnimationMove.to,
                0f, false, 500, 200
        );
        Animations.animate(
                Animations.AnimationEase.out,
                Animations.AnimationTiming.Quart,
                Animations.AnimationAction.waitPrev,
                this.width,
                Animations.AnimationMove.to,
                loc==PADTOUCH_TOP||loc==PADTOUCH_BOTTOM?p.rect.width*2:p.rect.height*2, false, 1000, 0
        );
    }
    void render(OrthographicCamera cam, ShapeRenderer sr) {
        sr.setColor(1, 1, 1, this.alpha.get());
        Rectangle clip = new Rectangle(0, 0, 0, 0);
        int linesize = 20;
        switch(loc) {
            case PADTOUCH_TOP:
                clip.x = pos.x-width.get()/2; clip.y = p.rect.y+p.rect.height-linesize;
                clip.width = width.get(); clip.height = linesize;
                break;
            case PADTOUCH_BOTTOM:
                clip.x = pos.x-width.get()/2; clip.y = p.rect.y;
                clip.width = width.get(); clip.height = linesize;
                break;
            case PADTOUCH_LEFT:
                clip.x = p.rect.x; clip.y = pos.y-width.get();
                clip.width = linesize; clip.height = width.get();
                break;
            case PADTOUCH_RIGHT:
                clip.x = p.rect.x+p.rect.width-linesize; clip.y = pos.y-width.get();
                clip.width = linesize; clip.height = width.get();
                break;
        }
        if (clip.x < p.rect.x) {
            clip.x = p.rect.x;
        }
        if (clip.width + clip.x > p.rect.x + p.rect.width) {
            clip.width = p.rect.width + p.rect.x - clip.x;
        }
        if(clip.y<p.rect.y) {
            clip.y = p.rect.y;
        }
        if(clip.height+clip.y>p.rect.y+p.rect.height) {
            clip.height = p.rect.height + p.rect.y-clip.y;
        }

        sr.rect(clip.x, clip.y, clip.width, clip.height);
    }
    private Vector2 pos;
    public Animations.AnimatableFloat width;
    private int loc;
    Animations.AnimatableFloat alpha;
    public Platform p;
}
