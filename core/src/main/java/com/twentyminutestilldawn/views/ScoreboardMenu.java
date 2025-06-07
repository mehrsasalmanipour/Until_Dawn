package com.twentyminutestilldawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.twentyminutestilldawn.Main;
import com.twentyminutestilldawn.controllers.MainMenuController;
import com.twentyminutestilldawn.controllers.ScoreboardController;
import com.twentyminutestilldawn.models.GameAssetManager;
import com.twentyminutestilldawn.models.User;

import java.util.List;

public class ScoreboardMenu implements Screen {

    private final Stage stage;
    private final Skin skin;
    private final ScoreboardController controller;
    private final User currentUser;

    public ScoreboardMenu(User user) {
        this.currentUser = user;
        this.skin = GameAssetManager.getGameAssetManager().getSkin();
        this.stage = new Stage(new ScreenViewport());
        this.controller = new ScoreboardController();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        updateTable(ScoreboardController.SortMode.SCORE);
    }

    private void updateTable(ScoreboardController.SortMode mode) {
        stage.clear();

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Table table = new Table(skin);
        table.add("Rank").pad(5);
        table.add("Username").pad(5);
        table.add("Score").pad(5);
        table.add("Kills").pad(5);
        table.add("Survival Time").pad(5).row();

        List<User> topUsers = controller.getTopUsers(mode);
        for (int i = 0; i < topUsers.size(); i++) {
            User user = topUsers.get(i);

            Label usernameLabel = new Label(user.getUsername(), skin);
            if (user.equals(currentUser)) {
                usernameLabel.setColor(0, 1, 1, 1);
            }

            Label scoreLabel = new Label(String.valueOf(user.getScore()), skin);
            Label killsLabel = new Label(String.valueOf(user.getTotalKills()), skin);
            Label timeLabel = new Label(String.format("%.0f", user.getTotalSurvivalTime()), skin);

            if (i == 0) usernameLabel.setColor(1, 0.84f, 0, 1);
            else if (i == 1) usernameLabel.setColor(0.75f, 0.75f, 0.75f, 1);
            else if (i == 2) usernameLabel.setColor(0.8f, 0.5f, 0.2f, 1);

            table.add(String.valueOf(i + 1));
            table.add(usernameLabel);
            table.add(scoreLabel);
            table.add(killsLabel);
            table.add(timeLabel).row();
        }

        Table buttonBar = new Table(skin);
        TextButton scoreBtn = new TextButton("Sort by Score", skin);
        scoreBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateTable(ScoreboardController.SortMode.SCORE);
            }
        });
        TextButton nameBtn = new TextButton("Sort by Name", skin);
        nameBtn.addListener(new ChangeListener() { // ✅ correct button
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateTable(ScoreboardController.SortMode.USERNAME);
            }
        });
        TextButton killBtn = new TextButton("Sort by Kill", skin);
        killBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateTable(ScoreboardController.SortMode.KILL);
            }
        });
        TextButton timeBtn = new TextButton("Sort by Time", skin);
        timeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateTable(ScoreboardController.SortMode.SURVIVAL);
            }
        });

        buttonBar.add(scoreBtn).padRight(10);
        buttonBar.add(nameBtn).padRight(10);
        buttonBar.add(killBtn).padRight(10);
        buttonBar.add(timeBtn);

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getMain().setScreen(new MainMenu(new MainMenuController(currentUser), skin, currentUser));
            }
        });

        root.top().padTop(20);
        root.add(table).expandX().top().row();
        root.add(buttonBar).padTop(20).row();
        root.add(backButton).expandY().bottom().padBottom(20);
    }

    @Override
    public void render(float delta) {
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
