package it.manueldicriscito.whumpall.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import it.manueldicriscito.whumpall.Animations;
import it.manueldicriscito.whumpall.Assets;
import it.manueldicriscito.whumpall.CircleButton;
import it.manueldicriscito.whumpall.DiCriTimer;
import it.manueldicriscito.whumpall.Image;
import it.manueldicriscito.whumpall.Level;
import it.manueldicriscito.whumpall.LevelRenderer;
import it.manueldicriscito.whumpall.Platform;
import it.manueldicriscito.whumpall.Range;
import it.manueldicriscito.whumpall.Whumpall;
import it.manueldicriscito.whumpall.Particles;

import static it.manueldicriscito.whumpall.Whumpall.PAD_DIR_NONE;
import static it.manueldicriscito.whumpall.Whumpall.PAD_TYPE_HORIZONTAL;
import static it.manueldicriscito.whumpall.Whumpall.getScreenBottom;
import static it.manueldicriscito.whumpall.Whumpall.getScreenLeft;
import static it.manueldicriscito.whumpall.Whumpall.getScreenRight;
import static it.manueldicriscito.whumpall.Whumpall.getScreenTop;
import static it.manueldicriscito.whumpall.Whumpall.globalVars;


public class CreateScreen implements Screen, InputProcessor {
    public static final int GAME_START = 0;
    public static final int GAME_PAUSE = 1;
    public static final int GAME_PLAY = 2;
    public static final int GAME_FINISH = 3;


    private int gameState;
    boolean tap = false;
    int lv;

    private Whumpall game;
    private GlyphLayout glyphLayout = new GlyphLayout();
    private LevelRenderer lr;
    private Level level;

    private DiCriTimer gsTimer;
    private Image bigCircle;

    private Animations.AnimatableFloat labelLevelCompletedAlpha = new Animations.AnimatableFloat(0);
    private Animations.AnimatableFloat labelAttemptsAlpha = new Animations.AnimatableFloat(0);
    private Animations.AnimatableFloat labelBlocksPlacedAlpha = new Animations.AnimatableFloat(0);
    private Animations.AnimatableFloat labelTotalBlocksWidthAlpha = new Animations.AnimatableFloat(0);

    private CircleButton backButton;
    private CircleButton nextButton;
    private CircleButton retryButton;

    private Vector3 touchPos = new Vector3();

