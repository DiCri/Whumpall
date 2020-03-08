package it.manueldicriscito.whumpall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

import static it.manueldicriscito.whumpall.Screens.PlayScreen.GAME_FINISH;
import static it.manueldicriscito.whumpall.Screens.PlayScreen.GAME_PLAY;
import static it.manueldicriscito.whumpall.Screens.PlayScreen.GAME_START;
import static it.manueldicriscito.whumpall.Whumpall.PAD_DIR_FINISH;
import static it.manueldicriscito.whumpall.Whumpall.PAD_DIR_LEFT;
import static it.manueldicriscito.whumpall.Whumpall.PAD_DIR_NONE;
import static it.manueldicriscito.whumpall.Whumpall.globalVars;


public class Level {
    public final Player player;
    public final Player playerGuide;
    public final List<Platform> lpads;
    public final List<Platform> pads;
    public final List<GravityZone> gzones;
    private Vector2 initPos;
    private Vector2 initSpeed;
    private int initJump;
    private int initGrav;
    public int maxPads;
    public int maxMana;
    public int attempts;
    public int currentLevel;
    public int gameState;
    public DiCriTimer timer;
    public DiCriTimer gsTimer;
    public Animations.AnimatableFloat totalBlocksWidth;


    Vector3 touchPos;

    public Animations.AnimatableFloat darkDisplay;
    public Animations.AnimatableFloat ttsAlpha;

    public boolean addingPad;

