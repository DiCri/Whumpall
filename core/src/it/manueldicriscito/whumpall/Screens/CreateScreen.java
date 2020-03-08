package it.manueldicriscito.whumpall.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

import static it.manueldicriscito.whumpall.Whumpall.PAD_DIR_FINISH;
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

    public static final int EDITOR_BUILD = 0;
    public static final int EDITOR_DELETE = 1;
    public static final int EDITOR_MOVE = 2;
    public static final int EDITOR_FINAL = 3;
    int editorMode = -1;


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

    private Vector3 touchPos = new Vector3();

    Map<String, CircleButton> cbtn = new HashMap<>();

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

        CircleButton cb = new CircleButton();
        cb.pos.set(getScreenRight(game.cam)-100, getScreenTop(game.cam)-100);
        cb.size.set(70);
        cb.dSize = 70;
        cb.hSize = 80;
        cb.color.set(Assets.darkBlueColor);
        cb.shadowColor.set(Color.WHITE);
        cb.defaultColor.set(Assets.darkBlueColor);
        cb.tapColor.set(Assets.darkerBlueColor);
        cb.texture = Assets.playButtonTexture;
        cb.textureSize = 70;
        cbtn.put("play", new CircleButton(cb));
        cb.holdDown = true;
        cb.pos.set(getScreenLeft(game.cam)+100, getScreenBottom(game.cam)+100);
        cb.texture = Assets.editButtonTexture;
        cbtn.put("edit", new CircleButton(cb));
        cb.pos.set(getScreenLeft(game.cam)+250, getScreenBottom(game.cam)+100);
        cb.texture = Assets.deleteButtonTexture;
        cbtn.put("delete", new CircleButton(cb));
        cb.pos.set(getScreenLeft(game.cam)+400, getScreenBottom(game.cam)+100);
        cb.texture = Assets.moveButtonTexture;
        cbtn.put("move", new CircleButton(cb));
        cb.pos.set(getScreenLeft(game.cam)+550, getScreenBottom(game.cam)+100);
        cb.texture = Assets.finalButtonTexture;
        cbtn.put("final", new CircleButton(cb));


        Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().width,Animations.AnimationMove.to,0, false, 1500, 0);
        Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force, bigCircle.getAnimatableRect().x,Animations.AnimationMove.to,this.level.player.pos.x, false, 1500, 0);
        Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().y,Animations.AnimationMove.to,this.level.player.pos.y, false, 1500, 0);


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
        for(Map.Entry<String, CircleButton> entry : cbtn.entrySet()) {
            entry.getValue().drawCircle(game.sr);
        }
        game.sr.end();
        game.batch.begin();
        for(Map.Entry<String, CircleButton> entry : cbtn.entrySet()) {
            entry.getValue().drawTexture(game.batch);
        }
        game.batch.end();


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
        game.cam.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        level.update(delta, game.cam);
        Animations.run();
        boolean btnTouch = false;

        cbtn.get("play").pos.set(getScreenRight(game.cam)-100, getScreenTop(game.cam)-100);
        cbtn.get("edit").pos.set(getScreenLeft(game.cam)+100, getScreenBottom(game.cam)+100);
        cbtn.get("delete").pos.set(getScreenLeft(game.cam)+250, getScreenBottom(game.cam)+100);
        cbtn.get("move").pos.set(getScreenLeft(game.cam)+400, getScreenBottom(game.cam)+100);
        cbtn.get("final").pos.set(getScreenLeft(game.cam)+550, getScreenBottom(game.cam)+100);
        for(Map.Entry<String, CircleButton> entry : cbtn.entrySet()) {
            entry.getValue().update(touchPos);
            if(entry.getValue().justClicked()) {
                if(entry.getValue().isSelected()) {
                    switch (entry.getKey()) {
                        case "edit":
                            editorMode = EDITOR_BUILD;
                            break;
                        case "move":
                            editorMode = EDITOR_MOVE;
                            break;
                        case "delete":
                            editorMode = EDITOR_DELETE;
                            break;
                        case "final":
                            editorMode = EDITOR_FINAL;
                            break;
                    }
                    for(Map.Entry<String, CircleButton> antry : cbtn.entrySet()) {
                        if(!antry.getKey().equals(entry.getKey())) antry.getValue().deselect();
                    }
                } else editorMode = -1;
            } else {
            }
            btnTouch |= entry.getValue().justTouched();
        }

        if (Gdx.input.justTouched() && !btnTouch) {
            tap = true;

            if(editorMode==EDITOR_BUILD) {
                Platform p = new Platform();
                p.rect.set(new Rectangle(touchPos.x, touchPos.y-20, 0, 40));
                p.type = PAD_TYPE_HORIZONTAL;
                p.fixed = true;
                p.dir = PAD_DIR_NONE;
                p.add();
                level.lpads.add(p);
                level.addingPad = true;
            } else if(editorMode==EDITOR_DELETE) {
                Iterator<Platform> i = level.lpads.iterator();
                while(i.hasNext()) {
                    Platform p = i.next();
                    if (p.rect.contains(touchPos.x, touchPos.y)) {
                        i.remove();
                    }
                }
            } else if(editorMode==EDITOR_FINAL) {
                Iterator<Platform> i = level.lpads.iterator();
                while(i.hasNext()) {
                    Platform p = i.next();
                    if(p.rect.contains(touchPos.x, touchPos.y)) {
                        for(Platform lp : level.lpads) {
                            lp.dir = PAD_DIR_NONE;
                        }
                        p.dir = PAD_DIR_FINISH;
                    }
                }
            }
        } else if (Gdx.input.isTouched()) {
            if(editorMode==EDITOR_BUILD) {
                if (level.addingPad && level.lpads.size() > 0) {
                    Platform p = level.lpads.get(level.lpads.size() - 1);
                    p.rect.setWidth(touchPos.x - p.rect.x);
                }
            }
        } else if (level.addingPad) {
            level.addingPad = false;
            Platform p = level.lpads.get(level.lpads.size()-1);
            p.fix();
        } else if(tap) {
            // Mouse Leave
            tap = false;
        }
        //end

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
        if(editorMode==EDITOR_MOVE) {
            float x = Gdx.input.getDeltaX();
            float y = Gdx.input.getDeltaY();

            game.cam.translate(-x,y);
        }
        return true;
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

