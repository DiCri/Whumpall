package it.manueldicriscito.whumpall.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

import it.manueldicriscito.whumpall.Animations;
import it.manueldicriscito.whumpall.Assets;
import it.manueldicriscito.whumpall.CircleButton;
import it.manueldicriscito.whumpall.DiCriTimer;
import it.manueldicriscito.whumpall.Image;
import it.manueldicriscito.whumpall.Level;
import it.manueldicriscito.whumpall.LevelRenderer;
import it.manueldicriscito.whumpall.Platform;
import it.manueldicriscito.whumpall.Whumpall;
import it.manueldicriscito.whumpall.Particles;

import static it.manueldicriscito.whumpall.Whumpall.LEVELSTATE_COMPLETED;
import static it.manueldicriscito.whumpall.Whumpall.LEVELSTATE_LOCKED;
import static it.manueldicriscito.whumpall.Whumpall.LEVELSTATE_UNLOCKED;
import static it.manueldicriscito.whumpall.Whumpall.PAD_DIR_NONE;
import static it.manueldicriscito.whumpall.Whumpall.PAD_TYPE_HORIZONTAL;
import static it.manueldicriscito.whumpall.Whumpall.getScreenBottom;
import static it.manueldicriscito.whumpall.Whumpall.getScreenLeft;
import static it.manueldicriscito.whumpall.Whumpall.getScreenRight;
import static it.manueldicriscito.whumpall.Whumpall.getScreenTop;
import static it.manueldicriscito.whumpall.Whumpall.globalVars;
import static it.manueldicriscito.whumpall.Whumpall.playerLine;


public class PlayScreen implements Screen, InputProcessor {
    public static final int GAME_START = 0;
    public static final int GAME_PAUSE = 1;
    public static final int GAME_PLAY = 2;
    public static final int GAME_FINISH = 3;


    private int gameState;
    private boolean tap = false;
    private final int lv;

    private final Whumpall game;
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private final LevelRenderer lr;
    private final Level level;

    private final DiCriTimer gsTimer;
    private final Image bigCircle;

    private final Animations.AnimatableFloat labelLevelCompletedAlpha = new Animations.AnimatableFloat(0);
    private final Animations.AnimatableFloat labelAttemptsAlpha = new Animations.AnimatableFloat(0);
    private final Animations.AnimatableFloat labelBlocksPlacedAlpha = new Animations.AnimatableFloat(0);
    private final Animations.AnimatableFloat labelTotalBlocksWidthAlpha = new Animations.AnimatableFloat(0);

    Map<String, CircleButton> cbtn = new HashMap<>();
    boolean testCoords = false;

    private Vector3 touchPos = new Vector3();

