package com.twentyminutestilldawn.controllers;

import com.twentyminutestilldawn.Main;
import com.twentyminutestilldawn.models.GameAssetManager;
import com.twentyminutestilldawn.views.LoginMenu;
import com.twentyminutestilldawn.views.PregameMenu;
import com.twentyminutestilldawn.views.RegisterMenu;
import com.twentyminutestilldawn.views.StartMenu;

public class StartMenuController {
    private StartMenu view;

    public void setView(StartMenu view) {
        this.view = view;
    }

    public void handleStartMenuButtons() {
        if (view != null) {
            if (view.getLoginButton().isChecked()) {
                Main.getMain().getScreen().dispose();
                Main.getMain().setScreen(new LoginMenu(new LoginController(), GameAssetManager.getGameAssetManager().getSkin()));
            }

            if (view.getSignupButton().isChecked()) {
                Main.getMain().getScreen().dispose();
                Main.getMain().setScreen(new RegisterMenu(new RegisterController(), GameAssetManager.getGameAssetManager().getSkin()));
            }

            if (view.getGuestButton().isPressed()) {
                Main.getMain().getScreen().dispose();
                Main.getMain().setScreen(new PregameMenu(new PregameController(null), GameAssetManager.getGameAssetManager().getSkin()));
            }
        }
    }
}
