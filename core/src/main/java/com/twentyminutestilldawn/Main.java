package com.twentyminutestilldawn;

import com.badlogic.gdx.Game;
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
        getMain().setScreen(new StartMenu(new StartMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
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
