package com.td.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class Monster implements Serializable {
    private transient Map map;
    private transient TextureRegion texture;
    private transient TextureRegion textureBackHp;
    private transient TextureRegion textureHp;
    private Vector2 position;
    private Vector2 velocity;
    private float speed;
    private boolean active;
    private transient Map.Route route;
    private int routeCounter;
    private int lastCellX, lastCellY;
    private float offsetX, offsetY;
    private int hp;
    private int hpMax;
    private int routeIndex;
    private StringBuilder sbHUD;

    public int getRouteIndex() {return routeIndex;}

    public void setMap(Map map) { this.map = map; }

    public void setTexture(TextureRegion texture) { this.texture = texture; }

    public void setTextureBackHp(TextureRegion textureBackHp) {this.textureBackHp = textureBackHp;}

    public void setTextureHp(TextureRegion textureHp) {this.textureHp = textureHp;}

    public void setRoute(Map.Route route) {this.route = route;}

    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public int getCellX() {
        return (int)(position.x / 80);
    }

    public int getCellY() {
        return (int)(position.y / 80);
    }

    public Monster(TextureAtlas atlas, Map map, int routeIndex) {
        this.map = map;
        this.texture = atlas.findRegion("monster");
        this.textureBackHp = atlas.findRegion("monsterBackHP");
        this.textureHp = atlas.findRegion("monsterHp");
        this.speed = 100.0f;
        this.routeIndex = routeIndex;
        this.route = map.getRoutes().get(routeIndex);
        this.offsetX = MathUtils.random(10, 70);
        this.offsetY = MathUtils.random(10, 70);
        this.position = new Vector2(route.getStartX() * 80 + offsetX, route.getStartY() * 80 + offsetY);
        this.lastCellX = route.getStartX();
        this.lastCellY = route.getStartY();
        this.routeCounter = 0;
        this.velocity = new Vector2(route.getDirections()[0].x * speed, route.getDirections()[0].y * speed);
        this.hpMax = 25;
        this.hp = this.hpMax;
        this.active = false;
        this.sbHUD = new StringBuilder(20);
    }

    public boolean takeDamage(int dmg) {
        hp -= dmg;
        if (hp <= 0) {
            active = false;
            return true;
        }
        return false;
    }

    public void deactivate() {
        active = false;
    }

    public void activate(int routeIndex) {
        this.routeIndex = routeIndex;
        this.offsetX = MathUtils.random(10, 70);
        this.offsetY = MathUtils.random(10, 70);
        this.route = map.getRoutes().get(routeIndex);
        this.position.set(route.getStartX() * 80 + offsetX, route.getStartY() * 80 + offsetY);
        this.lastCellX = route.getStartX();
        this.lastCellY = route.getStartY();
        this.routeCounter = 0;
        this.velocity.set(route.getDirections()[0].x * speed, route.getDirections()[0].y * speed);
        this.hpMax = 25;
        this.hp = this.hpMax;
        this.active = true;
        this.speed = MathUtils.random(80.0f, 120.0f);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - texture.getRegionWidth() / 2, position.y - texture.getRegionHeight() / 2, 40, 40,80,80,0.8f,0.8f,0);
    }

    public void renderHUD(SpriteBatch batch, BitmapFont font) {
        batch.draw(textureBackHp, position.x - 30, position.y + 40, 60, 16);
        batch.draw(textureHp, position.x - 30 + 2, position.y + 40 + 2, ((float)hp / hpMax) * 56, 12);
        sbHUD.setLength(0);
        sbHUD.append(hp);
        font.draw(batch, sbHUD, position.x - 30, position.y + 58, 60, 1, false);
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);

        int cx = (int) (position.x / 80);
        int cy = (int) (position.y / 80);

        float dx = Math.abs(cx * 80 + offsetX - position.x);
        float dy = Math.abs(cy * 80 + offsetY - position.y);

        if (map.isCrossroad(cx, cy) && Vector2.dst(0, 0, dx, dy) < velocity.len() * dt * 2) {
            if (!(lastCellX == cx && lastCellY == cy)) {
                position.set(cx * 80 + offsetX, cy * 80 + offsetY);
                routeCounter++;
                lastCellX = cx;
                lastCellY = cy;
                if (routeCounter > route.getDirections().length - 1) {
                    velocity.set(0, 0);
                    return;
                }
                velocity.set(route.getDirections()[routeCounter].x * speed, route.getDirections()[routeCounter].y * speed);
            }
        }
    }
}
