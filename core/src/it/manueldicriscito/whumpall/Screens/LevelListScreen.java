package it.manueldicriscito.whumpall.Screens;

import com.badlogic.gdx.Gdx;
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
import java.util.List;

import it.manueldicriscito.whumpall.Animations;
import it.manueldicriscito.whumpall.Assets;
import it.manueldicriscito.whumpall.CircleButton;
import it.manueldicriscito.whumpall.Image;
import it.manueldicriscito.whumpall.Whumpall;

import static it.manueldicriscito.whumpall.Whumpall.getScreenBottom;
import static it.manueldicriscito.whumpall.Whumpall.getScreenLeft;
import static it.manueldicriscito.whumpall.Whumpall.getScreenTop;

public class LevelListScreen implements Screen {
    private Whumpall game;
    private List<CircleButton> levelButtons;
    private Vector3 touchPos;
    private Image bigCircle;
    private GlyphLayout glyphLayout = new GlyphLayout();
    int level;

    public LevelListScreen(Whumpall game) {
        super();
        this.game = game;
        touchPos = new Vector3();
        levelButtons = new ArrayList<>();
        int levels = 50;
        level = 0;

        game.cam.position.set(540, 960, 0);
        for(int i=0; i<15; i++) {
            CircleButton cb = new CircleButton();
            cb.color.set(Color.WHITE);
            cb.shadowColor.set(Assets.darkerBlueColor);
            cb.size.set(80);
            cb.dSize = 80;
            cb.hSize = 90;
            cb.pos.set(getScreenLeft(game.cam)+130+200*(i%5f), getScreenTop(game.cam)-(200+((float)Math.floor(i/5f)*200)));
            levelButtons.add(cb);
        }
        bigCircle = new Image();
        bigCircle.setTexture(Assets.bigCircleTexture);
        bigCircle.setColor(new Color(1, 1, 1, 1));
        bigCircle.makeAnimatable();

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        game.port.update(width, height);
        game.cam.setToOrtho(false, 1080, height*1080f/width);
        game.cam.position.set(540, 960, 0);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(Assets.darkBlueColor.r, Assets.darkBlueColor.g, Assets.darkBlueColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        for(CircleButton cb : levelButtons) {
            cb.drawCircle(game.sr);
        }
        game.sr.end();
        game.batch.begin();
        BitmapFont levelButtonsFont = Assets.fontTibitto50;
        levelButtonsFont.setColor(Assets.darkerBlueColor);
        for(CircleButton cb : levelButtons) {
            String text = Integer.toString(levelButtons.indexOf(cb)+1);
            glyphLayout.setText(levelButtonsFont, text);
            Assets.fontTibitto50.draw(game.batch, text, cb.pos.x-glyphLayout.width/2, cb.pos.y+glyphLayout.height/2);
        }
        game.batch.end();

        bigCircle.arect.height = bigCircle.arect.width;
        bigCircle.render(game.batch);
        if(bigCircle.arect.height.get()>=5000 && level!=0) {
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
            if (cb.justClicked()) {
                bigCircle.setRect(new Rectangle(cb.pos.x, cb.pos.y, 0, 0));
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().width, Animations.AnimationMove.to, 5000, false, 2500, 0);
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().x,Animations.AnimationMove.by,-2500, false, 2500, 0);
                Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().y,Animations.AnimationMove.by,-2500, false, 2500, 0);
                level = levelButtons.indexOf(cb)+1;
            }
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
