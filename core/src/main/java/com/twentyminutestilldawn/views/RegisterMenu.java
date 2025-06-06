package com.twentyminutestilldawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.twentyminutestilldawn.controllers.RegisterController;

public class RegisterMenu implements Screen {
    private Stage stage;
    private final Skin skin;
    private Table table;
    private final RegisterController controller;

    private final TextField usernameField;
    private final TextField passwordField;
    private final SelectBox<String> questionBox;
    private final TextField answerField;
    private final TextButton registerButton;
    private final TextButton backButton;
    private final Label errorLabel;

    public RegisterMenu(RegisterController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.table = new Table();

        this.usernameField = new TextField("", skin);
        this.passwordField = new TextField("", skin);
        this.questionBox = new SelectBox<>(skin);
        this.questionBox.setItems(
                "What is your favorite number?",
                "What is your favorite music?",
                "What is your favorite food?"
        );

        this.answerField = new TextField("", skin);
        this.registerButton = new TextButton("Register", skin);
        this.backButton = new TextButton("Back", skin);
        this.errorLabel = new Label("", skin);
        this.errorLabel.setColor(Color.RED);
        this.errorLabel.setVisible(false);

        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.center();

        Label title = new Label("Register", skin);
        usernameField.setMessageText("Username");
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        answerField.setMessageText("Security Answer");

        table.add(title).padBottom(20).row();
        table.add(usernameField).width(250).pad(5).row();
        table.add(passwordField).width(250).pad(5).row();
        table.add(questionBox).width(250).pad(5).row();
        table.add(answerField).width(250).pad(5).row();
        table.add(registerButton).width(150).pad(10).padBottom(5).row();
        table.add(backButton).width(150).pad(10).row();
        table.add(errorLabel).padTop(10).row();

        stage.addActor(table);

        addHoverEffect(registerButton);
        addHoverEffect(backButton);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        controller.handleRegister();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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

    public TextField getUsernameField() { return usernameField; }
    public TextField getPasswordField() { return passwordField; }
    public String getSelectedQuestion() {
        return questionBox.getSelected();
    }
    public TextField getAnswerField() { return answerField; }
    public TextButton getRegisterButton() { return registerButton; }
    public TextButton getBackButton() { return backButton; }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setColor(Color.RED);
        errorLabel.setVisible(true);
    }

    public void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setColor(Color.GREEN);
        errorLabel.setVisible(true);

        usernameField.setText("");
        passwordField.setText("");
        answerField.setText("");
    }

    private void addHoverEffect(final TextButton button) {
        button.addListener(new InputListener() {
            @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.setColor(Color.TEAL);
            }

            @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setColor(Color.SKY);
            }
        });
    }

}
