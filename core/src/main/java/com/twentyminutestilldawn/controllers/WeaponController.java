package com.twentyminutestilldawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.twentyminutestilldawn.models.Bullet;
import com.twentyminutestilldawn.models.GameAssetManager;
import com.twentyminutestilldawn.models.Weapon;

import java.util.ArrayList;
import java.util.List;

public class WeaponController {
    private final Weapon weapon;
    private final OrthographicCamera camera;

    private final List<Bullet> bullets = new ArrayList<>();
    private float shootCooldown = 0.2f;
    private float shootTimer = 0f;

    private final List<Bullet> enemyBullets = new ArrayList<>();

    private Texture bulletTexture;
    private Vector2 weaponTip = new Vector2();

    private boolean isReloading = false;
    private float reloadTimer = 0f;
    private float stateTime = 0f;

    private float damageMultiplier = 1f;
    private float damageBuffTimeRemaining = 0f;

    private int projectileBonus = 0;
    private int bonusMaxAmmo = 0;

    public WeaponController(Weapon weapon, OrthographicCamera camera) {
        this.weapon = weapon;
        this.camera = camera;
        this.bulletTexture = GameAssetManager.getGameAssetManager().getBulletTexture();
    }

    public void update(float delta) {
        if (damageBuffTimeRemaining > 0f) {
            damageBuffTimeRemaining -= delta;
            if (damageBuffTimeRemaining <= 0f) {
                damageBuffTimeRemaining = 0f;
                damageMultiplier = 1f;
            }
        }

        shootTimer -= delta;
        stateTime += delta;

        if (isReloading) {
            reloadTimer -= delta;
            if (reloadTimer <= 0f) {
                weapon.currentAmmo = getMaxAmmo();
                isReloading = false;
            }
        } else {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shootTimer <= 0f && weapon.currentAmmo > 0) {
                Vector3 mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                Vector2 target = new Vector2(mouse.x, mouse.y);

                int baseDmg = weapon.damage;
                int buffedDmg = Math.round(baseDmg * damageMultiplier);

                int effectiveCount = weapon.projectileCount + projectileBonus;

                for (int i = 0; i < effectiveCount; i++) {
                    float spreadAngle = 5f;
                    float angleOffset = spreadAngle * (i - (effectiveCount - 1) / 2f);

                    Vector2 baseDirection = new Vector2(target).sub(weaponTip).nor();

                    Vector2 spreadDirection = new Vector2(baseDirection).setAngleDeg(baseDirection.angleDeg() + angleOffset);

                    Vector2 bulletTarget = new Vector2(weaponTip).add(spreadDirection.scl(1000));
                    Bullet b = new Bullet(new Vector2(weaponTip), bulletTarget, bulletTexture, buffedDmg);
                    bullets.add(b);
                }

                weapon.currentAmmo--;
                shootTimer = shootCooldown;
            }

            if (weapon.currentAmmo == 0 && !isReloading) {
                isReloading = true;
                reloadTimer = weapon.reloadTime;
            }
        }

        for (Bullet bullet : bullets) {
            bullet.update(delta);
        }
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public int getProjectileBonus() {
        return projectileBonus;
    }

    public void increaseProjectileBonus(int amt) {
        projectileBonus += amt;
    }

    public int getMaxAmmo() {
        return weapon.maxAmmo + bonusMaxAmmo;
    }

    public void increaseMaxAmmoBy(int amount) {
        bonusMaxAmmo += amount;
        weapon.currentAmmo += amount;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public List<Bullet> getEnemyBullets() {
        return enemyBullets;
    }

    public void setWeaponTip(Vector2 tip) {
        this.weaponTip.set(tip);
    }

    public TextureRegion getWeaponTexture() {
        return new TextureRegion(weapon.icon);
    }

    public float getShootCooldown() {
        return shootCooldown;
    }

    public void setShootCooldown(float cooldown) {
        this.shootCooldown = cooldown;
    }

    public TextureRegion getReloadAnimationFrame() {
        if (weapon.reloadAnimation == null) return new TextureRegion();
        return weapon.reloadAnimation.getKeyFrame(stateTime, true);
    }

    public boolean isReloading() {
        return isReloading;
    }

    public int getCurrentAmmo() {
        return weapon.currentAmmo;
    }

    public float getReloadTimeRemaining() {
        return reloadTimer;
    }

    public void applyTemporaryDamageMultiplier(float multiplier, float durationSeconds) {
        damageMultiplier = multiplier;
        damageBuffTimeRemaining = durationSeconds;
    }
}
