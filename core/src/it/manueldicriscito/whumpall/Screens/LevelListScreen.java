package it.manueldicriscito.whumpall.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.manueldicriscito.whumpall.Animations;
import it.manueldicriscito.whumpall.Assets;
import it.manueldicriscito.whumpall.CircleButton;
import it.manueldicriscito.whumpall.Data.LevelData;
import it.manueldicriscito.whumpall.DiCriTimer;
import it.manueldicriscito.whumpall.Image;
import it.manueldicriscito.whumpall.Whumpall;

import static it.manueldicriscito.whumpall.Whumpall.LEVELSTATE_COMPLETED;
import static it.manueldicriscito.whumpall.Whumpall.LEVELSTATE_LOCKED;
import static it.manueldicriscito.whumpall.Whumpall.LEVELSTATE_UNLOCKED;
import static it.manueldicriscito.whumpall.Whumpall.getLevels;
import static it.manueldicriscito.whumpall.Whumpall.getScreenLeft;
import static it.manueldicriscito.whumpall.Whumpall.getScreenTop;

public class LevelListScreen implements Screen {
    private final Whumpall game;
    private final List<CircleButton> levelButtons;
    private final Vector3 touchPos;
    private final Image bigCircle;
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private final DiCriTimer timer = new DiCriTimer();
    int level;
    List<LevelData> list;
    Animations.AnimatableFloat titleOffset = new Animations.AnimatableFloat(0f);

