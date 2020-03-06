package it.manueldicriscito.whumpall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import static it.manueldicriscito.whumpall.Screens.PlayScreen.GAME_START;
import static it.manueldicriscito.whumpall.Whumpall.PAD_DIR_FINISH;
import static it.manueldicriscito.whumpall.Whumpall.PAD_DIR_NONE;
import static it.manueldicriscito.whumpall.Whumpall.getAngle;
import static it.manueldicriscito.whumpall.Whumpall.getScreenBottom;
import static it.manueldicriscito.whumpall.Whumpall.getScreenTop;
import static it.manueldicriscito.whumpall.Whumpall.globalVars;

public class LevelRenderer {
    private Level level;
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private ShapeRenderer sr;
    public float minX, maxX;

    private float gunRotation;
    private Vector2 gunPos;
    private Animations.AnimatableFloat playerEyes;
    private Vector2 laserEndPos;


    public LevelRenderer(Whumpall game, Level level) {
        this.level = level;
        getLevelMinMax();
        this.cam = game.cam;
        this.sr = new ShapeRenderer();
        this.batch = game.batch;
        gunPos = new Vector2();
        playerEyes = new Animations.AnimatableFloat(15);
        laserEndPos = new Vector2(0, 0);
    }
    public void render() {
        renderBackground();
        renderGravityZones();
        renderPlayer();
        renderPlayerGuide();
        renderPads();
        renderObjects();
    }
    private void renderGravityZones() {
        this.sr.begin(ShapeRenderer.ShapeType.Filled);
        for(GravityZone gz : level.gzones) {
            gz.render(sr);
        }
        this.sr.end();
    }
    private void renderBackground() {
        this.sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setProjectionMatrix(cam.combined);
        Color mainColor = Assets.lightBlueColor;
        Color darkColor = Assets.darkLightBlueColor;

        sr.setColor(mainColor);
        sr.rect(minX, getScreenBottom(cam), maxX-minX, getScreenTop(cam)-getScreenBottom(cam));
        sr.rect(minX, getScreenBottom(cam), maxX-minX, 1920f/3, darkColor, darkColor, mainColor, mainColor);
        this.sr.end();
    }
    private void renderObjects() {
        batch.begin();
        int psize = 0;
        for(Platform p : level.pads) {
            psize += p.added?1:0;
        }
        Assets.fontKoHoBold50.setColor(1, 1, 1, 1);
        Assets.fontKoHoBold50.draw(batch, ""+(level.maxPads-psize), 1080-180+cam.position.x-540, 100);
        batch.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        // construct line: begin
        if(level.pads.size()>0) {
            Platform lastp = level.pads.get(level.pads.size()-1);
            if(lastp!=null) {
                sr.setColor(Color.WHITE);
                if(level.addingPad && !lastp.added) {
                    laserEndPos.x = lastp.rect.x + lastp.rect.width;
                    laserEndPos.y = lastp.rect.y + lastp.rect.height / 2;
                }
                float y1 = gunPos.y;
                float x1 = gunPos.x;
                float speed = 150;
                long timeDiff = System.currentTimeMillis()-(Long)globalVars.get("lastTapTime");
                if(timeDiff>speed) {
                    timeDiff = (long)speed;
                }
                float endlineX = (timeDiff)*(laserEndPos.x-x1)/speed;
                float endlineY = (timeDiff)*(laserEndPos.y-y1)/speed;
                if(level.addingPad && !lastp.added) {
                    sr.rectLine(x1, y1, x1+endlineX, y1+endlineY, 3);
                    Particles.trigger(
                            1,
                            laserEndPos.x,
                            laserEndPos.y,
                            Range.range(10, 15),
                            Range.range(10, 15),
                            Range.single(-20),
                            Range.single(-20),
                            Range.range(-200, 200),
                            Range.range(0, 500),
                            Range.single(0.5f),
                            Range.single(-1.5f),
                            Range.single(1500),
                            Color.WHITE,
                            5000
                    );
                } else {
                    sr.rectLine(x1+endlineX, y1+endlineY, laserEndPos.x, laserEndPos.y, 3);
                }
                gunRotation = getAngle(level.player.pos.x, level.player.pos.y, laserEndPos.x, laserEndPos.y);
            }
        }
        //construct line: end
        sr.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Assets.darkBlueColor);
        sr.rect(cam.position.x-540+1080-120, 40, 80, 40);
        sr.setColor(Assets.darkerBlueColor);
        sr.rect(cam.position.x-540+1080-120, 80, 80, 40);
        sr.end();
    }
    private void renderPlayerGuide() {
        if(this.level.gameState==GAME_START) {
            batch.setProjectionMatrix(cam.combined);
            batch.begin();
            batch.setColor(new Color(1, 1, 1, level.playerGuide.alpha));
            batch.draw(Assets.playerTexture,
                    level.playerGuide.pos.x-level.playerGuide.size/2,
                    level.playerGuide.pos.y-level.playerGuide.size/2,
                    level.player.size,
                    level.player.size);
            batch.end();
        }
    }
    private void renderPlayer() {
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.setColor(new Color(1, 1, 1, 0.078f*level.player.alpha));
        batch.draw(Assets.playerShadowTexture,
                level.player.pos.x-level.player.size/2-33+level.player.offset.x,
                level.player.pos.y - level.player.size/2-33+level.player.offset.y,
                level.player.size+60,
                level.player.size+60);
        batch.flush();
        batch.setColor(Assets.shadowColor.r, Assets.shadowColor.g, Assets.shadowColor.b, level.player.alpha);

        batch.draw(Assets.playerGunTexture,
                level.player.pos.x-level.player.size/2-15, level.player.pos.y-level.player.size/2-15,
                35, 35,
                level.player.size+30,
                level.player.size+30, 1, 1, gunRotation+30, 0, 0, 71, 71, false, false);
        batch.draw(Assets.playerTexture,
                level.player.pos.x-level.player.size/2+level.player.offset.x,
                level.player.pos.y-level.player.size/2+level.player.offset.y,
                level.player.size,
                level.player.size);
        batch.end();
        sr.begin(ShapeRenderer.ShapeType.Filled);
        gunPos.x = (float)(Math.cos(Math.toRadians(gunRotation+30))*20-Math.sin(Math.toRadians(gunRotation+30))*20+level.player.pos.x);
        gunPos.y = (float)(Math.sin(Math.toRadians(gunRotation+30))*20+Math.cos(Math.toRadians(gunRotation+30))*20+level.player.pos.y);
        sr.setColor(Assets.darkerBlueColor);
        sr.ellipse(level.player.pos.x-5-2+level.player.vel.x*5/300, level.player.pos.y-7+level.player.vel.y*20/(level.player.j*2), 6, 15-(15-playerEyes.get()));
        sr.ellipse(level.player.pos.x+2+level.player.vel.x*5/300, level.player.pos.y-7+level.player.vel.y*20/(level.player.j*2), 6, 15-(15-playerEyes.get()));
        if(Math.random()*1000<5) {
            Animations.animate(
                    Animations.AnimationEase.inOut,
                    Animations.AnimationTiming.Linear,
                    Animations.AnimationAction.force,
                    playerEyes,
                    Animations.AnimationMove.to,
                    0, false, 100, 0
                    );
            Animations.animate(
                    Animations.AnimationEase.inOut,
                    Animations.AnimationTiming.Linear,
                    Animations.AnimationAction.waitPrev,
                    playerEyes,
                    Animations.AnimationMove.to,
                    15, false, 50, 100
            );

        }
        sr.end();
    }
    private void renderPad(Platform p) {
        if(p.dir==PAD_DIR_NONE) {
            sr.setColor(Assets.darkBlueColor.r, Assets.darkBlueColor.g, Assets.darkBlueColor.b, p.added?1f:0.75f);
            sr.rect(p.rect.x+p.offset.x, p.rect.y+p.offset.y, p.rect.width, p.rect.height);
            if(p.fixed && p.added) {
                sr.setColor(Assets.darkerBlueColor);
                sr.rect(p.rect.x+p.offset.x, p.rect.y + p.rect.height / 2+p.offset.y, p.rect.width, p.rect.height / 2);
            }
        } else if(p.dir==PAD_DIR_FINISH) {
            sr.setColor(Assets.shadowColor);
            sr.rect(p.rect.x+p.offset.x, p.rect.y+p.offset.y, p.rect.width, p.rect.height);
        }
        sr.rect(p.rect.x+p.offset.x, p.rect.y-20+p.offset.y, p.rect.width, 20, Color.CLEAR, Color.CLEAR, Assets.playerShadowColor, Assets.playerShadowColor);
        if(p.hasShield) {
            sr.setColor(Color.WHITE);
            sr.rectLine(p.rect.x, p.rect.y, p.rect.x, p.rect.y+p.rect.height, 5);
            sr.rectLine(p.rect.x, p.rect.y+p.rect.height, p.rect.x+p.rect.width, p.rect.y+p.rect.height, 5);
            sr.rectLine(p.rect.x+p.rect.width, p.rect.y+p.rect.height, p.rect.x+p.rect.width, p.rect.y, 5);
            sr.rectLine(p.rect.x+p.rect.width, p.rect.y, p.rect.x, p.rect.y, 5);
        }
    }
    private void renderPads() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setProjectionMatrix(cam.combined);
        for(Platform p : level.pads) {
            renderPad(p);
        }
        for(Platform p : level.lpads) {
            renderPad(p);
        }
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    private void getLevelMinMax() {
        float min = 0;
        float max = 1080;
        for(Platform p : level.lpads) {
            if(p.rect.x<min) min = p.rect.x;
            if(p.rect.x+p.rect.width>max) max = p.rect.x+p.rect.width;
        }
        this.minX = min;
        this.maxX = max;
    }
}