    public Level(int currentLevel) {
        this.currentLevel = currentLevel;
        player = new Player();
        playerGuide = new Player();
        lpads = new ArrayList<>();
        pads = new ArrayList<>();
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

        globalVars.put("lastTapTime", System.currentTimeMillis());
        globalVars.put("addedBlocksMana", 0f);


        generateLevel(currentLevel);
        respawnPlayer();
        respawnPlayerGuide();
    }
    public void setGameState(int gameState) {
        this.gameState = gameState;
    }
    public void update(float delta, OrthographicCamera cam) {
        if(gameState!=GAME_FINISH) {
            player.update(delta, cam);
            if(gameState!=GAME_START && (player.pos.y+player.size/2<=-100 || player.pos.y-player.size/2>=1920+200)) {
                this.timer.save("Player Death");
                gameState = GAME_START;
                gsTimer.stop();
                gsTimer.start();
                player.pause();
                attempts++;
                totalBlocksWidth.set(0);
                addingPad = false;
                Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,this.darkDisplay,Animations.AnimationMove.to,1f, false, 200, 0);
                Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.waitPrev,this.darkDisplay,Animations.AnimationMove.to,0, false, 200, 300);
                showTapToStart();
                Animations.animate(Animations.AnimationEase.in, Animations.AnimationTiming.Linear, Animations.AnimationAction.force, this.totalBlocksWidth, Animations.AnimationMove.to,0, false, 0, 300);
            } else {
                if(gsTimer.get()>1500) {
                    gsTimer.reset();
                    this.respawnPlayerGuide();
                    playerGuide.alpha = 1;
                }
            }
            if(gameState==GAME_START) {
                playerGuide.update(delta, cam);
                if(gsTimer.get()>1000) {
                    playerGuide.alpha = 150-(gsTimer.get()-1000)*150/500f;
                    playerGuide.alpha/=255;
                } else if(gsTimer.get()<300) {
                    playerGuide.alpha = gsTimer.get()/2f;
                    playerGuide.alpha/=255;
                } else playerGuide.alpha = 150f/255f;
            }
            updatePads(delta);
            checkPadsCollision(player);
            checkGravityZones(player);
            checkGravityZones(playerGuide);
            if(gameState==GAME_START) {
                checkPadsCollision(playerGuide);
                if(gameState!=GAME_START) gameState = GAME_START;
            }
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
    public void checkPadCollision(Player player, Platform p) {
        if(player.getRight() > p.getLeft() && player.getLeft() < p.getRight()) {
            if (player.getOldTop() >= p.getBottom() && player.getTop() < p.getBottom()) {
                if(gameState==GAME_PLAY) {
                    p.fall();
                    p.add();
                    p.fix();
                    if(p.dir==PAD_DIR_FINISH) gameState = GAME_FINISH;
                }
                if(player.gravity>0) {
                    Particles.trigger(
                            5,
                            player.pos.x,
                            player.pos.y,
                            Range.range(5, 20),
                            Range.range(5, 20),
                            Range.single(-20),
                            Range.single(-20),
                            Range.range(-200, 200),
                            Range.range(0, 500),
                            Range.single(0.5f),
                            Range.single(-0.4f),
                            Range.single(1500),
                            Assets.darkBlueColor,
                            5000
                    );
                    player.jump();
                    player.gravity*=p.gravityChange;

                } else {
                    player.vel.y = 0;
                    player.pos.y = p.rect.y + p.rect.height + player.size/2;
                }
            }
            if (player.getOldBottom() < p.getTop() && player.getBottom() >= p.getTop()) {
                if(gameState==GAME_PLAY) {
                    p.fall();
                    p.add();
                    p.fix();
                    if(p.dir==PAD_DIR_FINISH) gameState = GAME_FINISH;
                }
                if(player.gravity>0) {
                    player.vel.y = 0;
                    player.pos.y = p.rect.y - player.size / 2;
                } else {
                    Particles.trigger(
                            5,
                            player.pos.x,
                            player.pos.y,
                            Range.range(5, 20),
                            Range.range(5, 20),
                            Range.single(-20),
                            Range.single(-20),
                            Range.range(-200, 200),
                            Range.range(0, 500),
                            Range.single(0.5f),
                            Range.single(-0.4f),
                            Range.single(1500),
                            Assets.darkBlueColor,
                            5000
                    );
                    player.jump();
                    player.gravity*=p.gravityChange;
                }
            }
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
    public void generateLevel(int level) {
        lpads.clear();
        pads.clear();
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
        }
        if(level==1) {
            Platform new_pad;
            GravityZone new_gzone;

            new_pad = new Platform();
            new_pad.rect.set(100, 1020, 250, 40);
            new_pad.fixed = true;
            new_pad.dir = PAD_DIR_NONE;
            new_pad.add();
            lpads.add(new_pad);

            new_pad = new Platform();
            new_pad.rect.set(700, 1020, 250, 40);
            new_pad.fixed = true;
            new_pad.dir = PAD_DIR_FINISH;
            new_pad.add();
            lpads.add(new_pad);


            initPos.x = 100;
            initPos.y = 1220;
            initJump = 750;
            initGrav = 1500;
            initSpeed.x = 300;
            initSpeed.y = -50;
            maxMana = 200;
            maxPads = 1;
        } else if(level==2) {
            Platform new_pad;
            GravityZone new_gzone;

            new_pad = new Platform();
            new_pad.rect.set(100, 1020, 250, 40);
            new_pad.fixed = true;
            new_pad.dir = PAD_DIR_NONE;
            new_pad.add();
            lpads.add(new_pad);

            new_pad = new Platform();
            new_pad.rect.set(400, 1020, 250, 40);
            new_pad.fixed = true;
            new_pad.dir = PAD_DIR_NONE;
            new_pad.add();
            lpads.add(new_pad);

            new_pad = new Platform();
            new_pad.rect.set(1000, 1020, 250, 40);
            new_pad.fixed = true;
            new_pad.dir = PAD_DIR_FINISH;
            new_pad.add();
            lpads.add(new_pad);

            new_pad = new Platform();
            new_pad.rect.set(-500, 1020, 250, 40);
            new_pad.fixed = true;
            new_pad.dir = PAD_DIR_NONE;
            new_pad.add();
            lpads.add(new_pad);

            initPos.x = 100;
            initPos.y = 1220;
            initJump = 750;
            initGrav = 1500;
            initSpeed.x = 300;
            initSpeed.y = -50;
            maxMana = 200;
            maxPads = 100;
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