    public PlayScreen(Whumpall game, int lv) {
        this(game, lv, null);
    }
    public PlayScreen(Whumpall game, int lv, Level level) {
        this.game = game;
        Gdx.input.setInputProcessor(this);

        this.lv = lv;
        if(level==null) {
            this.level = new Level(lv);
        } else {
            this.level = level;
        }
        lr = new LevelRenderer(game, this.level);

        gsTimer = new DiCriTimer();
        this.level.gsTimer = gsTimer;
        gsTimer.start();


        bigCircle = new Image();
        bigCircle.setTexture(Assets.Textures.get("bigCircle"));
        bigCircle.setColor(new Color(1, 1, 1, 1));
        bigCircle.makeAnimatable();
        bigCircle.setRect(new Rectangle(0, 0, 0, 0));
        if(lv!=-1) {
            bigCircle.setRect(new Rectangle(this.level.player.pos.x - 2000, this.level.player.pos.y - 2000, 4000, 4000));
            Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Expo, Animations.AnimationAction.force, bigCircle.getAnimatableRect().width, Animations.AnimationMove.to, 0, false, 1500, 0);
            Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Expo, Animations.AnimationAction.force, bigCircle.getAnimatableRect().x, Animations.AnimationMove.to, this.level.player.pos.x, false, 1500, 0);
            Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Expo, Animations.AnimationAction.force, bigCircle.getAnimatableRect().y, Animations.AnimationMove.to, this.level.player.pos.y, false, 1500, 0);
        }
        this.level.showTapToStart();

        CircleButton cb = new CircleButton();
        cb.texture = Assets.Textures.get("back");
        cb.color = new Animations.AnimatableColor(Assets.Colors.get("lightBlue"));
        cb.defaultColor.set(Assets.Colors.get("lightBlue"));
        cb.shadowColor.set(Assets.Colors.get("darkerBlue"));
        cb.tapColor.set(Assets.Colors.get("lightBlue"));
        cb.size.set(0);
        cb.dSize = 100;
        cb.hSize = 110;
        cb.textureSize = 100;
        cb.pos = new Vector2(1080f/4-50, 500);
        cbtn.put("back", new CircleButton(cb));
        cb.texture = Assets.Textures.get("retry");
        cb.color = new Animations.AnimatableColor(Assets.Colors.get("fuchsia"));
        cb.defaultColor.set(Assets.Colors.get("fuchsia"));
        cb.tapColor.set(Assets.Colors.get("fuchsia"));
        cb.pos = new Vector2(1080f/4*3+50, 500);
        cbtn.put("retry", new CircleButton(cb));
        cb.texture = Assets.Textures.get("next");
        cb.color = new Animations.AnimatableColor(Assets.Colors.get("green"));
        cb.textureSize = 150;
        cb.pos = new Vector2(1080f/2, 600);
        cbtn.put("next", new CircleButton(cb));

        cb.pos.set(getScreenRight(game.cam)-100, getScreenTop(game.cam)-100);
        cb.size.set(70); cb.setSizes(70, 80);
        cb.color.set(Assets.Colors.get("darkBlue"));
        cb.shadowColor.set(Color.WHITE);
        cb.defaultColor.set(Assets.Colors.get("darkBlue"));
        cb.tapColor.set(Assets.Colors.get("darkerBlue"));
        cb.texture = Assets.Textures.get("pause");
        cb.textureSize = 70;
        cbtn.put("pause", new CircleButton(cb));


        if(lv==-1) {
            cb.texture = Assets.Textures.get("edit");
            cb.pos.set(getScreenRight(game.cam) - 300, getScreenTop(game.cam) - 100);
            cb.size.set(70); cb.setSizes(70, 80);
            cb.color.set(Assets.Colors.get("darkBlue"));
            cb.shadowColor.set(Color.WHITE);
            cb.defaultColor.set(Assets.Colors.get("darkBlue"));
            cb.tapColor.set(Assets.Colors.get("darkerBlue"));
            cb.textureSize = 70;
            cbtn.put("edit", new CircleButton(cb));
        }

        playerLine.clear();
    }

    @Override
    public void show() {

    }
    @Override
    public void hide() {

    }

    @Override
    public void render (float delta) {

        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        lr.render();
        if(lv==-1) playerLine.add(new Vector2(level.player.pos));

        bigCircle.arect.height = bigCircle.arect.width;
        bigCircle.render(game.batch);

        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        game.sr.setColor(Color.BLACK);
        if(lv==-1) for(int k=0; k<playerLine.size()-1; k++) {
            game.sr.rectLine(playerLine.get(k), playerLine.get(k+1), 6);
        }
        for(Map.Entry<String, CircleButton> entry : cbtn.entrySet()) {
            if(entry.getKey().equals("back")
                || entry.getKey().equals("next")
                || entry.getKey().equals("retry")) {
                if(bigCircle.arect.getWidth()>1920) entry.getValue().drawCircle(game.sr);
            } else {
                entry.getValue().drawCircle(game.sr);
            }
        }

        game.sr.end();
        game.batch.begin();
        for(Map.Entry<String, CircleButton> entry : cbtn.entrySet()) {
            if(entry.getKey().equals("back")
                    || entry.getKey().equals("next")
                    || entry.getKey().equals("retry")) {
                if(bigCircle.arect.getWidth()>1920) entry.getValue().drawTexture(game.batch);
            } else {
                entry.getValue().drawTexture(game.batch);
            }
        }
        BitmapFont bmfont = Assets.Fonts.get("KoHoRegular100");
        glyphLayout.setText(bmfont, "Level " + level.currentLevel + " Completed");
        bmfont.setColor(0, 0, 0, labelLevelCompletedAlpha.get());
        bmfont.draw(game.batch, "Level " + level.currentLevel + " Completed", 1080f/2-glyphLayout.width/2, 1380);
        bmfont = Assets.Fonts.get("KoHoItalic50");
        glyphLayout.setText(bmfont, "Attempts: " + level.attempts);
        bmfont.setColor(0, 0, 0, labelAttemptsAlpha.get());
        bmfont.draw(game.batch, "Attempts: " + level.attempts, 1080f/2-glyphLayout.width/2, 1200);
        glyphLayout.setText(bmfont, "Blocks placed: " + level.pads.size());
        bmfont.setColor(0, 0, 0, labelBlocksPlacedAlpha.get());
        bmfont.draw(game.batch, "Blocks placed: " + level.pads.size(), 1080f/2-glyphLayout.width/2, 1100);
        glyphLayout.setText(bmfont, "Total blocks width: " + (int)level.totalBlocksWidth.get());
        bmfont.setColor(0, 0, 0, labelTotalBlocksWidthAlpha.get());
        bmfont.draw(game.batch, "Total blocks width: " + (int)level.totalBlocksWidth.get(), 1080f/2-glyphLayout.width/2, 1000);
        game.batch.end();

        // TAP TO START: begin
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        game.sr.setColor(0, 0, 0, 0.45f*level.ttsAlpha.get());
        game.sr.rect(getScreenLeft(game.cam), getScreenBottom(game.cam), 1080, getScreenTop(game.cam)-getScreenBottom(game.cam));
        game.sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        game.batch.begin();
        bmfont = Assets.Fonts.get("KoHoRegular100");
        glyphLayout.setText(bmfont, "tap to start");
        bmfont.setColor(1, 1, 1, 1*level.ttsAlpha.get());
        bmfont.draw(game.batch, "tap to start", getScreenLeft(game.cam)+1080f/2-glyphLayout.width/2, 1200);
        game.batch.end();
        // TAP TO START: end

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.sr.setProjectionMatrix(game.cam.combined);
        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        game.sr.setColor(0, 0, 0, level.darkDisplay.get());
        game.sr.rect(getScreenLeft(game.cam), getScreenBottom(game.cam), 1080, getScreenTop(game.cam)-getScreenBottom(game.cam));
        game.sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        if(level.darkDisplay.get()==1f) {
            this.gameState = GAME_START;
            level.gameState = GAME_START;
            level.respawnPlayer();
            level.respawnPlayerGuide();
            level.pads.clear();
            level.gsTimer.reset();
        }

        if(testCoords) {
            game.batch.begin();
            Assets.Fonts.get("KoHoRegular50").draw(game.batch, "[" + (int) touchPos.x + ", " + (int) touchPos.y + "]", getScreenRight(game.cam) - 300, getScreenBottom(game.cam) + 100);
            game.batch.end();
        }

        //test

        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        game.sr.setColor(Color.WHITE);
        game.sr.rect(getScreenLeft(game.cam)+20+11, getScreenTop(game.cam)-160+43, 90-(90f*level.totalBlocksWidth.get()/level.maxMana), 43);

        //test
        if(testCoords) {
            game.sr.rectLine(getScreenLeft(game.cam), touchPos.y, getScreenRight(game.cam), touchPos.y, 2);
            game.sr.rectLine(touchPos.x, getScreenBottom(game.cam), touchPos.x, getScreenTop(game.cam), 2);
        }

        game.sr.end();
        Particles.render(game.sr, delta);
        Gdx.graphics.setTitle("Whumpall ["+Gdx.graphics.getFramesPerSecond()+"fps]");
    }


    private void update(float delta) {
        level.update(delta, game.cam);
        //begin
        game.cam.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        Animations.run();

        //buttons

        if(lv==-1) cbtn.get("edit").pos.set(getScreenRight(game.cam)-275, getScreenTop(game.cam)-100);
        boolean btnTouch = false;
        for(Map.Entry<String, CircleButton> entry : cbtn.entrySet()) {
            entry.getValue().update(touchPos);
            if(entry.getValue().justClicked()) {
                switch(entry.getKey()) {
                    case "back":
                        game.setScreen(new LevelListScreen(game));
                        break;
                    case "edit":
                        game.setScreen(new CreateScreen(game, level.levelData, "PlayScreen"));
                        break;
                    case "retry":
                        level.resetLevel();
                        gameState = GAME_START;
                        level.gameState = GAME_START;
                        level.player.pause();
                        Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().width,Animations.AnimationMove.to,0, false, 1000, 0);
                        Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force, bigCircle.getAnimatableRect().x,Animations.AnimationMove.to,level.player.pos.x, false, 1000, 0);
                        Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().y,Animations.AnimationMove.to,level.player.pos.y, false, 1000, 0);
                        Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,labelLevelCompletedAlpha,Animations.AnimationMove.to, 0, false, 200, 0);
                        Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,labelAttemptsAlpha,Animations.AnimationMove.to,0, false, 200, 0);
                        Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,labelBlocksPlacedAlpha,Animations.AnimationMove.to, 0, false, 200, 0);
                        Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,labelTotalBlocksWidthAlpha,Animations.AnimationMove.to,0, false, 200, 0);
                        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Sine, Animations.AnimationAction.force, cbtn.get("retry").size, Animations.AnimationMove.to,0f, false, 400, 0);
                        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Sine, Animations.AnimationAction.force, cbtn.get("back").size, Animations.AnimationMove.to,0f, false, 400, 0);
                        Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Sine, Animations.AnimationAction.force, cbtn.get("next").size, Animations.AnimationMove.to,0f, false, 400, 0);
                        level.showTapToStart();
                        break;
                }
            }
            btnTouch |= entry.getValue().justTouched();
        }
        //buttons

        if (gameState != GAME_FINISH) {
            if (level.gameState == GAME_FINISH) {
                gameState = GAME_FINISH;
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Back,Animations.AnimationAction.force,cbtn.get("back").size,Animations.AnimationMove.to,100, false, 1500, 2000);
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Back,Animations.AnimationAction.force,cbtn.get("retry").size,Animations.AnimationMove.to,100, false, 1500, 2250);
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Back,Animations.AnimationAction.force,cbtn.get("next").size,Animations.AnimationMove.to,150, false, 1500, 2500);
                bigCircle.setRect(new Rectangle(level.player.pos.x, level.player.pos.y, 0, 0));
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().width, Animations.AnimationMove.to, 4000, false, 1800, 0);
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().x,Animations.AnimationMove.by,-2000, false, 1800, 0);
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().y,Animations.AnimationMove.by,-2000, false, 1800, 0);
                Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,labelLevelCompletedAlpha,Animations.AnimationMove.to,1, false, 510, 1000);
                Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,labelAttemptsAlpha,Animations.AnimationMove.to,1, false, 300, 1500);
                Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,labelBlocksPlacedAlpha,Animations.AnimationMove.to,1, false, 300, 1700);
                Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,labelTotalBlocksWidthAlpha,Animations.AnimationMove.to,1, false, 300, 1900);

                game.prefs.putInteger("lv"+(lv-1), LEVELSTATE_COMPLETED);
                if(game.prefs.getInteger("lv"+(lv))==LEVELSTATE_LOCKED) {
                    game.prefs.putInteger("lv"+(lv), LEVELSTATE_UNLOCKED);
                }
                game.prefs.flush();
            }
        }

        if (Gdx.input.justTouched() && !btnTouch) {
            tap = true;
            if (gameState == GAME_START) {
                level.hideTapToStart();
                gameState = GAME_PLAY;
                level.gameState = GAME_PLAY;
                level.player.resume();
            } else if(gameState!=GAME_FINISH) {
                if(level.pads.size()<level.maxPads && level.getManaUsed()<level.maxMana) {
                    Platform p = new Platform();
                    p.rect.set(new Rectangle(touchPos.x, touchPos.y-20, 0, 40));
                    p.type = PAD_TYPE_HORIZONTAL;
                    p.fixed = false;
                    p.dir = PAD_DIR_NONE;
                    level.pads.add(p);

                    level.addingPad = true;
                    globalVars.put("lastTapTime", System.currentTimeMillis());
                }
            }
        } else if (Gdx.input.isTouched() && !btnTouch) {
            if (level.addingPad && level.pads.size() > 0) {
                Platform p = level.pads.get(level.pads.size()-1);
                if(!p.added) {
                    float addedBlocksMana = (float)globalVars.get("addedBlocksMana");
                    p.rect.setWidth(touchPos.x - p.rect.x);
                    if(level.getManaUsed()>level.maxMana) {
                        p.rect.setWidth((level.maxMana-addedBlocksMana)*(p.rect.width>=0?1:-1));
                    }
                }

            }
        } else if (level.addingPad) {
            level.addingPad = false;

            Platform p = level.pads.get(level.pads.size()-1);
            p.fix();
            if (!p.added) {
                globalVars.put("lastTapTime", System.currentTimeMillis());
                p.add();
            }
        } else if(tap) {
            // Mouse Leave
            tap = false;
        }
        //end

        // camera control
        if(level.player.pos.x>=lr.minX+540 && level.player.pos.x<=lr.maxX-540) game.cam.position.x = level.player.pos.x;
        if(level.player.pos.x<lr.minX+540) game.cam.position.x = lr.minX+540;
        if(level.player.pos.x>lr.maxX-540) game.cam.position.x = lr.maxX-540;
        game.cam.update();
        game.sr.setProjectionMatrix(game.cam.combined);
        game.batch.setProjectionMatrix(game.cam.combined);
    }
    @Override
    public void resize(int width, int height) {
        game.port.update(width, height);
        game.cam.setToOrtho(false, 1080, height*1080f/width);
        game.cam.position.set(540, 960, 0);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {


    }


    @Override
    public void dispose() {
        game.sr.dispose();
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        /*
        float x = Gdx.input.getDeltaX();
        float y = Gdx.input.getDeltaY();
        */
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
