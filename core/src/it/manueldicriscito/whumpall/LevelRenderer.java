package it.manueldicriscito.whumpall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static it.manueldicriscito.whumpall.Screens.PlayScreen.GAME_DEATH;
import static it.manueldicriscito.whumpall.Screens.PlayScreen.GAME_START;
import static it.manueldicriscito.whumpall.Whumpall.PAD_DIR_FINISH;
import static it.manueldicriscito.whumpall.Whumpall.PAD_TYPE_HORIZONTAL;
import static it.manueldicriscito.whumpall.Whumpall.PAD_TYPE_VERTICAL;
import static it.manueldicriscito.whumpall.Whumpall.getAngle;
import static it.manueldicriscito.whumpall.Whumpall.getScreenBottom;
import static it.manueldicriscito.whumpall.Whumpall.getScreenLeft;
import static it.manueldicriscito.whumpall.Whumpall.getScreenRight;
import static it.manueldicriscito.whumpall.Whumpall.getScreenTop;
import static it.manueldicriscito.whumpall.Whumpall.globalVars;
import static it.manueldicriscito.whumpall.Whumpall.playerLine;

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

    public static List<PadTouchLine> ptls = new ArrayList<>();

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
        if(level.gameState!=GAME_DEATH) {
            renderPlayer();
            renderPlayerGuide();
        }
        renderPads();
        renderPadTouchLines();
        renderObjects();
        renderSpikes();
        renderCoin();
    }
    private void renderCoin() {
        if(level.coin!=null) {
            batch.begin();
            level.coin.draw(batch);
            batch.end();
        }
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
        Color mainColor = Assets.Colors.get("lightBlue");
        Color darkColor = Assets.Colors.get("darkLightBlue");

        sr.setColor(mainColor);
        sr.rect(getScreenLeft(cam), getScreenBottom(cam), getScreenRight(cam)-getScreenLeft(cam), getScreenTop(cam)-getScreenBottom(cam));
        sr.rect(getScreenLeft(cam), getScreenBottom(cam), getScreenRight(cam)-getScreenLeft(cam), 1920f/3, darkColor, darkColor, mainColor, mainColor);
        this.sr.end();
    }
    private void renderSpikes() {
        this.batch.begin();
        batch.setProjectionMatrix(cam.combined);

        for(Spike s : level.spikes) {
            batch.setColor(Color.WHITE);
            batch.draw(Assets.Textures.get("spikeBG"), s.pos.x-s.size/2f, s.pos.y-s.size/2f, s.size, s.size);
            int border = 16;
            batch.setColor(Assets.Colors.get("darkBlue"));
            batch.draw(
                    Assets.Textures.get("spike"),
                    s.pos.x-s.size/2f-border/2f, s.pos.y-s.size/2f-border/2f,
                    s.size/2f+border/2f, s.size/2f+border/2f,
                    s.size+border, s.size+border,
                    1, 1,
                    s.rotation,
                    0, 0, 400, 400,
                    false, false
            );
            batch.setColor(Assets.Colors.get("darkerBlue"));
            batch.draw(
                    Assets.Textures.get("spike"),
                    s.pos.x-s.size/2f, s.pos.y-s.size/2f,
                    s.size/2f, s.size/2f,
                    s.size, s.size,
                    1, 1,
                    s.rotation,
                    0, 0, 400, 400,
                    false, false);
        }
        this.batch.end();
    }
    private void renderObjects() {
        batch.begin();
        int psize = 0;
        for(Platform p : level.pads) {
            psize += p.added?1:0;
        }
        Assets.Fonts.get("KoHoBold50").setColor(1, 1, 1, 1);
        Assets.Fonts.get("KoHoBold50").draw(batch, ""+(level.maxPads-psize), getScreenLeft(cam)+150+80+20, getScreenTop(cam)-50-20);
        batch.setColor(Color.WHITE);
        batch.draw(Assets.Textures.get("battery"), getScreenLeft(cam)+20, getScreenTop(cam)-160, 128, 128);
        Assets.Fonts.get("KoHoBold40").setColor(1, 1, 1, 1f);
        Assets.Fonts.get("KoHoBold40").draw(batch,Integer.toString((int)Math.ceil(100-(100*level.totalBlocksWidth.get()/level.maxMana)))+'%', getScreenLeft(cam)+40, getScreenTop(cam)-150);
        batch.end();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.WHITE);
        sr.rect(getScreenLeft(cam)+20+11, getScreenTop(cam)-160+43, 90-(90f*level.totalBlocksWidth.get()/level.maxMana), 43);

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
        sr.setColor(Assets.Colors.get("darkBlue"));
        sr.rect(getScreenLeft(cam)+150, getScreenTop(cam)-130, 80, 40);
        sr.setColor(Assets.Colors.get("darkerBlue"));
        sr.rect(getScreenLeft(cam)+150, getScreenTop(cam)-90, 80, 40);
        sr.setColor(Color.WHITE);
        sr.rectLine(getScreenLeft(cam)+150, getScreenTop(cam)-130, getScreenLeft(cam)+150, getScreenTop(cam)-50, 5);
        sr.rectLine(getScreenLeft(cam)+150, getScreenTop(cam)-50, getScreenLeft(cam)+150+80, getScreenTop(cam)-50, 5);
        sr.rectLine(getScreenLeft(cam)+150+80, getScreenTop(cam)-50, getScreenLeft(cam)+150+80, getScreenTop(cam)-130, 5);
        sr.rectLine(getScreenLeft(cam)+150+80, getScreenTop(cam)-130, getScreenLeft(cam)+150, getScreenTop(cam)-130, 5);
        sr.end();
    }
    private void renderPlayerGuide() {
        if(this.level.gameState==GAME_START) {
            batch.setProjectionMatrix(cam.combined);
            batch.begin();
            batch.setColor(new Color(1, 1, 1, level.playerGuide.alpha));
            batch.draw(Assets.Textures.get("player"),
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
        batch.draw(Assets.Textures.get("playerShadow"),
                level.player.pos.x-level.player.size/2-33+level.player.offset.x,
                level.player.pos.y - level.player.size/2-33+level.player.offset.y,
                level.player.size+60,
                level.player.size+60);
        batch.flush();
        batch.setColor(Assets.Colors.get("shadow").r, Assets.Colors.get("shadow").g, Assets.Colors.get("shadow").b, level.player.alpha);

        batch.draw(Assets.Textures.get("playerGun"),
                level.player.pos.x-level.player.size/2-15, level.player.pos.y-level.player.size/2-15,
                35, 35,
                level.player.size+30,
                level.player.size+30, 1, 1, gunRotation+30, 0, 0, 71, 71, false, false);
        batch.draw(Assets.Textures.get("player"),
                level.player.pos.x-level.player.size/2+level.player.offset.x,
                level.player.pos.y-level.player.size/2+level.player.offset.y,
                level.player.size,
                level.player.size);
        batch.end();
        sr.begin(ShapeRenderer.ShapeType.Filled);
        gunPos.x = (float)(Math.cos(Math.toRadians(gunRotation+30))*20-Math.sin(Math.toRadians(gunRotation+30))*20+level.player.pos.x);
        gunPos.y = (float)(Math.sin(Math.toRadians(gunRotation+30))*20+Math.cos(Math.toRadians(gunRotation+30))*20+level.player.pos.y);
        sr.setColor(Assets.Colors.get("darkerBlue"));
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
        Color down,up;
        // change "if" with "switch" when needed
        if (p.dir == PAD_DIR_FINISH) {
            down = Assets.Colors.get("shadow");
            up = Assets.Colors.get("shadow");
        } else {
            down = Assets.Colors.get("darkBlue");
            up = Assets.Colors.get("darkerBlue");
        }
        short upp;
        if(p.upperPiece==null) upp = 0; else upp = (short)p.upperPiece.get();

        // lines under the pad
        if(p.superJump) {
            sr.rectLine(p.rect.x + 25, p.rect.y + 10, p.rect.x + 25, p.rect.y + 30 + upp, 15);
            sr.rectLine(p.rect.x + p.rect.width - 25, p.rect.y + 10, p.rect.x + p.rect.width - 25, p.rect.y + 10 + 20 + upp, 15);
        }

        if(p.fixed) {
            if(p.added) {
                switch(p.type) {
                    case PAD_TYPE_HORIZONTAL:
                        sr.setColor(up);
                        sr.rect(p.rect.x, p.rect.y+p.rect.height/2+upp, p.rect.width, p.rect.height/2);
                        sr.setColor(down);
                        sr.rect(p.rect.x, p.rect.y, p.rect.width, p.rect.height/2);
                        break;
                    case PAD_TYPE_VERTICAL:
                        sr.setColor(up);
                        sr.rect(p.rect.x, p.rect.y, p.rect.width/2, p.rect.height);
                        sr.setColor(down);
                        sr.rect(p.rect.x+20, p.rect.y, p.rect.width/2, p.rect.height);
                        break;
                }
            }
        } else {
            sr.setColor(down.r, down.g, down.b, p.added?1f:0.75f);
            sr.rect(p.rect.x, p.rect.y, p.rect.width, p.rect.height);
        }
        sr.rect(p.rect.x, p.rect.y-20, p.rect.width, 20, Color.CLEAR, Color.CLEAR, Assets.Colors.get("playerShadow"), Assets.Colors.get("playerShadow"));

        if(p.superJump) {
            sr.setColor(up);
            sr.rect(p.rect.x, p.rect.y, 40, 20);
            sr.rect(p.rect.x, p.rect.y+20+upp, 40, 20);
            sr.rect(p.rect.x+p.rect.width-40, p.rect.y, 40, 20);
            sr.rect(p.rect.x+p.rect.width-40, p.rect.y+20+upp, 40, 20);

            sr.setColor(down);
            sr.circle(p.rect.x+10, p.rect.y+10, 2.5f);
            sr.circle(p.rect.x+10, p.rect.y+30+upp, 2.5f);
            sr.circle(p.rect.x+p.rect.width-10, p.rect.y+10, 2.5f);
            sr.circle(p.rect.x+p.rect.width-10, p.rect.y+30+upp, 2.5f);
            sr.rectLine(p.rect.x+10, p.rect.y+10, p.rect.x+10, p.rect.y+30+upp, 5);
            sr.rectLine(p.rect.x+p.rect.width-10, p.rect.y+10,p.rect.x+p.rect.width-10, p.rect.y+30+upp,5);
        }
        /*sr.setColor(Color.WHITE);
        sr.rect(p.rect.x+10, p.rect.y, 20, 20);
        sr.triangle(
                p.rect.x, p.rect.y+20+upp,
                p.rect.x+20, p.rect.y+40+upp,
                p.rect.x+40, p.rect.y+20+upp
        );
        sr.rect(p.rect.x+p.rect.width-30, p.rect.y, 20, 20);
        sr.triangle(
                p.rect.x+p.rect.width-40, p.rect.y+20+upp,
                p.rect.x+p.rect.width-20, p.rect.y+40+upp,
                p.rect.x+p.rect.width, p.rect.y+20+upp
        );*/
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
    private void renderPadTouchLines() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for(PadTouchLine ptl : ptls) {
            ptl.render(cam, sr);

        }
        Iterator<PadTouchLine> i = ptls.iterator();
        while(i.hasNext()) {
            PadTouchLine ptl = i.next();
            ptl.render(cam, sr);
            if(ptl.alpha.get()==0f) {
                i.remove();
            }
        }
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    static void spawnPadTouchLine(Player player, Platform p, int loc) {
        PadTouchLine ptl = new PadTouchLine();
        ptl.spawn(player, p, loc);
        ptls.add(ptl);
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
