package it.manueldicriscito.whumpall;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.manueldicriscito.whumpall.Data.LevelData;
import it.manueldicriscito.whumpall.Screens.CreateScreen;
import it.manueldicriscito.whumpall.Screens.LevelListScreen;
import it.manueldicriscito.whumpall.Screens.PlayScreen;

public class Whumpall extends Game {

	public static final int LEVELSTATE_LOCKED = 0;
	public static final int LEVELSTATE_UNLOCKED = 1;
	public static final int LEVELSTATE_COMPLETED = 2;

	public static final int V_WIDTH = 1080;
	public static final int V_HEIGHT = 1920;

	public static final int PAD_DIR_NONE = 0;
	public static final int PAD_DIR_RIGHT = 1;
	public static final int PAD_DIR_LEFT = 2;
	public static final int PAD_DIR_FINISH = 3;

	public static final int PAD_TYPE_HORIZONTAL = 0;
	public static final int PAD_TYPE_VERTICAL = 1;

	public static DiCriTimer timer;
	private Game game;


	public SpriteBatch batch;
	public OrthographicCamera cam;
	public Viewport port;
	public ShapeRenderer sr;
	public Preferences prefs;
	private static Json json;

	public static Map<String, Object> globalVars = new HashMap<String, Object>();
	public static List<Vector2> playerLine = new ArrayList<>();

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
		prefs = Gdx.app.getPreferences("levels");
		json = new Json();
		json.setUsePrototypes(false);

		timer = new DiCriTimer();

		Assets.load();

		setScreen(new LevelListScreen(this));

	}

	public static JsonValue readData() {
		FileHandle file = Gdx.files.local("data.dat");
		JsonValue root = null;
		try {
			InputStreamReader reader = new InputStreamReader(file.read());
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = bufferedReader.readLine();
			if(line!=null) {
				root = new JsonReader().parse(line);
			}
			bufferedReader.close();
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return root;
	}
	public static void saveData(JsonValue root) {
		FileHandle file = Gdx.files.local("data.dat");
		try {
			OutputStreamWriter writer = new OutputStreamWriter(file.write(false));
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(root.asString());
			bufferedWriter.newLine();
			bufferedWriter.close();
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteLevels() {
		FileHandle file = Gdx.files.local("levels.dat");
		if(!file.exists()) {
			restoreLevels();
		}
		OutputStreamWriter writer = new OutputStreamWriter(file.write(false));
		try { writer.close(); } catch(Exception e) { e.printStackTrace(); }
	}
	public static List<LevelData> getLevels() {
		FileHandle file = Gdx.files.local("levels.dat");
		if(!file.exists()) restoreLevels();
		List<LevelData> list = new ArrayList<>();
		try {
			InputStreamReader reader = new InputStreamReader(file.read());
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line;
			do {
				line = bufferedReader.readLine();
				if(line!=null) {
					JsonValue root = new JsonReader().parse(line);
					list.add(new LevelData(root));
				}
			} while(line!=null);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public static void restoreLevels() {
		FileHandle from = Gdx.files.internal("levels.dat");
		from.copyTo(Gdx.files.local("levels.dat"));
	}
	public static LevelData loadLevel(String title) {
		LevelData levelData = null;
		FileHandle file = Gdx.files.local("levels.dat");
		if(!file.exists()) restoreLevels();

		boolean found = false;
		try {
			InputStreamReader reader = new InputStreamReader(file.read());
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line = "";
			do {
				line = bufferedReader.readLine();
				if(line!=null) {
					JsonValue root = new JsonReader().parse(line);
					levelData = new LevelData(root);
					found = levelData.name.equals(title);
					if(!found) levelData = null;
				}
			} while(!found && line!=null);
			reader.close();
			if(!found) levelData = new LevelData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return levelData;
	}
	public static void saveLevels(List<LevelData> list) {
		for(LevelData ld : list) {
			saveLevel(ld);
		}
	}
	public static void saveLevel(LevelData levelData) {
		saveLevel(levelData.name, levelData);
	}
	public static void saveLevel(String title, LevelData levelData) {
		levelData.name = title;
		FileHandle file = Gdx.files.local("levels.dat");
		if(!file.exists()) restoreLevels();

		List<LevelData> levels = getLevels();
		boolean existing = false;
		for(LevelData ld : levels) {
			if(ld.name.equals(levelData.name)) {
				existing = true;
				try {
					OutputStreamWriter writer = new OutputStreamWriter(file.write(false));
					BufferedWriter bufferedWriter = new BufferedWriter(writer);
					levels.set(levels.indexOf(ld), levelData);
					for(LevelData ldd : levels) {
						bufferedWriter.write(json.toJson(ldd));
						bufferedWriter.newLine();
					}
					bufferedWriter.close();
					writer.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
		if(!existing) {
			try {
				OutputStreamWriter writer = new OutputStreamWriter(file.write(true));
				BufferedWriter bufferedWriter = new BufferedWriter(writer);
				bufferedWriter.write(json.toJson(levelData));
				bufferedWriter.newLine();
				bufferedWriter.close();
				writer.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void render() {super.render();}

	@Override
	public void dispose() {
		batch.dispose();
		Assets.dispose();
	}
}
