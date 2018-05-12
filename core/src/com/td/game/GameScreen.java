package com.td.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.td.game.gui.UpperPanel;

import java.io.*;
import java.util.ArrayList;

public class GameScreen implements Screen, Serializable {
    private transient SpriteBatch batch;
    private transient BitmapFont font24;
    private transient Map map;
    private TurretEmitter turretEmitter;
    private MonsterEmitter monsterEmitter;
    private transient ParticleEmitter particleEmitter;
    private transient TextureAtlas atlas;
    private transient TextureRegion selectedCellTexture;
    private transient Stage stage;
    private transient Group groupTurretAction;
    private transient Group groupTurretSelection;
    private transient Group groupSaveAndExit;
    private PlayerInfo playerInfo;
    private transient UpperPanel upperPanel;
    private transient Camera camera;
    private transient LoadingGame loadingGame;

    private transient Vector2 mousePosition;

    private int selectedCellX, selectedCellY;

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public ParticleEmitter getParticleEmitter() {
        return particleEmitter;
    }

    public MonsterEmitter getMonsterEmitter() {
        return monsterEmitter;
    }

    public GameScreen(SpriteBatch batch, Camera camera) {
        this.batch = batch;
        this.camera = camera;
        this.loadingGame = new LoadingGame();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
        atlas = Assets.getInstance().getAtlas();
        selectedCellTexture = atlas.findRegion("cursor");
        map = new Map(atlas);
        font24 = Assets.getInstance().getAssetManager().get("zorque24.ttf", BitmapFont.class);
        if(ScreenManager.getInstance().isLoadGame()) {
            loadingGame.loadingGame(map, atlas, this);
            turretEmitter = loadingGame.getTurretEmitter();
            monsterEmitter = loadingGame.getMonsterEmitter();
            playerInfo = loadingGame.getPlayerInfo();
        }
        if(!ScreenManager.getInstance().isLoadGame()){
            turretEmitter = new TurretEmitter(atlas, this, map);
            monsterEmitter = new MonsterEmitter(atlas, map, 60);
            playerInfo = new PlayerInfo(100, 32);
        }

        particleEmitter = new ParticleEmitter(atlas.findRegion("star16"));
        mousePosition = new Vector2(0, 0);
        createGUI();
    }

