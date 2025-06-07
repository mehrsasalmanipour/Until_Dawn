package com.twentyminutestilldawn;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.twentyminutestilldawn.controllers.StartMenuController;
import com.twentyminutestilldawn.models.GameAssetManager;
import com.twentyminutestilldawn.views.StartMenu;

public class Main extends Game {
    private static Main instance;
    private static SpriteBatch batch;

    @Override
    public void create() {
        instance = this;
        batch = new SpriteBatch();

        GameAssetManager assetManager = GameAssetManager.getGameAssetManager();
        assetManager.loadMusic("Track1", "LeMonde.mp3");

        Music music = assetManager.getMusic("Track1");
        if (music != null) {
            music.setLooping(true);
            music.setVolume(1f);
            music.play();
        } else {
            System.err.println("ERROR: Music 'Track1' not found.");
        }

        setScreen(new StartMenu(new StartMenuController(), assetManager.getSkin()));
    }


    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }

    public static Main getMain() {
        return instance;
    }

    public static SpriteBatch getBatch() {
        return batch;
    }
}
