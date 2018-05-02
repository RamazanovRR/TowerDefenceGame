package com.td.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class Turret implements Serializable {
    private transient GameScreen gameScreen;
    private transient Map map;
    private int type;
    private int damage;
    private transient TextureRegion[] regions;
    private Vector2 position;
    private float angle;
    private float range;
    private float fireDelay;
    private float fireTimer;
    private float rotationSpeed;
    private Monster target;
    private boolean active;

    public void setGameScreen(GameScreen gameScreen) { this.gameScreen = gameScreen; }

    public void setMap(Map map) { this.map = map; }

    public void setRegions(TextureRegion[] regions) { this.regions = regions; }

    public boolean isActive() {
        return active;
    }

    public int getCellX() {
        return (int) (position.x / 80);
    }

    public int getCellY() {
        return (int) (position.y / 80);
    }

    private Vector2 tmpVector;

    public Turret(TextureRegion[] regions, GameScreen gameScreen, Map map, float cellX, float cellY) {
        this.regions = regions;
        this.gameScreen = gameScreen;
        this.map = map;
        this.range = 300;
        this.damage = 1;
        this.rotationSpeed = 270.0f;
        this.fireDelay = 0.1f;
        this.position = new Vector2(cellX * 80 + 40, cellY * 80 + 40);
        this.angle = 0;
        this.tmpVector = new Vector2(0, 0);
        this.active = false;
    }

    public void activate(TurretEmitter.TurretTemplate template, int cellX, int cellY) {
        this.range = template.getRadius();
        this.fireDelay = template.getFireRate();
        this.type = template.getImageIndex();
        this.damage = template.getDamage();
        setTurretToCell(cellX, cellY);
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public void setTurretToCell(int cellX, int cellY) {
        if (map.isCellEmpty(cellX, cellY)) {
            position.set(cellX * 80 + 40, cellY * 80 + 40);
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(regions[type], position.x - 40, position.y - 40, 40, 40, 80, 80, 1, 1, angle);
    }

    public boolean checkMonsterInRange(Monster monster) {
        return Vector2.dst(position.x, position.y, monster.getPosition().x, monster.getPosition().y) < range;
    }

    public void update(float dt) {
        if (target != null && (!checkMonsterInRange(target) || !target.isActive())) {
            target = null;
        }
        if (target == null) {
            Monster[] monsters = gameScreen.getMonsterEmitter().getMonsters();
            for (int i = 0; i < monsters.length; i++) {
                if (monsters[i].isActive() && checkMonsterInRange(monsters[i])) {
                    target = monsters[i];
                    break;
                }
            }
        }
        checkRotation(dt);
        tryToFire(dt);
    }

    public float getAngleToTarget() {
        return tmpVector.set(target.getPosition()).sub(position).angle();
    }

    public void checkRotation(float dt) {
        if (target != null) {
            float angleTo = getAngleToTarget();
            if (angle > angleTo) {
                if (Math.abs(angle - angleTo) <= 180.0f) {
                    angle -= rotationSpeed * dt;
                } else {
                    angle += rotationSpeed * dt;
                }
            }
            if (angle < angleTo) {
                if (Math.abs(angle - angleTo) <= 180.0f) {
                    angle += rotationSpeed * dt;
                } else {
                    angle -= rotationSpeed * dt;
                }
            }
            if (angle < 0.0f) {
                angle += 360.0f;
            }
            if (angle > 360.0f) {
                angle -= 360.0f;
            }
        }
    }

    public void tryToFire(float dt) {
        fireTimer += dt;
        if (target != null && fireTimer >= fireDelay && Math.abs(angle - getAngleToTarget()) < 15) {
            fireTimer = 0.0f;
            float bulletSpeed = 400.0f;
            float fromX = position.x + (float) Math.cos(Math.toRadians(angle)) * 28;
            float fromY = position.y + (float) Math.sin(Math.toRadians(angle)) * 28;
            float time = Vector2.dst(fromX, fromY, target.getPosition().x, target.getPosition().y) / bulletSpeed;
            float toX = target.getPosition().x + target.getVelocity().x * time;
            float toY = target.getPosition().y + target.getVelocity().y * time;
            gameScreen.getParticleEmitter().setupByTwoPoints(fromX, fromY, toX, toY, time, 1.2f, 1.5f, 1, 1, 0, 1, 1, 0, 0, 1);
            gameScreen.getParticleEmitter().setupByTwoPoints(fromX, fromY, toX + MathUtils.random(-10, 10), toY + MathUtils.random(-10, 10), time, 1.2f, 1.5f, 1, 1, 0, 1, 1, 0, 0, 1);

            if (target.takeDamage(damage)) {
                gameScreen.getPlayerInfo().addMoney(10);
            }
        }
    }
}
