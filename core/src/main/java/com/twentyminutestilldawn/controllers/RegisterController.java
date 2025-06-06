package com.twentyminutestilldawn.controllers;

import com.badlogic.gdx.graphics.Texture;
import com.twentyminutestilldawn.Main;
import com.twentyminutestilldawn.models.GameAssetManager;
import com.twentyminutestilldawn.models.User;
import com.twentyminutestilldawn.models.UserDatabase;
import com.twentyminutestilldawn.views.RegisterMenu;
import com.twentyminutestilldawn.views.StartMenu;

public class RegisterController {
    private RegisterMenu view;

    public void setView(RegisterMenu view) {
        this.view = view;
    }

    public void handleRegister() {
        if (view.getRegisterButton().isPressed()) {
            String username = view.getUsernameField().getText().trim();
            String password = view.getPasswordField().getText().trim();
            String question = view.getSelectedQuestion();
            String answer = view.getAnswerField().getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                view.showError("Username and password are required.");
                return;
            }

            if (UserDatabase.userExists(username)) {
                view.showError("Username already exists.");
                return;
            }

            if (!isStrongPassword(password)) {
                view.showError("Password must be 8+ chars, 1 uppercase, 1 number, 1 symbol.");
                return;
            }

            String avatarPath = GameAssetManager.getGameAssetManager().getRandomAvatarPath();
            User user = new User(username, password, question, answer, avatarPath);
            UserDatabase.registerUser(user);
            view.showSuccess("Registered successfully!");

            Main.getMain().setScreen(new StartMenu(new StartMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
        }

        if (view.getBackButton().isPressed()) {
            Main.getMain().setScreen(new StartMenu(new StartMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
        }
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8 &&
            password.matches(".*[A-Z].*") &&
            password.matches(".*\\d.*") &&
            password.matches(".*[@#$%^&+=!()_.*].*");
    }
}
