package com.twentyminutestilldawn.models;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Seed {
    public final Vector2 position;
    public final TextureRegion region;

    public Seed(Vector2 position, TextureRegion region) {
        this.position = position;
        this.region = region;
    }
}
