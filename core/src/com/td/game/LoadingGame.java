package com.td.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.io.*;
import java.util.ArrayList;

public class LoadingGame {

    private TurretEmitter turretEmitter;
    private MonsterEmitter monsterEmitter;
    private PlayerInfo playerInfo;

    private ArrayList<Object> arrObj;

    public TurretEmitter getTurretEmitter() {
        return turretEmitter;
    }

    public MonsterEmitter getMonsterEmitter() {
        return monsterEmitter;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public LoadingGame() {
        arrObj = new ArrayList<>();
    }

    public void saveGame(TurretEmitter turretEmitter, MonsterEmitter monsterEmitter, PlayerInfo playerInfo) {
        arrObj.add(turretEmitter);
        arrObj.add(monsterEmitter);
        arrObj.add(playerInfo);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Save.sav"));
            oos.writeObject(arrObj);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        arrObj.clear();
    }

    public void loadingGame(Map map, TextureAtlas atlas, GameScreen gameScreen) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Save.sav"));
            arrObj = (ArrayList<Object>) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        turretEmitter = (TurretEmitter) arrObj.get(0);
        turretEmitter.setAtlas(atlas);
        turretEmitter.setGameScreen(gameScreen);
        turretEmitter.setMap(map);
        turretEmitter.loadTurretsSaveFile();
        monsterEmitter = (MonsterEmitter) arrObj.get(1);
        monsterEmitter.setMap(map);
        monsterEmitter.loadMonsterSaveFile(map, atlas.findRegion("monster"),
                                                atlas.findRegion("monsterBackHP"),
                                                atlas.findRegion("monsterHp"));
        playerInfo = (PlayerInfo) arrObj.get(2);
        arrObj.clear();

    }
}
