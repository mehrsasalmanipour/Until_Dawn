package com.twentyminutestilldawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class SaveManager {
    public static void saveGame(SaveData saveData) {
        Json json = new Json();
        FileHandle file = Gdx.files.local("");
        file.writeString(json.prettyPrint(saveData), false);
    }

    public static SaveData loadGame() {
        FileHandle file = Gdx.files.local("save.json");
        if (!file.exists()) return null;

        Json json = new Json();
        return json.fromJson(SaveData.class, file.readString());
    }
}
