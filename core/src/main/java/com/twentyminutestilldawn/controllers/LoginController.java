package com.twentyminutestilldawn.controllers;

import com.twentyminutestilldawn.Main;
import com.twentyminutestilldawn.models.User;
import com.twentyminutestilldawn.models.UserDatabase;
import com.twentyminutestilldawn.views.LoginMenu;
import com.twentyminutestilldawn.views.MainMenu;
import com.twentyminutestilldawn.views.StartMenu;
import com.twentyminutestilldawn.models.GameAssetManager;

public class LoginController {
    private LoginMenu view;

    public void setView(LoginMenu view) {
        this.view = view;
    }

    public void handleLogin() {
        if (view.getLoginButton().isPressed()) {
            String username = view.getUsernameField().getText().trim();
            String password = view.getPasswordField().getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                view.showError("Please enter username and password.");
                return;
            }

            if (!UserDatabase.userExists(username)) {
                view.showError("Username does not exist.");
                return;
            }

            if (!UserDatabase.validateLogin(username, password)) {
                view.showError("Incorrect password.");
                return;
            }

            User user = UserDatabase.getUser(username);

            Main.getMain().getScreen().dispose();
            Main.getMain().setScreen(new MainMenu(new MainMenuController(user), GameAssetManager.getGameAssetManager().getSkin(), user));
            return;
        }

        if (view.getBackButton().isPressed()) {
            Main.getMain().setScreen(new StartMenu(new StartMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
        }

        if (view.getForgotButton().isPressed()) {
            String username = view.getUsernameField().getText().trim();

            if (!UserDatabase.userExists(username)) {
                view.showError("Enter a valid username first.");
                return;
            }

            User user = UserDatabase.getUser(username);
            view.showForgotPasswordDialog(user);
        }
    }
}
