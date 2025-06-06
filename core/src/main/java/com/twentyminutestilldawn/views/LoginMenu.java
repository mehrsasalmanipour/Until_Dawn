package com.twentyminutestilldawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.twentyminutestilldawn.controllers.LoginController;
import com.twentyminutestilldawn.models.User;
import com.twentyminutestilldawn.models.UserDatabase;

public class LoginMenu implements Screen {
    private final Skin skin;
    private final LoginController controller;
    private Stage stage;
    private Table table;

    private final TextField usernameField;
    private final TextField passwordField;
    private final TextButton loginButton;
    private final TextButton backButton;
    private final TextButton forgotButton;
    private final Label messageLabel;

    private boolean dialogOpen = false;

    public LoginMenu(LoginController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.usernameField = new TextField("", skin);
        this.passwordField = new TextField("", skin);
        this.loginButton = new TextButton("Login", skin);
        this.backButton = new TextButton("Back", skin);
        this.forgotButton = new TextButton("Forgot Password?", skin);
        this.messageLabel = new Label("", skin);
        messageLabel.setColor(Color.RED);
        messageLabel.setVisible(false);

        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        table.center();

        usernameField.setMessageText("Username");
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        table.add(new Label("Login", skin)).padBottom(20).row();
        table.add(usernameField).width(250).pad(5).row();
        table.add(passwordField).width(250).pad(5).row();
        table.add(loginButton).width(150).pad(10).row();
        table.add(forgotButton).width(150).pad(5).row();
        table.add(backButton).width(150).pad(10).row();
        table.add(messageLabel).padTop(10).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
        controller.handleLogin();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
    }

    public TextField getUsernameField() { return usernameField; }
    public TextField getPasswordField() { return passwordField; }
    public TextButton getLoginButton() { return loginButton; }
    public TextButton getBackButton() { return backButton; }
    public TextButton getForgotButton() { return forgotButton; }

    public void showError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setColor(Color.RED);
        messageLabel.setVisible(true);
    }

    public void showSuccess(String msg) {
        messageLabel.setText(msg);
        messageLabel.setColor(Color.GREEN);
        messageLabel.setVisible(true);
    }

    public void showForgotPasswordDialog(User user) {
        if (dialogOpen) return;
        dialogOpen = true;

        Dialog dialog = new Dialog("Recover Password", skin) {
            @Override
            protected void result(Object object) {
                dialogOpen = false;
            }
        };

        TextField answerField = new TextField("", skin);
        answerField.setMessageText("Your Answer");

        TextField newPasswordField = new TextField("", skin);
        newPasswordField.setMessageText("New Password");
        newPasswordField.setPasswordMode(true);
        newPasswordField.setPasswordCharacter('*');

        Label feedback = new Label("", skin);
        feedback.setColor(Color.RED);

        TextButton submit = new TextButton("Submit", skin);
        TextButton cancel = new TextButton("Cancel", skin);

        submit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String answer = answerField.getText().trim();
                String newPassword = newPasswordField.getText().trim();

                if (!answer.equalsIgnoreCase(user.getSecurityAnswer())) {
                    feedback.setText("Wrong answer.");
                    return;
                }

                if (newPassword.isEmpty()) {
                    feedback.setText("New password can't be empty.");
                    return;
                }

                user.setPassword(newPassword);
                UserDatabase.registerUser(user);
                dialog.hide();
                showSuccess("Password updated. Try logging in.");
            }
        });

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.getContentTable().add(new Label(user.getSecurityQuestion(), skin)).pad(10).row();
        dialog.getContentTable().add(answerField).width(250).pad(5).row();
        dialog.getContentTable().add(newPasswordField).width(250).pad(5).row();
        dialog.getContentTable().add(feedback).pad(5).row();
        dialog.button(submit);
        dialog.button(cancel);
        dialog.show(stage);
    }
}