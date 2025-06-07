package com.twentyminutestilldawn.controllers;

import com.twentyminutestilldawn.Main;
import com.twentyminutestilldawn.models.GameAssetManager;
import com.twentyminutestilldawn.models.SaveData;
import com.twentyminutestilldawn.models.SaveManager;
import com.twentyminutestilldawn.models.User;
import com.twentyminutestilldawn.views.*;

public class MainMenuController {
    private MainMenu view;
    private final User user;

    public void setView(MainMenu view) {
        this.view = view;
    }

    public MainMenuController(User user) {
        this.user = user;
    }

    public void handleMainMenuButtons() {
        if (view != null) {
            if (view.getSettingsButton().isChecked()) {
                Main.getMain().getScreen().dispose();
//                Main.getMain().setScreen(new SettingsMenu(new SettingsController(), GameAssetManager.getGameAssetManager().getSkin(), user));
            }

            if (view.getProfileButton().isChecked()) {
                Main.getMain().getScreen().dispose();
                Main.getMain().setScreen(new ProfileMenu(new ProfileController(user), user, GameAssetManager.getGameAssetManager().getSkin()));
            }

            if (view.getPregameButton().isChecked()) {
                Main.getMain().getScreen().dispose();
                Main.getMain().setScreen(new PregameMenu(new PregameController(user), GameAssetManager.getGameAssetManager().getSkin()));
            }

            if (view.getScoreboardButton().isChecked()) {
                Main.getMain().getScreen().dispose();
                Main.getMain().setScreen(new ScoreboardMenu(user));
            }

            if (view.getTalentButton().isChecked()) {
                Main.getMain().getScreen().dispose();
                Main.getMain().setScreen(new TalentMenu(new TalentController(user), GameAssetManager.getGameAssetManager().getSkin()));
            }

            if (view.getContinueButton().isChecked()) {
                SaveData save = SaveManager.loadGame(user);
                if (save != null) {
                    GameView gameView = new GameView(user, save, GameAssetManager.getGameAssetManager().getSkin());
                    Main.getMain().setScreen(gameView);
                }
            }

            if (view.getLogoutButton().isPressed()) {
                Main.getMain().getScreen().dispose();
                Main.getMain().setScreen(new LoginMenu(new LoginController(), GameAssetManager.getGameAssetManager().getSkin()));
            }
        }
    }
}
