package it.manueldicriscito.whumpall;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Assets {
    public static Color lightBlueColor;
    public static Color darkLightBlueColor;
    public static Color darkBlueColor;
    public static Color darkerBlueColor;
    public static Color shadowColor;
    public static Color greenColor;
    public static Color fuchsiaColor;
    public static Color gravityZoneColor;
    public static Color playerShadowColor;
    public static Texture playerTexture;
    public static Texture playerShadowTexture;
    public static Texture playerGunTexture;
    public static Texture bigCircleTexture;
    public static Texture retryButtonTexture;
    public static Texture nextButtonTexture;
    public static Texture playButtonTexture;
    public static Texture backButtonTexture;
    public static Texture editButtonTexture;
    public static Texture deleteButtonTexture;
    public static Texture moveButtonTexture;
    public static Texture finalButtonTexture;
    public static Texture wipTexture;
    public static Texture titleTopTexture;
    public static Texture titleBottomTexture;
    public static Texture titleShadowTexture;
    public static Texture titleWhiteTexture;
    public static Texture batteryTexture;
    public static BitmapFont fontKoHoRegular100;
    public static BitmapFont fontKoHoItalic50;
    public static BitmapFont fontTibitto50;
    public static BitmapFont fontKoho;
    static BitmapFont fontKoHoBold50;
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
    public static void load() {
        playerTexture = loadTexture("ball.png");
        playerShadowTexture = loadTexture("blur_circle.png");
        bigCircleTexture = loadTexture("big_circle.png");
        retryButtonTexture = loadTexture("retry.png");
        playButtonTexture = loadTexture("play.png");
        nextButtonTexture = loadTexture("play.png");
        backButtonTexture = loadTexture("list.png");
        editButtonTexture = loadTexture("edit-option.png");
        deleteButtonTexture = loadTexture("delete_option.png");
        moveButtonTexture = loadTexture("move_option.png");
        finalButtonTexture = loadTexture("final-option.png");
        titleTopTexture = loadTexture("title_top.png");
        titleBottomTexture = loadTexture("title_bottom.png");
        titleShadowTexture = loadTexture("title_shadow.png");
        titleWhiteTexture = loadTexture("title_white.png");
        playerGunTexture = loadTexture("gun.png");
        wipTexture = loadTexture("wip.png");
        batteryTexture = loadTexture("battery.png");
        //lightBlueColor = new Color(0, 0.678f, 0.710f, 1);
        lightBlueColor = new Color(0x00adb5ff);
        darkLightBlueColor = new Color(0x008589ff);
        greenColor = new Color(0x00b300ff);
        fuchsiaColor = new Color(0xb300b3ff);
        darkBlueColor = new Color(0x2f3e46ff);
        darkerBlueColor = new Color(0x222831ff);
        shadowColor = new Color(0xedededff);
        gravityZoneColor = new Color(0x00b32dff);
        playerShadowColor = new Color(0, 0, 0, 50/255f);
        elegyMusic = loadMusic("Elegy.ogg");

        FreeTypeFontGenerator generator = loadFontGenerator("KoHo/KoHo-Regular.ttf");
        if(generator!=null) {
            FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
            params.size = 100; params.color = Color.WHITE;
            fontKoHoRegular100 = generator.generateFont(params);
            generator = new FreeTypeFontGenerator(Gdx.files.internal("KoHo/KoHo-Italic.ttf"));
            params.size = 50; params.color = Color.WHITE;
            fontKoHoItalic50 = generator.generateFont(params);
            generator = new FreeTypeFontGenerator(Gdx.files.internal("KoHo/KoHo-Bold.ttf"));
            params.size = 50; params.color = Color.WHITE;
            fontKoHoBold50 = generator.generateFont(params);
            params.size = 100; params.color = Color.WHITE;
            fontKoHoBold100 = generator.generateFont(params);
            generator = new FreeTypeFontGenerator(Gdx.files.internal("Tibitto/TIBITTO_.TTF"));
            params.size = 100; params.color = Color.WHITE;
            fontTibitto50 = generator.generateFont(params);
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
        playerTexture.dispose();
        playerShadowTexture.dispose();
        bigCircleTexture.dispose();
        fontKoHoRegular100.dispose();
        fontKoHoItalic50.dispose();
        fontKoHoBold50.dispose();
    }
}
