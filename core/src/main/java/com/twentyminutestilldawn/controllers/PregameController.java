package com.twentyminutestilldawn.controllers;

import com.twentyminutestilldawn.Main;
import com.twentyminutestilldawn.models.GameCharacter;
import com.twentyminutestilldawn.models.GameAssetManager;
import com.twentyminutestilldawn.models.User;
import com.twentyminutestilldawn.models.Weapon;
import com.twentyminutestilldawn.views.GameView;
import com.twentyminutestilldawn.views.PregameMenu;

import java.util.List;

public class PregameController {
    private PregameMenu view;
    private GameCharacter selectedCharacter;
    private Weapon selectedWeapon;
    private final User user;

    public void setView(PregameMenu view) {
        this.view = view;
    }

    public PregameController(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public List<GameCharacter> getAllCharacters() {
        return GameAssetManager.getGameAssetManager().getAllCharacters();
    }

    public List<Weapon> getAllWeapons() {
        return GameAssetManager.getGameAssetManager().getAllWeapons();
    }

    public void selectCharacter(GameCharacter character) {
        this.selectedCharacter = character;
        view.updateCharacterDetails(character);
    }

    public void selectWeapon(Weapon weapon) {
        this.selectedWeapon = weapon;
        view.updateWeaponDetails(weapon);
    }

    public GameCharacter getSelectedCharacter() {
        return selectedCharacter;
    }

    public Weapon getSelectedWeapon() {
        return selectedWeapon;
    }

    public void onPlayClicked() {
        GameCharacter character = getSelectedCharacter();
        Weapon weapon = getSelectedWeapon();
        int minutes = view.getSelectedDurationMinutes();
        int seconds = minutes * 60;
        Main.getMain().setScreen(new GameView(user, character, weapon, seconds, GameAssetManager.getGameAssetManager().getSkin()));
    }
}
