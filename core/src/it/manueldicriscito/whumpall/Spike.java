package it.manueldicriscito.whumpall;

import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Spike {
    public Vector2 pos;
    public int size;
    public int rotation;
    public boolean toRight;
    public Spike() {
        size = 100;
        pos = new Vector2(0, 0);
        toRight = true;
        rotation = 0;
        Random rd = new Random();
        toRight = rd.nextBoolean();
    }
    public void update(float delta) {
        rotation += (toRight?360:-360)*delta;
        rotation = rotation%360;
    }
}
