package it.manueldicriscito.whumpall.Data;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

import java.io.Serializable;

import it.manueldicriscito.whumpall.Platform;

public class PlatformData implements Serializable {
    public int dir;
    public Rectangle rect;
    public int type;
    public boolean fixed;
    public int gravityChange;
    public boolean superJump;

    public PlatformData(Platform p) {
        dir = p.dir;
        rect = new Rectangle(p.rect);
        type = p.type;
        fixed = p.fixed;
        gravityChange = p.gravityChange;
        superJump = p.superJump;
    }
    public PlatformData(JsonValue root) {
        this.dir = root.getInt("dir");
        JsonValue rect = root.get("rect");
        this.rect = new Rectangle(
                rect.getFloat("x"),
                rect.getFloat("y"),
                rect.getFloat("width"),
                rect.getFloat("height")
        );
        this.type = root.getInt("type");
        this.fixed = root.getBoolean("fixed");
        this.gravityChange = root.getInt("gravityChange");
        if(root.has("superJump")) {
            this.superJump = root.getBoolean("superJump");
        } else superJump = false;
    }
}
