package it.manueldicriscito.whumpall.Data;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import it.manueldicriscito.whumpall.Platform;

public class PlatformData {
    public int dir;
    public Rectangle rect;
    public int type;
    public boolean fixed;
    public int gravityChange;

    public PlatformData(Platform p) {
        dir = p.dir;
        rect = new Rectangle(p.rect);
        type = p.type;
        fixed = p.fixed;
        gravityChange = p.gravityChange;
    }
}
