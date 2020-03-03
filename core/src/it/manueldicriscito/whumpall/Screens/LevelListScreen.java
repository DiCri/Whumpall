package it.manueldicriscito.whumpall.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

import it.manueldicriscito.whumpall.Animations;
import it.manueldicriscito.whumpall.Assets;
import it.manueldicriscito.whumpall.CircleButton;
import it.manueldicriscito.whumpall.Whumpall;

import static it.manueldicriscito.whumpall.Whumpall.getScreenLeft;
import static it.manueldicriscito.whumpall.Whumpall.getScreenTop;

public class LevelListScreen implements Screen {
    private Whumpall game;
    private List<CircleButton> levelButtons;
    private Vector3 touchPos;

    public LevelListScreen(Whumpall game) {
        super();
        this.game = game;
        touchPos = new Vector3();
        levelButtons = new ArrayList<>();
        int levels = 50;

        game.cam.position.set(540, 960, 0);
        for(int i=0; i<levels; i++) {
            CircleButton cb = new CircleButton();
            cb.color.set(Color.WHITE);
            cb.shadowColor.set(Assets.darkerBlueColor);
            cb.size.set(75);
            cb.dSize = 75;
            cb.hSize = 85;
            cb.pos.set(getScreenLeft(game.cam)+130+200*(i%5f), getScreenTop(game.cam)-(200+((float)Math.floor(i/5f)*200)));

            levelButtons.add(cb);
        }
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
        Assets.fontKoHoItalic50.setColor(0, 0, 0, 1);
        for(CircleButton cb : levelButtons) {
            Assets.fontKoHoItalic50.draw(game.batch, Integer.toString(levelButtons.indexOf(cb)+1), cb.pos.x-20, cb.pos.y+20);
        }
        game.batch.end();
    }
    public void update(float delta) {
        game.cam.update();
        game.sr.setProjectionMatrix(game.cam.combined);
        game.batch.setProjectionMatrix(game.cam.combined);
        game.cam.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        for(CircleButton cb : levelButtons) {
            cb.update(touchPos);
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
