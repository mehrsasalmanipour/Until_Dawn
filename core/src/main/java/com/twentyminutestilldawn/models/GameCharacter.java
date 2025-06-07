package com.twentyminutestilldawn.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class GameCharacter {
    public final String id;
    public final String name;
    private int currentHp;
    private int maxHp;
    private int bonusHp = 0;
    public final int speed;
    private float speedMultiplier = 1f;
    private float speedBuffTimeRemaining = 0f;
    public final Texture portrait;
    public final Animation<TextureRegion> animatedIdlePortrait;
    public final Animation<TextureRegion> animatedWalkPortrait;
    private final Vector2 position;
    private boolean invincible = false;
    private float invincibleTimer = 0f;
    private int level = 1;
    private int xp    = 0;
    private LevelUpListener levelUpListener;
    public interface LevelUpListener {
        void onLevelUp(int newLevel);
    }

    public void setLevelUpListener(LevelUpListener listener) {
        this.levelUpListener = listener;
    }

    public GameCharacter(String id, String name, int maxHp, int speed, Texture portrait,
                         Animation<TextureRegion> animatedIdlePortrait,
                         Animation<TextureRegion> animatedWalkPortrait) {
        this.id = id;
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.speed = speed;
        this.portrait = portrait;
        this.animatedIdlePortrait = animatedIdlePortrait;
        this.animatedWalkPortrait = animatedWalkPortrait;

        this.position = new Vector2();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void addToPosition(Vector2 delta) {
        this.position.add(delta);
    }

    public int getSpeed() {
        return speed;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public Texture getPortrait() {
        return portrait;
    }

    public int getMaxHp() {
        return maxHp + bonusHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void takeDamage(int amount) {
        currentHp = Math.max(0, currentHp - amount);
    }

    public void heal(int amount) {
        currentHp = Math.min(getMaxHp(), currentHp + amount);
    }

    public void addVitalityPoint() {
        bonusHp += 1;
        currentHp = Math.min(currentHp + 1, getMaxHp());
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(float duration) {
        invincible = true;
        invincibleTimer = duration;
    }

    public void updateInvincibility(float delta) {
        if (invincible) {
            invincibleTimer -= delta;
            if (invincibleTimer <= 0f) {
                invincible = false;
                invincibleTimer = 0f;
            }
        }
    }

    public void updateSpeedBuff(float delta) {
        if (speedBuffTimeRemaining > 0f) {
            speedBuffTimeRemaining -= delta;
            if (speedBuffTimeRemaining <= 0f) {
                speedBuffTimeRemaining = 0f;
                speedMultiplier = 1f;
            }
        }
    }

    public float getEffectiveSpeed() {
        return speed * speedMultiplier;
    }

    public void applyTemporarySpeedMultiplier(float mult, float durationSeconds) {
        speedMultiplier = mult;
        speedBuffTimeRemaining = durationSeconds;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public void addXp(int amount) {
        xp += amount;

        while (xp >= xpToNextLevel()) {
            xp -= xpToNextLevel();
            level++;

            if (levelUpListener != null) {
                levelUpListener.onLevelUp(level);
            }
        }
    }

    public int xpToNextLevel() {
        return 20 * level;
    }
}
