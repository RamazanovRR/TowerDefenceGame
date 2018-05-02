package com.td.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuScreen implements Screen {
    private SpriteBatch batch;
    private Stage stage;
    private Skin skin;
    private Music music;
    private Sound sound;
    private BitmapFont font32;
    private BitmapFont font96;

    public MenuScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void show() {
        font32 = Assets.getInstance().getAssetManager().get("zorque32.ttf", BitmapFont.class);
        font96 = Assets.getInstance().getAssetManager().get("zorque96.ttf", BitmapFont.class);
//        music = Gdx.audio.newMusic(Gdx.files.internal("Jumping bat.wav"));
//        music.setLooping(true);
//        music.play();
        // sound = Gdx.audio.newSound(Gdx.files.internal("fire.wav"));
        // sound.play();
        createGUI();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0.4f, 0.4f, 1.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        font96.draw(batch, "Java Tower Defense", 0, 600, 1280, 1, false);
        batch.end();
        stage.draw();
    }

    public void update(float dt) {
        stage.act(dt);
    }

    public void createGUI() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        skin.add("font32", font32);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("simpleButton");
        textButtonStyle.font = font32;
        skin.add("simpleSkin", textButtonStyle);

        Button btnNewGame = new TextButton("Start New Game", skin, "simpleSkin");
        Button btnLoadGame = new TextButton("Load Game", skin, "simpleSkin");
        Button btnExitGame = new TextButton("Exit Game", skin, "simpleSkin");
        btnNewGame.setPosition(640 - 160, 300);
        btnLoadGame.setPosition(640 - 160, 180);
        btnExitGame.setPosition(640 - 160, 60);
        stage.addActor(btnNewGame);
        stage.addActor(btnLoadGame);
        stage.addActor(btnExitGame);
        btnNewGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });
        btnExitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        btnLoadGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.LOADGAME);
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().resize(width, height);
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
