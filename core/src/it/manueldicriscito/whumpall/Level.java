package it.manueldicriscito.whumpall;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

import it.manueldicriscito.whumpall.Data.LevelData;
import it.manueldicriscito.whumpall.Data.PlatformData;
import it.manueldicriscito.whumpall.Data.SpikeData;

import static it.manueldicriscito.whumpall.LevelRenderer.spawnPadTouchLine;
import static it.manueldicriscito.whumpall.PadTouchLine.PADTOUCH_BOTTOM;
import static it.manueldicriscito.whumpall.PadTouchLine.PADTOUCH_LEFT;
import static it.manueldicriscito.whumpall.PadTouchLine.PADTOUCH_RIGHT;
import static it.manueldicriscito.whumpall.PadTouchLine.PADTOUCH_TOP;
import static it.manueldicriscito.whumpall.Screens.PlayScreen.GAME_DEATH;
import static it.manueldicriscito.whumpall.Screens.PlayScreen.GAME_FINISH;
import static it.manueldicriscito.whumpall.Screens.PlayScreen.GAME_PLAY;
import static it.manueldicriscito.whumpall.Screens.PlayScreen.GAME_START;
import static it.manueldicriscito.whumpall.Whumpall.PAD_DIR_FINISH;
import static it.manueldicriscito.whumpall.Whumpall.PAD_TYPE_HORIZONTAL;
import static it.manueldicriscito.whumpall.Whumpall.PAD_TYPE_VERTICAL;
import static it.manueldicriscito.whumpall.Whumpall.globalVars;
import static it.manueldicriscito.whumpall.Whumpall.loadLevel;


public class Level {
    public final Player player;
    public final Player playerGuide;
    public final List<Platform> lpads;
    public final List<Platform> pads;
    public final List<Spike> spikes;
    public final List<GravityZone> gzones;
    public Vector2 initPos;
    public Vector2 initSpeed;
    public int initJump;
    public int initGrav;
    public int maxPads;
    public int maxMana;
    public int attempts;
    public int currentLevel;
    public int gameState;
    public DiCriTimer timer;
    public DiCriTimer gsTimer;
    public Animations.AnimatableFloat totalBlocksWidth;
    public LevelData levelData;
    public String name;
    public Coin coin;


    Vector3 touchPos;

    public Animations.AnimatableFloat darkDisplay;
    public Animations.AnimatableFloat ttsAlpha;

    public boolean addingPad;

    public void resetLevel() {
        lpads.clear();
        pads.clear();
        spikes.clear();
        respawnPlayer();
        respawnPlayerGuide();
        initPos.set(levelData.initPos);
        initSpeed.set(levelData.initSpeed);
        initJump = levelData.initJump;
        initGrav = levelData.initGrav;
        maxMana = levelData.maxMana;
        maxPads = levelData.maxPads;
        for(PlatformData pd : levelData.lpads) {
            Platform new_pad = new Platform();
            new_pad.rect.set(pd.rect);
            new_pad.fixed = pd.fixed;
            new_pad.dir = pd.dir;
            new_pad.type = pd.type;
            new_pad.superJump = pd.superJump;
            new_pad.add();
            lpads.add(new_pad);
        }
        for(SpikeData sd : levelData.spikes) {
            Spike new_spike = new Spike();
            new_spike.pos.set(sd.pos);
            new_spike.size = sd.size;
            spikes.add(new_spike);
        }
        if(levelData.coin!=null) coin = new Coin(levelData.coin);
        respawnPlayer();
        respawnPlayerGuide();
    }

