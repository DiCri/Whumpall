package it.manueldicriscito.whumpall.Data;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.manueldicriscito.whumpall.Level;
import it.manueldicriscito.whumpall.Platform;
import it.manueldicriscito.whumpall.Spike;

public class LevelData implements Serializable {
    public List<PlatformData> lpads;
    public List<SpikeData> spikes;
    public CoinData coin;
    public Vector2 initPos;
    public Vector2 initSpeed;
    public int initJump;
    public int initGrav;
    public int maxPads;
    public int maxMana;
    public String name;

    public LevelData() {
        initPos = new Vector2();
        initSpeed = new Vector2();
        name = "Untitled";
        lpads = new ArrayList<>();
        spikes = new ArrayList<>();
        initPos.x = 100;
        initPos.y = 1220;
        initJump = 750;
        initGrav = 1500;
        initSpeed.x = 300;
        initSpeed.y = -50;
        maxMana = 200;
        maxPads = 1;
    }
    public LevelData(Level level) {
        this.name = level.name;
        lpads = new ArrayList<>();
        for(Platform p : level.lpads) {
            lpads.add(new PlatformData(p));
        }
        spikes = new ArrayList<>();
        for(Spike s : level.spikes) {
            spikes.add(new SpikeData(s));
        }
        if(level.coin!=null) coin = new CoinData(level.coin);
        initPos = new Vector2(level.initPos);
        initSpeed = new Vector2(level.initSpeed);
        initJump = level.initJump;
        initGrav = level.initGrav;
        maxPads = level.maxPads;
        maxMana = level.maxMana;
    }
    public LevelData(JsonValue root) {
        lpads = new ArrayList<>();
        if(root.has("lpads")) for(JsonValue pad : root.get("lpads")) {
            lpads.add(new PlatformData(pad));
        }
        spikes = new ArrayList<>();
        if(root.has("spikes")) for(JsonValue spike : root.get("spikes")) {
            spikes.add(new SpikeData(spike));
        }
        if(root.has("coin")) coin = new CoinData(new Vector2(root.get("pos").getFloat("x"), root.get("pos").getFloat("y")));
        initPos = new Vector2(root.get("initPos").getFloat("x"), root.get("initPos").getFloat("y"));
        initSpeed = new Vector2(root.get("initSpeed").getFloat("x"), root.get("initSpeed").getFloat("y"));
        initJump = root.getInt("initJump");
        initGrav = root.getInt("initGrav");
        maxPads = root.getInt("maxPads");
        maxMana = root.getInt("maxMana");
        name = root.getString("name");
        //state = root.has("state")?root.getInt("state"):LEVELSTATE_UNLOCKED;
    }
}
