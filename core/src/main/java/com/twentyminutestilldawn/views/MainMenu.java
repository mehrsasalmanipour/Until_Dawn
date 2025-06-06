package com.twentyminutestilldawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.twentyminutestilldawn.controllers.MainMenuController;
import com.twentyminutestilldawn.models.User;

public class MainMenu implements Screen {
    private final Skin skin;
    private final MainMenuController controller;
    private final User user;
    private Stage stage;

    private TextButton settingsBtn;
    private TextButton profileBtn;
    private TextButton pregameBtn;
    private TextButton scoreboardBtn;
    private TextButton talentBtn;
    private TextButton continueBtn;
    private TextButton logoutBtn;

    public MainMenu(MainMenuController controller, Skin skin, User user) {
        this.controller = controller;
        this.skin = skin;
        this.user = user;
        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(30);
        rootTable.top();
        stage.addActor(rootTable);

        Table leftTable = new Table();
        Image avatarImage = new Image(new Texture(user.getAvatarPath()));
        avatarImage.setSize(100, 100);
        avatarImage.setScaling(Scaling.fit);

        Label usernameLabel = new Label("User: " + user.getUsername(), skin);
        Label scoreLabel = new Label("Score: " + user.getScore(), skin);

        leftTable.add(avatarImage).padBottom(10).row();
        leftTable.add(usernameLabel).padBottom(5).row();
        leftTable.add(scoreLabel).padBottom(15).row();

        Table rightTable = new Table();
        settingsBtn = new TextButton("Settings", skin);
        profileBtn = new TextButton("Profile", skin);
        pregameBtn = new TextButton("Pre-game Menu", skin);
        scoreboardBtn = new TextButton("Scoreboard", skin);
        talentBtn = new TextButton("Talent", skin);
        continueBtn = new TextButton("Continue Saved Game", skin);
        logoutBtn = new TextButton("Logout", skin);

        rightTable.add(settingsBtn).fillX().uniformX().pad(1).row();
        rightTable.add(profileBtn).fillX().uniformX().pad(1).row();
        rightTable.add(pregameBtn).fillX().uniformX().pad(1).row();
        rightTable.add(scoreboardBtn).fillX().uniformX().pad(1).row();
        rightTable.add(talentBtn).fillX().uniformX().pad(1).row();
        rightTable.add(continueBtn).fillX().uniformX().pad(1).row();
        rightTable.add(logoutBtn).fillX().uniformX().pad(1).row();

        rootTable.add(leftTable).top().left().padRight(50);
        rootTable.add(rightTable).top().right().expandX();
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        controller.handleMainMenuButtons();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
    }

    public TextButton getSettingsButton() { return settingsBtn; }
    public TextButton getProfileButton() { return profileBtn; }
    public TextButton getPregameButton() { return pregameBtn; }
    public TextButton getScoreboardButton() { return scoreboardBtn; }
    public TextButton getTalentButton() { return talentBtn; }
    public TextButton getContinueButton() { return continueBtn; }
    public TextButton getLogoutButton() { return logoutBtn; }
}