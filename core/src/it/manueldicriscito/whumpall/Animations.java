package it.manueldicriscito.whumpall;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

import it.manueldicriscito.whumpall.AnimationTypes.Back;
import it.manueldicriscito.whumpall.AnimationTypes.Bounce;
import it.manueldicriscito.whumpall.AnimationTypes.Circ;
import it.manueldicriscito.whumpall.AnimationTypes.Cubic;
import it.manueldicriscito.whumpall.AnimationTypes.Elastic;
import it.manueldicriscito.whumpall.AnimationTypes.Expo;
import it.manueldicriscito.whumpall.AnimationTypes.Linear;
import it.manueldicriscito.whumpall.AnimationTypes.Quad;
import it.manueldicriscito.whumpall.AnimationTypes.Quart;
import it.manueldicriscito.whumpall.AnimationTypes.Quint;
import it.manueldicriscito.whumpall.AnimationTypes.Sine;

public class Animations {

    public enum AnimationEase {
        in, out, inOut;
    }
    public enum AnimationAction {
        waitPrev, endPrev, force;
    }
    public enum AnimationMove {
        by, to;
    }
    public enum AnimationTiming {
        Back, Bounce, Circ, Cubic, Elastic, Expo, Linear, Quad, Quart, Quint, Sine;
    }

    public static final int EASE_OUT = 0x1;
    public static final int EASE_IN = 0x2;
    public static final int EASE_INOUT = 0x3;

    public static final int WAIT_PREV_ANIM = 0x4;
    public static final int END_PREV_ANIM = 0x8;
    public static final int FORCE_ANIM = 0x10;

    private static final boolean PIXELS = false;
    private static final boolean PERCENT = true;

    private static final boolean MOVE_BY = false;
    private static final boolean MOVE_TO = true;

    private static List<animclass> anims = new ArrayList<animclass>();

