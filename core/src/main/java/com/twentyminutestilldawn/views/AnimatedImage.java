package com.twentyminutestilldawn.views;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class AnimatedImage extends Actor {
    private final Animation<TextureRegion> animation;
    private float stateTime = 0f;

    public AnimatedImage(Animation<TextureRegion> animation) {
        this.animation = animation;
        TextureRegion firstFrame = animation.getKeyFrame(0f);
        setSize(firstFrame.getRegionWidth(), firstFrame.getRegionHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.setColor(getColor());
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }
}