    public LevelListScreen(Whumpall game) {
        super();
        this.game = game;
        touchPos = new Vector3();
        levelButtons = new ArrayList<>();
        level = 0;

        list = getLevels();
        game.cam.position.set(540, 960, 0);
        for(int i=0; i<list.size(); i++) {
            if(!game.prefs.contains("lv"+i)) {
                if(i==0) {
                    game.prefs.putInteger("lv"+i, LEVELSTATE_UNLOCKED);
                } else {
                    if(game.prefs.getInteger("lv"+(i-1))==LEVELSTATE_COMPLETED) {
                        game.prefs.putInteger("lv"+i, LEVELSTATE_UNLOCKED);
                    } else {
                        game.prefs.putInteger("lv"+i, LEVELSTATE_LOCKED);
                    }
                }
            }
            CircleButton cb = new CircleButton();
            int levelState = game.prefs.getInteger("lv"+i);
            switch(levelState) {
                case LEVELSTATE_COMPLETED:
                    cb.color.set(Color.WHITE);
                    cb.defaultColor.set(Color.WHITE);
                    cb.shadowColor.set(Assets.Colors.get("darkerBlue"));
                    cb.tapColor.set(Color.WHITE);
                    break;
                case LEVELSTATE_UNLOCKED:
                    cb.color.set(Assets.Colors.get("darkerBlue"));
                    cb.defaultColor.set(Assets.Colors.get("darkerBlue"));
                    cb.tapColor.set(Assets.Colors.get("darkerBlue"));
                    cb.shadowColor.set(Color.WHITE);
                    break;
                case LEVELSTATE_LOCKED:
                    cb.color.set(Assets.Colors.get("darkerBlue"));
                    cb.defaultColor.set(Assets.Colors.get("darkerBlue"));
                    cb.tapColor.set(Assets.Colors.get("darkerBlue"));
                    cb.shadowColor.set(Assets.Colors.get("darkerBlue"));
                    break;
            }

            cb.size.set(0);
            cb.dSize = 80;
            cb.hSize = 90;
            cb.pos.set(getScreenLeft(game.cam)+130+200*(i%5f), getScreenTop(game.cam)-(600+((float)Math.floor(i/5f)*200)));
            levelButtons.add(cb);
            Animations.animate(
                    Animations.AnimationEase.out,
                    Animations.AnimationTiming.Expo,
                    Animations.AnimationAction.force,
                    cb.size, Animations.AnimationMove.to,
                    cb.dSize, false, 1200, i*80
            );
            Animations.animate(
                    Animations.AnimationEase.out,
                    Animations.AnimationTiming.Back,
                    Animations.AnimationAction.force,
                    titleOffset, Animations.AnimationMove.to,
                    25, false, 1000, 500
            );
        }
        game.prefs.flush();

        bigCircle = new Image();
        bigCircle.setTexture(Assets.Textures.get("bigCircle"));
        bigCircle.setColor(new Color(1, 1, 1, 1));
        bigCircle.makeAnimatable();

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        game.port.update(width, height);
        game.cam.setToOrtho(false, 1080, height*1080f/width);
        game.cam.position.set(540, 960, 0);
        timer.start();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(Assets.Colors.get("darkBlue").r, Assets.Colors.get("darkBlue").g, Assets.Colors.get("darkBlue").b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        for(CircleButton cb : levelButtons) {
            cb.drawCircle(game.sr);
        }
        game.sr.end();
        game.batch.begin();

        for(CircleButton cb : levelButtons) {
            float alpha = (timer.get()-(150+levelButtons.indexOf(cb)*80))/200f;
            if(alpha>1f) alpha = 1f;
            if(cb.size.get()>0f) Assets.Fonts.get("Tibitto100").getData().setScale(cb.size.get()*100/cb.dSize/100);
            Color darkerBlue = Assets.Colors.get("darkerBlue");
            Assets.Fonts.get("Tibitto100").setColor(darkerBlue.r, darkerBlue.g, darkerBlue.b, alpha);
            int levelState = game.prefs.getInteger("lv"+levelButtons.indexOf(cb));
            if(levelState==LEVELSTATE_UNLOCKED) {
                Assets.Fonts.get("Tibitto100").setColor(1, 1, 1, alpha);
            }

            levelButtons.indexOf(cb);
            //String text = Integer.toString(levelButtons.indexOf(cb)+1);
            String text = list.get(levelButtons.indexOf(cb)).name;
            glyphLayout.setText(Assets.Fonts.get("Tibitto100"), text);
            Assets.Fonts.get("Tibitto100").draw(game.batch, text, cb.pos.x-glyphLayout.width/2, cb.pos.y+glyphLayout.height/2);

        }
        glyphLayout.setText(Assets.Fonts.get("Tibitto150"), "World 1");
        float alpha = (timer.get()-500)/200f;
        alpha = Math.min(alpha, 1f);
        Color darkerBlue = Assets.Colors.get("darkerBlue");
        Assets.Fonts.get("Tibitto150").setColor(darkerBlue.r, darkerBlue.g, darkerBlue.b, alpha);
        Assets.Fonts.get("Tibitto150").draw(game.batch, "World 1", 1080f/2-glyphLayout.width/2, getScreenTop(game.cam)-200-25+titleOffset.get());
        Assets.Fonts.get("Tibitto150").setColor(1, 1, 1, alpha);
        Assets.Fonts.get("Tibitto150").draw(game.batch, "World 1", 1080f/2-glyphLayout.width/2, getScreenTop(game.cam)-190-25+titleOffset.get());
        game.batch.end();

        bigCircle.arect.height = bigCircle.arect.width;
        bigCircle.render(game.batch);
        if(bigCircle.arect.height.get()>=5000 && level!=0) {
            game.batch.setColor(Color.WHITE);
            game.setScreen(new PlayScreen(game, level));
        }


    }
    public void update(float delta) {
        game.cam.update();
        game.sr.setProjectionMatrix(game.cam.combined);
        game.batch.setProjectionMatrix(game.cam.combined);
        game.cam.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        for(CircleButton cb : levelButtons) {
            cb.update(touchPos);
            int levelState = game.prefs.getInteger("lv"+levelButtons.indexOf(cb));
            if (cb.justClicked() && levelState!=LEVELSTATE_LOCKED) {
                bigCircle.setRect(new Rectangle(cb.pos.x, cb.pos.y, 0, 0));
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().width, Animations.AnimationMove.to, 5000, false, 2500, 0);
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().x,Animations.AnimationMove.by,-2500, false, 2500, 0);
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().y,Animations.AnimationMove.by,-2500, false, 2500, 0);
                level = levelButtons.indexOf(cb)+1;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            game.setScreen(new CreateScreen(game));
        }
        Animations.run();
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
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
