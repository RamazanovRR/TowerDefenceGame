package com.td.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TurretEmitter implements Serializable {
    public class TurretTemplate implements Serializable {
        private int imageIndex;
        private int cost;
        private int damage;
        private int radius;
        private float fireRate;

        public int getImageIndex() {
            return imageIndex;
        }

        public int getCost() {
            return cost;
        }

        public int getDamage() {
            return damage;
        }

        public int getRadius() {
            return radius;
        }

        public float getFireRate() {
            return fireRate;
        }

        // #image_index cost fire_rate damage radius
        public TurretTemplate(String line) {
            String[] tokens = line.split("\\s");
            imageIndex = Integer.parseInt(tokens[0]);
            cost = Integer.parseInt(tokens[1]);
            fireRate = Float.parseFloat(tokens[2]);
            damage = Integer.parseInt(tokens[3]);
            radius = Integer.parseInt(tokens[4]);
        }
    }

    private transient GameScreen gameScreen;
    private transient TextureAtlas atlas;
    private transient Map map;
    private Turret[] turrets;
    private TurretTemplate[] templates;
    private transient TextureRegion[] regions;

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void setAtlas(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public TurretEmitter(TextureAtlas atlas, GameScreen gameScreen, Map map) {
        this.loadTurretData();
        this.gameScreen = gameScreen;
        this.map = map;
        this.atlas = atlas;
        this.turrets = new Turret[20];
        regions = new TextureRegion(atlas.findRegion("turrets")).split(80, 80)[0];
        for (int i = 0; i < turrets.length; i++) {
            turrets[i] = new Turret(regions, gameScreen, map, 0, 0);
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < turrets.length; i++) {
            if (turrets[i].isActive()) {
                turrets[i].render(batch);
            }
        }
    }

    public void update(float dt) {
        for (int i = 0; i < turrets.length; i++) {
            if (turrets[i].isActive()) {
                turrets[i].update(dt);
            }
        }
    }

    public int getTurretCost(int index) {
        return templates[index].cost;
    }

    public void setTurret(int index, int cellX, int cellY) {
        if (map.isCellEmpty(cellX, cellY)) {
            for (int i = 0; i < turrets.length; i++) {
                if (turrets[i].isActive() && turrets[i].getCellX() == cellX && turrets[i].getCellY() == cellY) {
                    return;
                }
            }
            for (int i = 0; i < turrets.length; i++) {
                if (!turrets[i].isActive()) {
                    turrets[i].activate(templates[index], cellX, cellY);
                    break;
                }
            }
        }
    }

    public void destroyTurret(int cellX, int cellY) {
        for (int i = 0; i < turrets.length; i++) {
            if (turrets[i].isActive() && turrets[i].getCellX() == cellX && turrets[i].getCellY() == cellY) {
                turrets[i].deactivate();
            }
        }
    }

    public void loadTurretData() {
        BufferedReader br = null;
        List<String> lines = new ArrayList<String>();
        try {
            br = Gdx.files.internal("turrets.dat").reader(8192);
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
        templates = new TurretTemplate[lines.size() - 1];
        for (int i = 1; i < lines.size(); i++) {
            templates[i - 1] = new TurretTemplate(lines.get(i));
        }
    }

    public void loadTurretsSaveFile (){
        for(int i = 0; i < turrets.length; i++) {
            turrets[i].setMap(map);
            turrets[i].setGameScreen(gameScreen);
            turrets[i].setRegions(atlas.findRegion("turrets").split(80, 80)[0]);
        }
    }
}