    public void generateLevel(int level) {
        lpads.clear();
        pads.clear();
        spikes.clear();
        respawnPlayer();
        respawnPlayerGuide();

        if(level==-1) {
            initPos.x = 100;
            initPos.y = 1220;
            initJump = 750;
            initGrav = 1500;
            initSpeed.x = 300;
            initSpeed.y = -50;
            maxMana = 200;
            maxPads = 1;
        } else {
            this.generateLevel(loadLevel(Integer.toString(level)));
        }

    }
    public void generateLevel(LevelData levelData) {
        this.levelData = levelData;
        resetLevel();
    }
    public Level(LevelData levelData) {
        this(-1);
        generateLevel(levelData);
    }
    public Level(int currentLevel) {
        this.currentLevel = currentLevel;
        player = new Player();
        playerGuide = new Player();
        lpads = new ArrayList<>();
        pads = new ArrayList<>();
        spikes = new ArrayList<>();
        gzones = new ArrayList<>();

        initPos = new Vector2();
        initSpeed = new Vector2();
        initGrav = 1500;
        initJump = -750;
        maxPads = 100;
        maxMana = 1000;
        addingPad = false;
        touchPos = new Vector3();
        attempts = 0;
        totalBlocksWidth = new Animations.AnimatableFloat(0);
        gameState = GAME_START;
        darkDisplay = new Animations.AnimatableFloat(0);
        ttsAlpha = new Animations.AnimatableFloat(0);
        timer = new DiCriTimer();
        gsTimer = new DiCriTimer();
        gsTimer.start();
        name = "Untitled";

        globalVars.put("lastTapTime", System.currentTimeMillis());
        globalVars.put("addedBlocksMana", 0f);

        generateLevel(currentLevel);
        respawnPlayer();
        respawnPlayerGuide();
    }
    public void update(float delta, OrthographicCamera cam) {
        if(gameState!=GAME_FINISH) {
            updatePads(delta);
            updateSpikes(delta);
            if(gameState==GAME_PLAY) {
                checkPadsCollision(player);
                checkSpikesCollision(player);
                checkGravityZones(player);
                player.update(delta, cam);
                if(player.pos.y+player.size/2<=-100 || player.pos.y-player.size/2>=1920+200) player.die();
                if(player.dead) {
                    gameState = GAME_DEATH;
                    player.revive();
                    this.timer.save("Player Death");
                    gsTimer.stop();
                    gsTimer.start();
                    attempts++;
                    totalBlocksWidth.set(0);
                    addingPad = false;
                    Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,this.darkDisplay,Animations.AnimationMove.to,1f, false, 200, 0);
                    Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.waitPrev,this.darkDisplay,Animations.AnimationMove.to,0, false, 200, 300);
                    showTapToStart();
                    Animations.animate(Animations.AnimationEase.in, Animations.AnimationTiming.Linear, Animations.AnimationAction.force, this.totalBlocksWidth, Animations.AnimationMove.to,0, false, 0, 300);
                    player.pause();
                }
            } else if(gsTimer.get()>1500) {
                gsTimer.reset();
                this.respawnPlayerGuide();
                playerGuide.alpha = 1;
            }
            if(gameState==GAME_START) {
                playerGuide.update(delta, cam);
                checkPadsCollision(playerGuide);
                if(gsTimer.get()>1000) {
                    playerGuide.alpha = 150-(gsTimer.get()-1000)*150/500f;
                    playerGuide.alpha/=255;
                } else if(gsTimer.get()<300) {
                    playerGuide.alpha = gsTimer.get()/2f;
                    playerGuide.alpha/=255;
                } else playerGuide.alpha = 150f/255f;
            }



