package it.manueldicriscito.whumpall.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

import it.manueldicriscito.whumpall.Animations;
import it.manueldicriscito.whumpall.Assets;
import it.manueldicriscito.whumpall.Button;
import it.manueldicriscito.whumpall.CircleButton;
import it.manueldicriscito.whumpall.Data.LevelData;
import it.manueldicriscito.whumpall.Image;
import it.manueldicriscito.whumpall.Whumpall;

import static it.manueldicriscito.whumpall.Whumpall.getScreenBottom;
import static it.manueldicriscito.whumpall.Whumpall.getScreenLeft;
import static it.manueldicriscito.whumpall.Whumpall.getScreenTop;

public class MainScreen implements Screen {

    private Whumpall game;

    private Vector3 touchPos;
    private boolean tap = false;

    private AroundPoint playButton_player;
    private AroundPoint createButton_player;
    private class AroundPoint {
        public Vector2 point;
        public float radius;
        float angle;
        public AroundPoint() {
            point = new Vector2();
        }
        public float getX() {
            return (float)(radius*Math.cos(angle)+point.x);
        }
        public float getY() {
            return (float)(radius*Math.sin(angle)+point.y);
        }
    }

    private CircleButton playButton;
    private CircleButton createButton;

    private Image bigCircle;

    public MainScreen(Whumpall game) {
        this.game = game;
        bgHue = 0f;
        touchPos = new Vector3();

        playButton = new CircleButton();
        playButton.texture = Assets.Textures.get("play");
        playButton.size.set(200);
        playButton.color.set(Assets.Colors.get("darkBlue"));
        playButton.defaultColor.set(Assets.Colors.get("darkBlue"));
        playButton.tapColor.set(Assets.Colors.get("darkerBlue"));
        playButton.shadowColor.set(Color.WHITE);
        playButton.textureSize = 180;
        playButton.pos = new Vector2(1080/2f, 1050); 
        playButton_player = new AroundPoint();
        playButton_player.angle = 0f;
        playButton_player.point = playButton.pos;
        playButton_player.radius = 185;
        playButton.dSize = 200;
        playButton.hSize = 210;

        createButton = new CircleButton();
        createButton.texture = Assets.Textures.get("wip");
        createButton.size.set(130);
        createButton.color.set(Assets.Colors.get("darkBlue"));
        createButton.defaultColor.set(Assets.Colors.get("darkBlue"));
        createButton.tapColor.set(Assets.Colors.get("darkerBlue"));
        createButton.shadowColor.set(Color.WHITE);
        createButton.textureSize = 108;
        createButton.pos = new Vector2(1080/2f, 650);
        createButton_player = new AroundPoint();
        createButton_player.angle = 0f;
        createButton_player.point = createButton.pos;
        createButton_player.radius = 185;
        createButton.dSize = 130;
        createButton.hSize = 140;

        /*
        Assets.elegyMusic.setLooping(true);
        Assets.elegyMusic.play();
         */

        bigCircle = new Image();
        bigCircle.setTexture(Assets.Textures.get("bigCircle"));
        bigCircle.setColor(Assets.Colors.get("darkBlue"));
        bigCircle.makeAnimatable();

    }
    float bgHue;
    @Override
    public void render(float delta) {
        update(delta);

        game.sr.begin(ShapeRenderer.ShapeType.Filled);
        Color mainColor = Assets.Colors.get("lightBlue");
        Color darkColor = Assets.Colors.get("darkLightBlue");

        bgHue += 0.0003f;
        if(bgHue>=1f) bgHue = 0f;
        game.sr.setColor(Assets.Colors.get("lightBlue"));
        game.sr.rect(getScreenLeft(game.cam), getScreenBottom(game.cam), 1080, getScreenTop(game.cam)-getScreenBottom(game.cam));
        game.sr.rect(getScreenLeft(game.cam), getScreenBottom(game.cam), 1080, getScreenTop(game.cam)-getScreenBottom(game.cam)/3f, Assets.Colors.get("darkLightBlue"),Assets.Colors.get("darkLightBlue"), Assets.Colors.get("lightBlue"), Assets.Colors.get("lightBlue"));
        playButton.drawCircle(game.sr);
        createButton.drawCircle(game.sr);
        game.sr.end();

        game.batch.begin();
        game.batch.setColor(1f, 1f, 1f, 1f);
        game.batch.draw(Assets.Textures.get("titleShadow"), getScreenLeft(game.cam), 1920-300, 1080, 166);
        game.batch.setColor(1f, 1f, 1f, 1f);
        game.batch.draw(Assets.Textures.get("titleWhite"), getScreenLeft(game.cam), 1920-304, 1080, 166);
        game.batch.setColor(57 / 255f, 62 / 255f, 70 / 255f, 1f);
        game.batch.draw(Assets.Textures.get("titleBottom"), getScreenLeft(game.cam), 1920-300, 1080, 166);
        game.batch.setColor(34 / 255f, 40 / 255f, 49 / 255f, 1f);
        game.batch.draw(Assets.Textures.get("titleTop"), getScreenLeft(game.cam), 1920-300, 1080, 166);

        playButton.drawTexture(game.batch);
        createButton.drawTexture(game.batch);

        game.batch.setColor(Color.WHITE);
        game.batch.draw(Assets.Textures.get("player"), playButton_player.getX()-15, playButton_player.getY()-15, 30, 30);
        game.batch.draw(Assets.Textures.get("player"), createButton_player.getX()-15, createButton_player.getY()-15, 30, 30);
        game.batch.end();

        bigCircle.arect.height = bigCircle.arect.width;
        bigCircle.render(game.batch);
        if(bigCircle.arect.getWidth()>1920) {

        }
    }

    public void update(float delta) {
        game.cam.update();
        Animations.run();

        if(playButton.tap) {
            playButton_player.angle-=0.06f;
        } else playButton_player.angle-=0.04f;
        if(createButton.tap) {
            createButton_player.angle+=0.05f;
        } else createButton_player.angle+=0.03f;


        playButton_player.radius = playButton.size.get()-15;
        createButton_player.radius = createButton.size.get()-15;

        game.cam.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        playButton.update(touchPos);
        if(playButton.justClicked()) {
            game.setScreen(new LevelListScreen(game));
        }
        createButton.update(touchPos);
        if(createButton.justClicked()) {
            bigCircle.setRect(new Rectangle(createButton.pos.x, createButton.pos.y, 0, 0));
            Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().width,Animations.AnimationMove.to, 4000, false, 1800, 0);
            Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().x,Animations.AnimationMove.by,-2000, false, 1800, 0);
            Animations.animate(Animations.AnimationEase.out,Animations.AnimationTiming.Expo,Animations.AnimationAction.force,bigCircle.getAnimatableRect().y,Animations.AnimationMove.by,-2000, false, 1800, 0);

            game.setScreen(new CreateScreen(game, new LevelData()));
        }
        if (Gdx.input.justTouched()) {
            tap = true;
        } else if(Gdx.input.isTouched()) {

        } else if(tap) {
            tap = false;
        }
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
    public void dispose() {

    }

    @Override
    public void show() {

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
}
