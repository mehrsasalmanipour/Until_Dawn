package com.twentyminutestilldawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.twentyminutestilldawn.Main;
import com.twentyminutestilldawn.controllers.MainMenuController;
import com.twentyminutestilldawn.models.GameAssetManager;
import com.twentyminutestilldawn.models.User;

public class SettingsMenu implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final User user;

    private final SelectBox<String> musicSelector;
    private final Slider volumeSlider;
    private CheckBox bwCheckbox;

    public SettingsMenu(User user) {
        this.user = user;
        this.skin = GameAssetManager.getGameAssetManager().getSkin();
        this.stage = new Stage(new ScreenViewport());

        musicSelector = new SelectBox<>(skin);
        volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table(skin);
        table.setFillParent(true);
        stage.addActor(table);

        table.add(new Label("Music Track:", skin)).padBottom(10).row();
        musicSelector.setItems("Track1", "Track2", "Track3");

        table.add(musicSelector).padBottom(20).row();

        table.add(new Label("Volume:", skin)).padBottom(10).row();
        table.add(volumeSlider).width(200).padBottom(20).row();

        bwCheckbox = new CheckBox("Black & White Mode", skin);
        bwCheckbox.setChecked(GameAssetManager.getGameAssetManager().isGrayscaleEnabled());
        table.add(bwCheckbox).padBottom(20).row();

        TextButton applyButton = new TextButton("Apply", skin);
        applyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySettings();
            }
        });

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getMain().setScreen(new MainMenu(new MainMenuController(user), skin, user));
            }
        });

        table.add(applyButton).padBottom(10).row();
        table.add(backButton);
    }

    private void applySettings() {
        GameAssetManager assetManager = GameAssetManager.getGameAssetManager();
        assetManager.stopAllMusic();

        String selected = musicSelector.getSelected();
        Music selectedMusic = assetManager.getMusic(selected);
        selectedMusic.setVolume(volumeSlider.getValue());
        selectedMusic.play();

        boolean isBW = bwCheckbox.isChecked();
        assetManager.setGrayscaleEnabled(isBW);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}