    public CreateScreen(Whumpall game, int lv) {
        this.game = game;
        Gdx.input.setInputProcessor(this);

        this.lv = lv;
        this.level = new Level(lv);
        lr = new LevelRenderer(game, this.level);

        gsTimer = new DiCriTimer();
        this.level.gsTimer = gsTimer;
        gsTimer.start();


        bigCircle = new Image();
        bigCircle.setTexture(Assets.bigCircleTexture);
        bigCircle.setColor(new Color(1, 1, 1, 1));
        bigCircle.makeAnimatable();
        bigCircle.setRect(new Rectangle(this.level.player.pos.x-2000, this.level.player.pos.y-2000, 4000, 4000));

        Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().width,Animations.AnimationMove.to,0, false, 1500, 0);
        Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force, bigCircle.getAnimatableRect().x,Animations.AnimationMove.to,this.level.player.pos.x, false, 1500, 0);
        Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().y,Animations.AnimationMove.to,this.level.player.pos.y, false, 1500, 0);
        level.showTapToStart();

        backButton = new CircleButton();
        backButton.texture = Assets.backButtonTexture;
        backButton.color = new Animations.AnimatableColor(Assets.lightBlueColor);
        backButton.size.set(0);
        backButton.textureSize = 100;
        backButton.pos = new Vector2(1080f/4-50, 500);
        nextButton = new CircleButton();
        nextButton.texture = Assets.nextButtonTexture;
        nextButton.color = new Animations.AnimatableColor(Assets.greenColor);
        nextButton.size.set(0);
        nextButton.textureSize = 150;
        nextButton.pos = new Vector2(1080f/2, 600);
        retryButton = new CircleButton();
        retryButton.texture = Assets.retryButtonTexture;
        retryButton.color = new Animations.AnimatableColor(Assets.fuchsiaColor);
        retryButton.size.set(0);
        retryButton.textureSize = 100;
        retryButton.pos = new Vector2(1080f/4*3+50, 500);

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

        bigCircle.arect.height = bigCircle.arect.width;
        bigCircle.render(game.batch);
        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        if(bigCircle.arect.getWidth()>1920) {
            backButton.drawCircle(game.sr);
            nextButton.drawCircle(game.sr);
            retryButton.drawCircle(game.sr);
        }
        game.sr.end();
        game.batch.begin();
        if(bigCircle.arect.getWidth()>1920 && gameState == GAME_FINISH) {
            backButton.drawTexture(game.batch);
            nextButton.drawTexture(game.batch);
            retryButton.drawTexture(game.batch);
        }
        glyphLayout.setText(Assets.fontKoHoRegular100, "Level " + level.currentLevel + " Completed");
        Assets.fontKoHoRegular100.setColor(0, 0, 0, labelLevelCompletedAlpha.get());
        Assets.fontKoHoRegular100.draw(game.batch, "Level " + level.currentLevel + " Completed", 1080f/2-glyphLayout.width/2, 1380);
        glyphLayout.setText(Assets.fontKoHoItalic50, "Attempts: " + level.attempts);
        Assets.fontKoHoItalic50.setColor(0, 0, 0, labelAttemptsAlpha.get());
        Assets.fontKoHoItalic50.draw(game.batch, "Attempts: " + level.attempts, 1080f/2-glyphLayout.width/2, 1200);
        glyphLayout.setText(Assets.fontKoHoItalic50, "Blocks placed: " + level.pads.size());
        Assets.fontKoHoItalic50.setColor(0, 0, 0, labelBlocksPlacedAlpha.get());
        Assets.fontKoHoItalic50.draw(game.batch, "Blocks placed: " + level.pads.size(), 1080f/2-glyphLayout.width/2, 1100);
        glyphLayout.setText(Assets.fontKoHoItalic50, "Total blocks width: " + (int)level.totalBlocksWidth.get());
        Assets.fontKoHoItalic50.setColor(0, 0, 0, labelTotalBlocksWidthAlpha.get());
        Assets.fontKoHoItalic50.draw(game.batch, "Total blocks width: " + (int)level.totalBlocksWidth.get(), 1080f/2-glyphLayout.width/2, 1000);
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
        glyphLayout.setText(Assets.fontKoHoRegular100, "tap to start");
        Assets.fontKoHoRegular100.setColor(1, 1, 1, 1*level.ttsAlpha.get());
        Assets.fontKoHoRegular100.draw(game.batch, "tap to start", getScreenLeft(game.cam)+1080f/2-glyphLayout.width/2, 1200);
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
        game.batch.begin();
        game.batch.setColor(Color.WHITE);
        game.batch.draw(Assets.batteryTexture, getScreenLeft(game.cam)+20, getScreenTop(game.cam)-160, 128, 128);
        game.batch.end();
        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        game.sr.setColor(Color.WHITE);

        game.sr.rect(getScreenLeft(game.cam)+20+11, getScreenTop(game.cam)-160+43, 90-(90f*level.totalBlocksWidth.get()/level.maxMana), 43);
        game.sr.end();
        Particles.render(game.sr, delta);
        Gdx.graphics.setTitle("Whumpall ["+Gdx.graphics.getFramesPerSecond()+"fps]");


    }


    private void update(float delta) {
        level.update(delta, game.cam);
        //begin
        Animations.run();
        if (gameState != GAME_FINISH) {
            if (level.gameState == GAME_FINISH) {
                gameState = GAME_FINISH;
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Back,Animations.AnimationAction.force,backButton.size,Animations.AnimationMove.to,100, false, 1500, 2000);
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Back,Animations.AnimationAction.force,retryButton.size,Animations.AnimationMove.to,100, false, 1500, 2250);
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Back,Animations.AnimationAction.force,nextButton.size,Animations.AnimationMove.to,150, false, 1500, 2500);
                bigCircle.setRect(new Rectangle(level.player.pos.x, level.player.pos.y, 0, 0));
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().width, Animations.AnimationMove.to, 4000, false, 1800, 0);
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().x,Animations.AnimationMove.by,-2000, false, 1800, 0);
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().y,Animations.AnimationMove.by,-2000, false, 1800, 0);
                Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,labelLevelCompletedAlpha,Animations.AnimationMove.to,1, false, 510, 1000);
                Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,labelAttemptsAlpha,Animations.AnimationMove.to,1, false, 300, 1500);
                Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,labelBlocksPlacedAlpha,Animations.AnimationMove.to,1, false, 300, 1700);
                Animations.animate(Animations.AnimationEase.in,Animations.AnimationTiming.Linear,Animations.AnimationAction.force,labelTotalBlocksWidthAlpha,Animations.AnimationMove.to,1, false, 300, 1900);
                game.prefs.putBoolean("lv"+(lv-1), true);
                game.prefs.flush();
            }
        }
        if (Gdx.input.justTouched()) {
            tap = true;
            if (gameState == GAME_START) {
                level.hideTapToStart();
                gameState = GAME_PLAY;
                level.gameState = GAME_PLAY;
                level.player.resume();
            } else if(gameState==GAME_FINISH) {
                game.cam.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
                retryButton.update(touchPos);
                backButton.update(touchPos);
                nextButton.update(touchPos);
                if(backButton.justClicked()) {
                    game.setScreen(new LevelListScreen(game));
                }
                /*
                if(retryButton.hover((int)touchPos.x, (int)touchPos.y)) {
                    Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Back,Animations.AnimationAction.force,retryButton.size,Animations.AnimationMove.to,110f, false, 200, 0);
                    retryButton.tap = true;
                }
                if(backButton.hover((int)touchPos.x, (int)touchPos.y)) {
                    Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Back,Animations.AnimationAction.force,backButton.size,Animations.AnimationMove.to,110f, false, 200, 0);
                    backButton.tap = true;
                }
                if(nextButton.hover((int)touchPos.x, (int)touchPos.y)) {
                    Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Back,Animations.AnimationAction.force,nextButton.size,Animations.AnimationMove.to,165f, false, 200, 0);
                    nextButton.tap = true;
                }
                */
            } else {
                game.cam.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
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
        } else if (Gdx.input.isTouched()) {
            game.cam.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
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
            game.cam.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            level.addingPad = false;

            Platform p = level.pads.get(level.pads.size()-1);
            p.fix();
            if (!p.added) {
                globalVars.put("lastTapTime", System.currentTimeMillis());
                p.add();
            }
        } else if(tap) {
            // Mouse Leave

            if(nextButton.tap) {
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Back, Animations.AnimationAction.force, nextButton.size, Animations.AnimationMove.to,150f, false, 200, 0);
                nextButton.tap = false;
            }
            if(backButton.tap) {
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Back,Animations.AnimationAction.force,backButton.size,Animations.AnimationMove.to,100f, false, 200, 0);
                backButton.tap = false;
            }
            if(retryButton.tap) {
                if(retryButton.hover((int)touchPos.x, (int)touchPos.y)) {
                    level.generateLevel(lv);
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

                    Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Sine, Animations.AnimationAction.force, retryButton.size, Animations.AnimationMove.to,0f, false, 400, 0);
                    Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Sine, Animations.AnimationAction.force, backButton.size, Animations.AnimationMove.to,0f, false, 400, 0);
                    Animations.animate(Animations.AnimationEase.out, Animations.AnimationTiming.Sine, Animations.AnimationAction.force, nextButton.size, Animations.AnimationMove.to,0f, false, 400, 0);
                    level.showTapToStart();
                } else Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Back,Animations.AnimationAction.force,retryButton.size,Animations.AnimationMove.to,100f, false, 200, 0);
                retryButton.tap = false;
            }
            tap = false;
        }
        //end
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