    public void createGUI() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);

        InputProcessor myProc = new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                selectedCellX = (int) (mousePosition.x / 80);
                selectedCellY = (int) (mousePosition.y / 80);
                System.out.println(selectedCellX + "  " + selectedCellY);
                if(map.isCellEmpty(selectedCellX, selectedCellY)) {
                    int x = selectedCellX * 80 - 220;
                    int y = selectedCellY * 80 + 100;
                    if(selectedCellX * 80 < 170) {
                        x = selectedCellX * 80 - 160;
                    }
                    if(selectedCellX * 80 > 1350) {
                        x = selectedCellX * 80 - 280;
                    }
                    if(selectedCellY * 80 > 550) {
                        y = selectedCellY * 80 - 100;
                    }
                    if(selectedCellY * 80 < 100) {
                        y = selectedCellY * 80 + 100;
                    }
                    if(turretEmitter.isEmptyTurret(selectedCellX, selectedCellY)) {
                        groupTurretAction.setPosition(x, y);
                        groupTurretAction.setVisible(true);
                    } else {
                        groupTurretAction.setVisible(false);
                        groupTurretSelection.setPosition(x, y);
                        groupTurretSelection.setVisible(true);
                    }
                }
                if(!map.isCellEmpty(selectedCellX,selectedCellY)) {
                    groupTurretSelection.setVisible(false);
                }
                return true;
            }
        };

        InputMultiplexer im = new InputMultiplexer(stage, myProc);
        Gdx.input.setInputProcessor(im);

        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();

        textButtonStyle.up = skin.getDrawable("shortButton");
        textButtonStyle.font = font24;
        skin.add("simpleSkin", textButtonStyle);

        groupTurretAction = new Group();
        groupTurretAction.setVisible(false);
        groupTurretAction.setPosition(50, 600);

        Button btnUpgradeTurret = new TextButton("Upg", skin, "simpleSkin");
        Button btnDestroyTurret = new TextButton("Dst", skin, "simpleSkin");
        btnUpgradeTurret.setPosition(10, 10);
        btnDestroyTurret.setPosition(110, 10);
        groupTurretAction.addActor(btnUpgradeTurret);
        groupTurretAction.addActor(btnDestroyTurret);

        btnDestroyTurret.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playerInfo.addMoney(turretEmitter.destroyTurret(selectedCellX, selectedCellY) / 2);
                groupTurretAction.setVisible(false);
            }
        });


        groupTurretSelection = new Group();
        groupTurretSelection.setVisible(false);
        groupTurretSelection.setPosition(50, 500);
        groupTurretSelection.setSize(190, 80);
        Button btnSetTurret1 = new TextButton("T1", skin, "simpleSkin");
        Button btnSetTurret2 = new TextButton("T2", skin, "simpleSkin");
        btnSetTurret1.setPosition(10, 10);
        btnSetTurret2.setPosition(110, 10);
        groupTurretSelection.addActor(btnSetTurret1);
        groupTurretSelection.addActor(btnSetTurret2);

        btnSetTurret1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setTurret(0);
            }
        });
        btnSetTurret2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setTurret(1);
            }
        });

        stage.addActor(groupTurretSelection);
        stage.addActor(groupTurretAction);

        groupSaveAndExit = new Group();
        groupSaveAndExit.setPosition(1020, 600);
        Button btnSaveGame = new TextButton("Save", skin, "simpleSkin");
        Button btnExitGame = new TextButton("Exit", skin, "simpleSkin");
        btnSaveGame.setPosition(10, 10);
        btnExitGame.setPosition(110, 10);
        groupSaveAndExit.addActor(btnSaveGame);
        groupSaveAndExit.addActor(btnExitGame);

        stage.addActor(groupSaveAndExit);

        btnSaveGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loadingGame.saveGame(turretEmitter,monsterEmitter,playerInfo);
            }
        });

        btnExitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });

        upperPanel = new UpperPanel(playerInfo, stage, 0, 720 - 60);
        skin.dispose();
    }

    public void setTurret(int index) {
        if (playerInfo.isMoneyEnough(turretEmitter.getTurretCost(index))) {
            playerInfo.decreaseMoney(turretEmitter.getTurretCost(index));
            turretEmitter.setTurret(index, selectedCellX, selectedCellY);
        }
        groupTurretSelection.setVisible(false);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.position.set(640 + 160, 360, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        map.render(batch);
        turretEmitter.render(batch);
        monsterEmitter.render(batch, font24);
        particleEmitter.render(batch);
        batch.setColor(1, 1, 0, 0.5f);
        batch.draw(selectedCellTexture, selectedCellX * 80, selectedCellY * 80);
        batch.setColor(1, 1, 1, 1);
        batch.end();
        camera.position.set(640, 360, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        stage.draw();

    }

    public void update(float dt) {
        camera.position.set(640 + 160, 360, 0);
        camera.update();
        ScreenManager.getInstance().getViewport().apply();
        mousePosition.set(Gdx.input.getX(), Gdx.input.getY());
        ScreenManager.getInstance().getViewport().unproject(mousePosition);
        monsterEmitter.update(dt);
        turretEmitter.update(dt);
        particleEmitter.update(dt);
        particleEmitter.checkPool();
        checkMonstersAtHome();
        upperPanel.update();
        stage.act(dt);
    }

    public void checkMonstersAtHome() {
        for (int i = 0; i < monsterEmitter.getMonsters().length; i++) {
            Monster m = monsterEmitter.getMonsters()[i];
            if (m.isActive()) {
                if (map.isHome(m.getCellX(), m.getCellY())) {
                    m.deactivate();
                    playerInfo.decreaseHp(1);
                }
            }
        }
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
