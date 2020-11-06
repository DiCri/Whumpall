package it.manueldicriscito.whumpall;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.HashMap;
import java.util.Map;

public class Assets {
    public static Map<String, Color> Colors = new HashMap<>();
    public static Map<String, Texture> Textures = new HashMap<>();
    public static Map<String, BitmapFont> Fonts = new HashMap<>();
    public static BitmapFont fontKoho;
    private static BitmapFont fontKoHoBold100;
    private static Music elegyMusic;

    public static Texture loadTexture(String file) {
        try {
            Texture t = new Texture(Gdx.files.internal(file));
            return t;
        } catch(Exception e) {
            return null;
        }
    }
    public static Music loadMusic(String file) {
        try {
            Music m = Gdx.audio.newMusic(Gdx.files.internal(file));
            return m;
        } catch(Exception e) {
            return null;
        }
    }
    public static FreeTypeFontGenerator loadFontGenerator(String file) {
        try {
            FreeTypeFontGenerator g = new FreeTypeFontGenerator(Gdx.files.internal(file));
            return g;
        } catch(Exception e) {
            return null;
        }
    }
    static void loadFont(String name, FreeTypeFontGenerator generator, int size) {
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = size; params.color = Color.WHITE;
        Fonts.put(name, generator.generateFont(params));
    }
    public static void load() {
        Textures.put("player", loadTexture("ball.png"));
        Textures.put("playerShadow", loadTexture("blur_circle.png"));
        Textures.put("bigCircle", loadTexture("big_circle.png"));
        Textures.put("retry", loadTexture("retry.png"));
        Textures.put("save", loadTexture("save.png"));
        Textures.put("load", loadTexture("load.png"));
        Textures.put("play", loadTexture("play.png"));
        Textures.put("pause", loadTexture("pause.png"));
        Textures.put("next", loadTexture("play.png"));
        Textures.put("back", loadTexture("list.png"));
        Textures.put("edit", loadTexture("edit-option.png"));
        Textures.put("delete", loadTexture("delete_option.png"));
        Textures.put("move", loadTexture("move_option.png"));
        Textures.put("final", loadTexture("final-option.png"));
        Textures.put("titleTop", loadTexture("title_top.png"));
        Textures.put("titleBottom", loadTexture("title_bottom.png"));
        Textures.put("titleShadow", loadTexture("title_shadow.png"));
        Textures.put("titleWhite", loadTexture("title_white.png"));
        Textures.put("playerGun", loadTexture("gun.png"));
        Textures.put("wip", loadTexture("wip.png"));
        Textures.put("battery", loadTexture("battery.png"));
        Textures.put("padtype_horizontal", loadTexture("pad_type.png"));
        Textures.put("padtype_vertical", loadTexture("pad_type_vertical.png"));
        Textures.put("plus", loadTexture("plus.png"));
        Textures.put("spike", loadTexture("spike.png"));
        Textures.put("spikeBG", loadTexture("spike_bg.png"));

        //lightBlueColor = new Color(0, 0.678f, 0.710f, 1);
        Colors.put("lightBlue", new Color(0x00adb5ff));
        Colors.put("darkLightBlue", new Color(0x008589ff));
        Colors.put("green", new Color(0x00b300ff));
        Colors.put("fuchsia", new Color(0xb300b3ff));
        Colors.put("darkBlue", new Color(0x2f3e46ff));
        Colors.put("darkerBlue", new Color(0x222831ff));
        Colors.put("shadow", new Color(0xedededff));
        Colors.put("gravityZone", new Color(0x00b32dff));
        Colors.put("playerShadow", new Color(0, 0, 0, 50/255f));

        elegyMusic = loadMusic("Elegy.ogg");

        FreeTypeFontGenerator generator = loadFontGenerator("KoHo/KoHo-Regular.ttf");
        if(generator!=null) {
            FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
            loadFont("KoHoRegular50", generator, 50);
            loadFont("KoHoRegular100", generator, 100);
            generator = new FreeTypeFontGenerator(Gdx.files.internal("KoHo/KoHo-Italic.ttf"));
            loadFont("KoHoItalic50", generator, 50);
            generator = new FreeTypeFontGenerator(Gdx.files.internal("KoHo/KoHo-Bold.ttf"));
            loadFont("KoHoBold40", generator, 40);
            loadFont("KoHoBold50", generator, 50);
            loadFont("KoHoBold100", generator, 100);
            generator = new FreeTypeFontGenerator(Gdx.files.internal("Tibitto/TIBITTO_.TTF"));
            loadFont("Tibitto100", generator, 100);
            loadFont("Tibitto150", generator, 150);
        }

    }


    private static float hue2rgb(float p, float q, float h) {
        if (h < 0) {
            h += 1;
        }

        if (h > 1) {
            h -= 1;
        }

        if (6 * h < 1) {
            return p + ((q - p) * 6 * h);
        }

        if (2 * h < 1) {
            return q;
        }

        if (3 * h < 2) {
            return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
        }

        return p;
    }
    static public Color hslColor(float h, float s, float l) {
        float q, p, r, g, b;

        if (s == 0) {
            r = g = b = l; // achromatic
        } else {
            q = l < 0.5 ? (l * (1 + s)) : (l + s - l * s);
            p = 2 * l - q;
            r = hue2rgb(p, q, h + 1.0f / 3);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1.0f / 3);
        }
        return new Color(r, g, b, 1f);
    }


    public static void dispose() {
        Textures.clear();
        Colors.clear();
        Fonts.clear();
    }
}
