package it.manueldicriscito.whumpall.Data;

import com.badlogic.gdx.math.Vector2;

import it.manueldicriscito.whumpall.Coin;

public class CoinData {
    public Vector2 pos;
    public CoinData(Coin coin) {
        pos = new Vector2(coin.pos);
    }
    public CoinData(Vector2 pos) {
        this.pos = new Vector2(pos);
    }
}