            checkGravityZones(playerGuide);
        }
    }
    public void updateSpikes(float delta) {
        for(Spike s : spikes) {
            s.update(delta);
        }
    }
    public void updatePads(float delta) {
        totalBlocksWidth.set(0);
        for(Platform p : pads) {
            p.update(delta);
            totalBlocksWidth.add(Math.abs(p.rect.width));
            if(pads.indexOf(p)!=pads.size()-1) {
                globalVars.put("addedBlocksMana", totalBlocksWidth.get());
            }
        }
        for(Platform p : lpads) p.update(delta);
    }
    public void checkGravityZones(Player player) {
        boolean nogz = false;
        for(GravityZone gz : gzones) {
            if(gz.polygon.contains(player.pos)) {
                player.gravity = gz.gravity;
                nogz = true;
            }
        }
        if(!nogz) player.gravity = initGrav;
    }
    public void checkSpikeCollision(Player player, Spike s) {
        if(!player.dead) {
            if(s.pos.dst(player.pos)<(s.size/2f+player.size/1.5f)) player.die();
        }
    }
    public void checkPadCollision(Player player, Platform p) {
        switch(p.type) {
            case PAD_TYPE_VERTICAL:
                if(player.getTop()>p.getBottom() && player.getBottom()<p.getTop()) {
                    if(player.getOldLeft() > p.getRight() && player.getLeft() <= p.getRight()) {
                        player.vel.x = player.vel.x>0?player.vel.x:-player.vel.x;
                        spawnPadTouchLine(player, p, PADTOUCH_RIGHT);
                    }
                    if(player.getOldRight() < p.getLeft() && player.getRight() >= p.getLeft()) {
                        player.vel.x = player.vel.x<0?player.vel.x:-player.vel.x;
                        spawnPadTouchLine(player, p, PADTOUCH_LEFT);
                    }
                }
                break;
            case PAD_TYPE_HORIZONTAL:
                boolean jump = false;
                boolean ceil = false;
                if(player.getRight() > p.getLeft() && player.getLeft() < p.getRight()) {
                    jump = player.getOldBottom() > p.getTop() && player.getBottom() <= p.getTop();
                    ceil = player.getOldTop() <= p.getBottom() && player.getTop() > p.getBottom();
                    boolean updown = player.gravity<0;
                    if(jump||ceil && gameState==GAME_PLAY) {
                        p.fall();
                        p.add();
                        p.fix();
                        if(p.dir==PAD_DIR_FINISH) gameState = GAME_FINISH;
                    }
                    if(jump || (ceil && updown)) {
                        // jump
                        Particles.trigger(5, player.pos.x, player.pos.y, Range.range(5, 20), Range.range(5, 20), Range.single(-20), Range.single(-20), Range.range(-200, 200), Range.range(0, 500), Range.single(0.5f), Range.single(-0.4f), Range.single(1500), Assets.Colors.get("darkBlue"), 5000);
                        spawnPadTouchLine(player, p, updown?PADTOUCH_BOTTOM:PADTOUCH_TOP);
                        if(p.superJump) {
                            player.superJump(2f);
                            p.triggerSuperJump();
                        } else player.jump();
                        player.pos.y = updown ? p.rect.y-player.size/2 : p.rect.y+p.rect.height+player.size/2+1;
                        player.gravity *= p.gravityChange;
                    }
                    if(ceil || (jump && updown)) {
                        // fall
                        player.vel.y = 0;
                        player.pos.y = !updown ? p.rect.y-player.size/2 : p.rect.y+p.rect.height+player.size/2;
                        spawnPadTouchLine(player, p, updown?PADTOUCH_TOP:PADTOUCH_BOTTOM);
                    }
                }
                if((!jump && !ceil) && player.getTop() > p.getBottom() && player.getBottom() < p.getTop()) {
                    boolean left = player.getOldRight() < p.getLeft() && player.getRight() >= p.getLeft();
                    boolean right = player.getOldLeft() > p.getRight() && player.getLeft() <= p.getRight();
                    if(left || right) player.die();
                }
                break;
        }
    }
    public void checkPadsCollision(Player player) {
        for(Platform p : pads) {
            checkPadCollision(player, p);
        }
        for(Platform p : lpads) {
            checkPadCollision(player, p);
        }
    }
    public void checkSpikesCollision(Player player) {
        for(Spike s : spikes) {
            checkSpikeCollision(player, s);
        }
    }
    public int getManaUsed() {
        int manaUsed = 0;
        for(Platform p : pads) {
            manaUsed += Math.abs(p.rect.width);
        }
        return manaUsed;
    }
    public void respawnPlayerGuide() {
        playerGuide.pos.x = initPos.x;
        playerGuide.pos.y = initPos.y;
        playerGuide.lpos.x = initPos.x;
        playerGuide.lpos.y = initPos.y;
        playerGuide.j = initJump;
        playerGuide.gravity = initGrav;
        playerGuide.vel.x = initSpeed.x;
        playerGuide.vel.y = initSpeed.y;
    }
    public void respawnPlayer() {
        player.pos.x = initPos.x;
        player.pos.y = initPos.y;
        player.lpos.x = initPos.x;
        player.lpos.y = initPos.y;
        player.j = initJump;
        player.gravity = initGrav;
        player.vel.x = initSpeed.x;
        player.vel.y = initSpeed.y;
        player.pause();
    }
    public void showTapToStart() {
        Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,this.ttsAlpha,Animations.AnimationMove.to,1f, false, 200, 700);
    }
    public void hideTapToStart() {
        Animations.animate(Animations.AnimationEase.in, Animations.AnimationTiming.Linear, Animations.AnimationAction.force, this.ttsAlpha,Animations.AnimationMove.to,0f, false, 100, 0);
    }


}