    private static class animclass {
        Object var;
        AnimationEase ease;
        AnimationTiming timing;
        AnimationMove move;
        AnimationAction action;
        long t;
        int d;
        Float b;
        float c,to;
        int delay, pause_time, pause_delay;
        boolean pause;
        boolean moveByPercentage;
    }
    public static class AnimatableFloat {
        private Float num;
        AnimatableFloat() {
            num = 0f;
        }
        public AnimatableFloat(float num) {
            this.num = num;
        }
        public float get() {
            return num;
        }
        public void set(float num) {
            this.num = num;
        }
        public void add(float num) {
            this.num+=num;
        }
        public void sub(float num) {
            this.num-=num;
        }
        public void mul(float num) {
            this.num*=num;
        }
        public void div(float num) {
            this.num/=num;
        }

    }
    public static class AnimatableColor {
        AnimatableFloat r;
        AnimatableFloat g;
        AnimatableFloat b;
        public AnimatableFloat a;
        private AnimatableColor() {
            r = new AnimatableFloat();
            g = new AnimatableFloat();
            b = new AnimatableFloat();
            a = new AnimatableFloat();
        }
        public AnimatableColor(Color color) {
            this.set(color);
        }
        public void set(Color color) {
            if(r!=null) r.set(color.r); else r = new AnimatableFloat(color.r);
            if(g!=null) g.set(color.g); else g = new AnimatableFloat(color.g);
            if(b!=null) b.set(color.b); else b = new AnimatableFloat(color.b);
            if(a!=null) a.set(color.a); else a = new AnimatableFloat(color.a);
        }
        public Color get() {
            return new Color(r.get(), g.get(), b.get(), a.get());
        }
    }
    public static class AnimatableRectangle {
        public AnimatableFloat x;
        public AnimatableFloat y;
        public AnimatableFloat width;
        public AnimatableFloat height;
        public AnimatableRectangle() {
            x = new AnimatableFloat();
            y = new AnimatableFloat();
            width = new AnimatableFloat();
            height = new AnimatableFloat();
        }
        public AnimatableRectangle(Rectangle rect) {
            this.set(rect);
        }
        public AnimatableRectangle(AnimatableRectangle rect) {
            set(new Rectangle(rect.x.get(), rect.y.get(), rect.width.get(), rect.height.get()));
        }
        public AnimatableRectangle(float x, float y, float width, float height) {
            set(new Rectangle(x, y, width, height));
        }
        public void set(Rectangle rect) {
            if(x!=null) x.set(rect.x); else x = new AnimatableFloat(rect.x);
            if(y!=null) y.set(rect.y); else y = new AnimatableFloat(rect.y);
            if(width!=null) width.set(rect.width); else width = new AnimatableFloat(rect.width);
            if(height!=null) height.set(rect.height); else height = new AnimatableFloat(rect.height);
        }
        public float getX() {
            return x.get();
        }
        public void setX(float x) {
            this.x.set(x);
        }
        public float getY() {
            return y.get();
        }
        public void setY(float y) {
            this.y.set(y);
        }
        public float getWidth() {
            return width.get();
        }
        public void setWidth(float width) {
            this.width.set(width);
        }
        public float getHeight() {
            return height.get();
        }
        public void setHeight(float height) {
            this.height.set(height);
        }
    }
    public static void run() {
        for(int k=0; k<anims.size(); k++) {
            if(System.currentTimeMillis()-anims.get(k).t>=anims.get(k).delay) {
                float new_value = 0;
                float c_t = System.currentTimeMillis()-anims.get(k).t-anims.get(k).delay;
                switch(anims.get(k).timing) {
                    case Back:
                        if(anims.get(k).ease==AnimationEase.in) new_value = Back.easeIn(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.out) new_value = Back.easeOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.inOut) new_value = Back.easeInOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        break;
                    case Bounce:
                        if(anims.get(k).ease==AnimationEase.in) new_value = Bounce.easeIn(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.out) new_value = Bounce.easeOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.inOut) new_value = Bounce.easeInOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        break;
                    case Circ:
                        if(anims.get(k).ease==AnimationEase.in) new_value = Circ.easeIn(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.out) new_value = Circ.easeOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.inOut) new_value = Circ.easeInOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        break;
                    case Cubic:
                        if(anims.get(k).ease==AnimationEase.in) new_value = Cubic.easeIn(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.out) new_value = Cubic.easeOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.inOut) new_value = Cubic.easeInOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        break;
                    case Elastic:
                        if(anims.get(k).ease==AnimationEase.in) new_value = Elastic.easeIn(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.out) new_value = Elastic.easeOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.inOut) new_value = Elastic.easeInOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        break;
                    case Expo:
                        if(anims.get(k).ease==AnimationEase.in) new_value = Expo.easeIn(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.out) new_value = Expo.easeOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.inOut) new_value = Expo.easeInOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        break;
                    case Linear:
                        if(anims.get(k).ease==AnimationEase.in) new_value = Linear.easeIn(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.out) new_value = Linear.easeOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.inOut) new_value = Linear.easeInOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        break;
                    case Quad:
                        if(anims.get(k).ease==AnimationEase.in) new_value = Quad.easeIn(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.out) new_value = Quad.easeOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.inOut) new_value = Quad.easeInOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        break;
                    case Quart:
                        if(anims.get(k).ease==AnimationEase.in) new_value = Quart.easeIn(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.out) new_value = Quart.easeOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.inOut) new_value = Quart.easeInOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        break;
                    case Quint:
                        if(anims.get(k).ease==AnimationEase.in) new_value = Quint.easeIn(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.out) new_value = Quint.easeOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.inOut) new_value = Quint.easeInOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        break;
                    case Sine:
                        if(anims.get(k).ease==AnimationEase.in) new_value = Sine.easeIn(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.out) new_value = Sine.easeOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        if(anims.get(k).ease==AnimationEase.inOut) new_value = Sine.easeInOut(c_t, anims.get(k).b, anims.get(k).c, anims.get(k).d);
                        break;
                }
                if(!anims.get(k).pause) {
                    ((AnimatableFloat)anims.get(k).var).set(new_value);
                    if(c_t>=anims.get(k).d) {
                        ((AnimatableFloat)anims.get(k).var).set(anims.get(k).b+anims.get(k).c);
                        for(int j=k+1; j<anims.size(); j++)
                            if(anims.get(j).var == anims.get(k).var) anims.get(j).b = anims.get(k).b+anims.get(k).c;
                        anims.remove(k);
                        k--;
                    }
                } else anims.get(k).pause_delay = (int)System.currentTimeMillis()-anims.get(k).pause_time;
            } else if(anims.get(k).pause) anims.get(k).pause_delay = (int)System.currentTimeMillis()-anims.get(k).pause_time;
        }
    }

    public static void animate(AnimationEase easing,
                               AnimationTiming timing,
                               AnimationAction action,
                               AnimatableFloat var,
                               AnimationMove move,
                               float to, boolean moveByPercentage, int ms, int delay) {
        animclass new_animation = new animclass();
        new_animation.pause = false;
        new_animation.pause_time = 0;
        new_animation.pause_delay = 0;
        new_animation.timing = timing;
        new_animation.ease = easing;
        new_animation.action = action;
        new_animation.t = System.currentTimeMillis();
        new_animation.b = var.get();
        new_animation.delay = delay;
        new_animation.moveByPercentage = moveByPercentage;
        new_animation.move = move;
        new_animation.to = to;
        new_animation.var = var;
        new_animation.d = ms;


        switch(move) {
            case by:
                if(moveByPercentage) new_animation.c = new_animation.b/100.f*to;
                else new_animation.c = to;
                break;
            case to:
                if(moveByPercentage) new_animation.c = (new_animation.b/100.f*to)-new_animation.b;
                else new_animation.c = to-new_animation.b;
                break;
        }
        switch(action) {
            case force:
                for(int k = 0; k<anims.size(); k++) {
                    if(anims.get(k).var==new_animation.var) {
                        anims.remove(k);
                    }
                }
                break;
            case endPrev:
                for(int k=0; k<anims.size(); k++) {
                    if(anims.get(k).var==new_animation.var) {
                        ((AnimatableFloat)anims.get(k).var).set(anims.get(k).to);
                        if(anims.get(k).move==AnimationMove.by) ((AnimatableFloat)anims.get(k).var).set(anims.get(k).b+anims.get(k).c);
                        anims.remove(k);
                    }
                }
                break;
            case waitPrev:
                if(anims.size()>0) {
                    for (int k = anims.size() - 1; k >= 0; k--)
                        if (anims.get(k).var == new_animation.var) {
                            new_animation.delay += anims.get(k).d - (System.currentTimeMillis() - anims.get(k).t - anims.get(k).delay);
                            break;
                        }
                }

                break;
        }
        anims.add(new_animation);

        for(int k=0; k<anims.size(); k++) {
            for(int j=0; j<k; j++) {
                if(anims.get(j).var == anims.get(k).var) {
                    anims.get(k).b = anims.get(j).b+anims.get(j).c;
                    switch(anims.get(k).move) {
                        case by:
                            if(anims.get(k).moveByPercentage) anims.get(k).c = anims.get(k).b/100.f*to;
                            else anims.get(k).c = anims.get(k).to;
                            break;
                        case to:
                            if(anims.get(k).moveByPercentage) anims.get(k).c = anims.get(k).b/100.f*to;
                            else anims.get(k).c = anims.get(k).to-anims.get(k).b;
                            break;
                    }
                }
            }
        }

    }
}
