package com.twentyminutestilldawn.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Weapon {
    public final String id;
    public final String name;
    public final int damage;
    public final int projectileCount;
    public final int maxAmmo;
    public int currentAmmo;
    public final float reloadTime;
    public final Texture icon;
    public final Animation<TextureRegion> reloadAnimation;

    public Weapon(String id, String name, int damage, int projectileCount, int maxAmmo, float reloadTime, Texture icon, Animation<TextureRegion> reloadAnimation) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.projectileCount = projectileCount;
        this.maxAmmo = maxAmmo;
        this.currentAmmo = maxAmmo;
        this.reloadTime = reloadTime;
        this.icon = icon;
        this.reloadAnimation = reloadAnimation;
    }

    public String getId() {
        return id;
    }
}
