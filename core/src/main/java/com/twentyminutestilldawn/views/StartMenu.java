package com.twentyminutestilldawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.twentyminutestilldawn.controllers.StartMenuController;

public class StartMenu implements Screen {
    private Stage stage;
    private Skin skin;
    private final Table table;
    private final StartMenuController controller;

    private final TextButton loginButton;
    private final TextButton signupButton;
    private final TextButton guestButton;

    public StartMenu(StartMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.table = new Table();

        this.loginButton = new TextButton("Log In", skin);
        this.signupButton = new TextButton("Sign Up", skin);
        this.guestButton = new TextButton("Play as Guest", skin);

        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.center();

        Label title = new Label("Welcome to Game", skin);
        table.add(title).padBottom(30).row();
        table.add(loginButton).width(200).pad(10).row();
        table.add(signupButton).width(200).pad(10).row();
        table.add(guestButton).width(200).pad(10).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        controller.handleStartMenuButtons();
    }

    @Override
    public void resize(int width, int hight) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public TextButton getLoginButton() { return loginButton; }
    public TextButton getSignupButton() { return signupButton; }
    public TextButton getGuestButton() { return guestButton; }
}
