package it.manueldicriscito.whumpall.Data;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import it.manueldicriscito.whumpall.Level;
import it.manueldicriscito.whumpall.Platform;

public class LevelData {
    public List<PlatformData> lpads;
    public Vector2 initPos;
    public Vector2 initSpeed;
    public int initJump;
    public int initGrav;
    public int maxPads;
    public int maxMana;
    public LevelData(Level level) {
        lpads = new ArrayList<>();
        for(Platform p : level.lpads) {
            lpads.add(new PlatformData(p));
        }
        initPos = new Vector2(level.initPos);
        initSpeed = new Vector2(level.initSpeed);
        initJump = level.initJump;
        initGrav = level.initGrav;
        maxPads = level.maxPads;
        maxMana = level.maxMana;
    }
}
