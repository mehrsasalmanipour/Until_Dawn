package com.twentyminutestilldawn.models;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Monster {
    public final MonsterType type;
    public final Vector2 position;
    public final Animation<TextureRegion> walkAnimation;
    private final Vector2 velocity = new Vector2();
    private final int maxHp;
    private int currentHp;
    private float shootCooldown = 0f;
    private float elderShootCooldown = 0f;

    public Monster(MonsterType type, Vector2 position, Animation<TextureRegion> walkAnimation, int hp) {
        this.type = type;
        this.position = position;
        this.walkAnimation = walkAnimation;
        this.maxHp = hp;
        this.currentHp = hp;

        if (type == MonsterType.EYEBAT) {
            this.shootCooldown = 0f;
        }
    }

    public void moveTowards(Vector2 target, float speed, float delta) {
        Vector2 direction = new Vector2(target).sub(position).nor();
        velocity.set(direction).scl(speed * delta);
        position.add(velocity);
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void takeDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public void updateEyebatTimer(float delta) {
        if (type == MonsterType.EYEBAT && shootCooldown > 0f) {
            shootCooldown -= delta;
        }
    }

    public boolean canEyebatShoot() {
        return type == MonsterType.EYEBAT && shootCooldown <= 0f;
    }

    public void resetEybatCooldown() {
        if (type == MonsterType.EYEBAT) {
            this.shootCooldown = 3.0f;
        }
    }

    public void updateElderTimer(float delta) {
        if (type == MonsterType.ELDER && elderShootCooldown > 0f) {
            elderShootCooldown -= delta;
        }
    }
    public boolean canElderShoot() {
        return (type == MonsterType.ELDER && elderShootCooldown <= 0f);
    }
    public void resetElderCooldown() {
        if (type == MonsterType.ELDER) {
            elderShootCooldown = 5.0f;
        }
    }
}

