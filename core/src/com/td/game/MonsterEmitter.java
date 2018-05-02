package com.td.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MonsterEmitter implements Serializable {
    public class Wave implements Serializable {
        private float time;
        private int routeIndex;
        private int monstersCount;

        public Wave(float time, int routeIndex, int monstersCount) {
            this.time = time;
            this.routeIndex = routeIndex;
            this.monstersCount = monstersCount;
        }
    }

    private transient Map map;
    private Monster[] monsters;
    private Wave[] waves;
    private float spawnTimer;
    private int currentWave;

    public void setMap(Map map) {
        this.map = map;
    }

    public Monster[] getMonsters() {
        return monsters;
    }

    public MonsterEmitter(TextureAtlas atlas, Map map, int maxSize) {
        this.map = map;
        this.monsters = new Monster[maxSize];
        for (int i = 0; i < monsters.length; i++) {
            this.monsters[i] = new Monster(atlas, map, 0);
        }
        loadScenario("scenario1");
    }

    private void loadScenario(String scenario) {
        BufferedReader br = null;
        List<String> lines = new ArrayList<String>();
        try {
            br = Gdx.files.internal(scenario + ".dat").reader(8192);
            String str;
            while ((str = br.readLine()) != null) {
                lines.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lines.remove(0);
        waves = new Wave[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String[] arr = lines.get(i).split("\\s");
            waves[i] = new Wave(Float.parseFloat(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
        }
    }

    public void createMonster(int routeIndex) {
        for (int i = 0; i < monsters.length; i++) {
            if (!monsters[i].isActive()) {
                monsters[i].activate(routeIndex);
                break;
            }
        }
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        for (int i = 0; i < monsters.length; i++) {
            if (monsters[i].isActive()) {
                monsters[i].render(batch);
                monsters[i].renderHUD(batch, font);
            }
        }
    }

    public void update(float dt) {
        if (currentWave < waves.length) {
            spawnTimer += dt;
            Wave w = waves[currentWave];
            if (spawnTimer >= w.time) {
                for (int j = 0; j < w.monstersCount; j++) {
                    createMonster(w.routeIndex);
                }
                currentWave++;
            }
        }

        for (int i = 0; i < monsters.length; i++) {
            if (monsters[i].isActive()) {
                monsters[i].update(dt);
            }
        }
    }

    public void loadMonsterSaveFile(Map map, TextureRegion texture, TextureRegion textureBackHp, TextureRegion textureHp) {
        for (int i = 0; i < monsters.length; i++) {
            monsters[i].setMap(map);
            monsters[i].setTexture(texture);
            monsters[i].setTextureBackHp(textureBackHp);
            monsters[i].setTextureHp(textureHp);
            monsters[i].setRoute(map.getRoutes().get(monsters[i].getRouteIndex()));

        }
    }


}
