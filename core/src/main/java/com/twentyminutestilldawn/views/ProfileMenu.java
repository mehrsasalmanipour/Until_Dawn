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
import com.twentyminutestilldawn.Main;
import com.twentyminutestilldawn.controllers.MainMenuController;
import com.twentyminutestilldawn.controllers.ProfileController;
import com.twentyminutestilldawn.controllers.StartMenuController;
import com.twentyminutestilldawn.models.GameAssetManager;
import com.twentyminutestilldawn.models.User;

public class ProfileMenu implements Screen {
    private final User user;
    private final Skin skin;
    private final ProfileController controller;

    private Stage stage;
    private Table table;

    private TextField usernameField;
    private TextField passwordField;
    private TextButton changeUsernameButton;
    private TextButton changePasswordButton;
    private TextButton deleteAccountButton;
    private TextButton chooseAvatarButton;
    private Label messageLabel;

    private TextButton backButton;

    public ProfileMenu(ProfileController controller, User user, Skin skin) {
        this.controller = controller;
        this.user = user;
        this.skin = skin;
        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.center();

        usernameField = new TextField(user.getUsername(), skin);
        passwordField = new TextField("", skin);
        passwordField.setMessageText("New Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        changeUsernameButton = new TextButton("Change Username", skin);
        changePasswordButton = new TextButton("Change Password", skin);
        deleteAccountButton = new TextButton("Delete Account", skin);
        chooseAvatarButton = new TextButton("Choose Avatar", skin);
        backButton = new TextButton("Back", skin);

        messageLabel = new Label("", skin);
        messageLabel.setVisible(false);

        table.add(new Label("Profile Menu", skin)).padBottom(20).row();
        table.add(usernameField).width(250).pad(1).row();
        table.add(changeUsernameButton).width(200).pad(1).row();
        table.add(passwordField).width(250).pad(1).row();
        table.add(changePasswordButton).width(200).pad(1).row();
        table.add(chooseAvatarButton).width(200).pad(1).row();
        table.add(deleteAccountButton).width(200).padTop(1).row();
        table.add(backButton).width(200).padTop(1).row();
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getMain().setScreen(new MainMenu(
                        new MainMenuController(user),
                        skin,
                        user
                ));
            }
        });

        table.add(messageLabel).padTop(1).row();

        stage.addActor(table);

        setupListeners();
    }

    private void setupListeners() {
        changeUsernameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String newUsername = usernameField.getText().trim();
                if (controller.updateUsername(newUsername)) {
                    showSuccess("Username changed!");
                } else {
                    showError("Username already exists.");
                }
            }
        });

        changePasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String newPassword = passwordField.getText().trim();
                if (controller.updatePassword(newPassword)) {
                    showSuccess("Password updated.");
                } else {
                    showError("Password too simple.");
                }
            }
        });

        chooseAvatarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Window avatarWindow = new Window("Choose Avatar", skin);
                avatarWindow.setModal(true);
                avatarWindow.setMovable(false);
                avatarWindow.pad(10);

                Table avatarTable = new Table();
                int columns = 2;

                final String path0 = "Avatar/avatar_0.png";
                Image avatarImage0 = new Image(GameAssetManager.getGameAssetManager().getAllAvatars().get(0));
                avatarImage0.setSize(64, 64);
                avatarImage0.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        controller.updateAvatar(path0);
                        avatarWindow.remove();
                        showSuccess("Avatar changed.");
                    }
                });
                avatarTable.add(avatarImage0).size(64).pad(10);

                final String path1 = "Avatar/avatar_1.png";
                Image avatarImage1 = new Image(GameAssetManager.getGameAssetManager().getAllAvatars().get(1));
                avatarImage1.setSize(64, 64);
                avatarImage1.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        controller.updateAvatar(path1);
                        avatarWindow.remove();
                        showSuccess("Avatar changed.");
                    }
                });
                avatarTable.add(avatarImage1).size(64).pad(10);
                avatarTable.row();

                final String path2 = "Avatar/avatar_2.png";
                Image avatarImage2 = new Image(GameAssetManager.getGameAssetManager().getAllAvatars().get(2));
                avatarImage2.setSize(64, 64);
                avatarImage2.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        controller.updateAvatar(path2);
                        avatarWindow.remove();
                        showSuccess("Avatar changed.");
                    }
                });
                avatarTable.add(avatarImage2).size(64).pad(10);

                final String path3 = "Avatar/avatar_3.png";
                Image avatarImage3 = new Image(GameAssetManager.getGameAssetManager().getAllAvatars().get(3));
                avatarImage3.setSize(64, 64);
                avatarImage3.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        controller.updateAvatar(path3);
                        avatarWindow.remove();
                        showSuccess("Avatar changed.");
                    }
                });
                avatarTable.add(avatarImage3).size(64).pad(10);
                avatarTable.row();

                TextButton cancelButton = new TextButton("Cancel", skin);
                cancelButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        avatarWindow.remove();
                    }
                });
                avatarWindow.add(avatarTable).row();
                avatarWindow.add(cancelButton).padTop(10).center();
                avatarWindow.pack();
                avatarWindow.setPosition(
                        (stage.getWidth() - avatarWindow.getWidth()) / 2f,
                        (stage.getHeight() - avatarWindow.getHeight()) / 2f
                );
                stage.addActor(avatarWindow);
            }
        });

        deleteAccountButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.deleteAccount();
                Main.getMain().setScreen(new StartMenu(new StartMenuController(), skin));
            }
        });
    }

    private void showError(String msg) {
        messageLabel.setColor(Color.RED);
        messageLabel.setText(msg);
        messageLabel.setVisible(true);
    }

    private void showSuccess(String msg) {
        messageLabel.setColor(Color.GREEN);
        messageLabel.setText(msg);
        messageLabel.setVisible(true);
    }

    @Override public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void dispose() { stage.dispose(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}