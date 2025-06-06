package com.twentyminutestilldawn.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    public Sprite sprite;
    public Vector2 direction;
    public float speed = 400f;
    public int damage;

    public Bullet(Vector2 spawn, Vector2 target, Texture texture, int damage) {
        sprite = new Sprite(texture);
        sprite.setSize(25, 25);
        sprite.setOriginCenter();
        sprite.setPosition(spawn.x - 10, spawn.y - 10);
        direction = target.cpy().sub(spawn).nor();
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void update(float delta) {
        sprite.translate(direction.x * speed * delta, direction.y * speed * delta);
    }
}
