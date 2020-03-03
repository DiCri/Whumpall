package it.manueldicriscito.whumpall;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import it.manueldicriscito.whumpall.Screens.LevelListScreen;
import it.manueldicriscito.whumpall.Screens.PlayScreen;

public class Whumpall extends Game {
	public static final int V_WIDTH = 1080;
	public static final int V_HEIGHT = 1920;

	public static final int PAD_DIR_NONE = 0;
	public static final int PAD_DIR_RIGHT = 1;
	public static final int PAD_DIR_LEFT = 2;
	static final int PAD_DIR_FINISH = 3;

	public static final int PAD_TYPE_HORIZONTAL = 0;
	public static final int PAD_TYPE_VERTICAL = 1;

	public static DiCriTimer timer;
	private Game game;


	public SpriteBatch batch;
	public OrthographicCamera cam;
	public Viewport port;
	public ShapeRenderer sr;

	public static Map<String, Object> globalVars = new HashMap<String, Object>();

	static float getAngle(float x1, float y1, float x2, float y2) {
		float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
		if(angle < 0){
			angle += 360;
		}
		return angle;
	}

	public static float getScreenBottom(OrthographicCamera cam) {
		return cam.position.y-(Gdx.graphics.getHeight()*1080f/Gdx.graphics.getWidth())/2f;
	}
	public static float getScreenTop(OrthographicCamera cam) {
		return cam.position.y+(Gdx.graphics.getHeight()*1080f/Gdx.graphics.getWidth())/2f;
	}
	public static float getScreenLeft(OrthographicCamera cam) {
		return cam.position.x-540;
	}
	public static float getScreenRight(OrthographicCamera cam) {
		return cam.position.x+540;
	}

	public static Vector2 calculateIntersectionPoint(Vector2 s1, Vector2 e1, Vector2 s2, Vector2 e2) {
		float a1 = e1.y-s1.y;
		float b1 = s1.x-e1.x;
		float c1 = a1*(s1.x)+b1*(s1.y);

		float a2 = e2.y-s2.y;
		float b2 = s2.x-e2.x;
		float c2 = a2*(s2.x)+b2*(s2.y);

		float determinant = a1*b2 - a2*b1;
		if(determinant==0) {
			return null;
		} else {
			float x = (b2*c1-b1*c2)/determinant;
			float y = (a1*c2-a2*c1)/determinant;
			return new Vector2(x, y);
		}

	}


	@Override
	public void create() {
		game = this;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		batch = new SpriteBatch();
		sr = new ShapeRenderer();

		cam = new OrthographicCamera(1080, 1920);
		port = new ExtendViewport(1080, 1920, cam);
		port.apply(true);

		timer = new DiCriTimer();

		Assets.load();

		//setScreen(new PlayScreen(this));
		setScreen(new LevelListScreen(this));
	}

	@Override
	public void render() {super.render();}

	@Override
	public void dispose() {
		batch.dispose();
		//Assets.dispose();
	}

}
