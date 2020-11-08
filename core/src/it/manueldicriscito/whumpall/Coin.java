package it.manueldicriscito.whumpall;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import it.manueldicriscito.whumpall.Data.CoinData;

public class Coin {
    public final Vector2 pos;
    private final DiCriTimer timer;
    private int frame;
    public int frameDuration;
    public Coin(CoinData coin) {

        pos = new Vector2(coin.pos);
        timer = new DiCriTimer();
        frame = 0;
        frameDuration = 50;
    }
    public Coin() {
        pos = new Vector2(-100, -100);
        frame = 0;
        timer = new DiCriTimer();
        frameDuration = 50;
    }
    public void setPos(float x, float y) {
        pos.x = x; pos.y = y;
    }
    public void draw(SpriteBatch batch) {
        batch.setColor(Color.WHITE);
        if(!timer.isON()) timer.start();
        frame = (int)(timer.get()/frameDuration)%10;
        float size = 0.80f;
        Texture texture = Assets.Textures.get("coin");
        batch.draw(
                texture,
                pos.x-(70*size)/2, pos.y-(70*size)/2, 70*size, 105*size,
                40+145*(frame%5), 40+((frame/5)%2)*210, 140, 210,
                false, false
        );
    }
    public Vector2 getPos() {
        return pos;
    }
}
