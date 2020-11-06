package it.manueldicriscito.whumpall.Data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

import it.manueldicriscito.whumpall.Spike;

public class SpikeData {
    public Vector2 pos;
    public int size;
    public SpikeData(Spike s) {
        size = s.size;
        pos = new Vector2(s.pos);
    }
    public SpikeData(JsonValue root) {
        JsonValue pos = root.get("pos");
        this.pos = new Vector2(
                pos.getFloat("x"),
                pos.getFloat("y")
        );
        this.size = root.getInt("size");
    }
}
