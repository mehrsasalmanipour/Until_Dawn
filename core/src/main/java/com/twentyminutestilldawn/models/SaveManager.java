package com.twentyminutestilldawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class SaveManager {
    private static final String SAVE_FILE = "saves.json";

    public static void saveGame(User user, SaveData newSaveData) {
        Json json = new Json();
        FileHandle file = Gdx.files.local(SAVE_FILE);

        SaveDataMap saveMap;
        if (file.exists()) {
            saveMap = json.fromJson(SaveDataMap.class, file.readString());
        } else {
            saveMap = new SaveDataMap();
        }

        saveMap.saves.put(user.getUsername(), newSaveData);
        file.writeString(json.prettyPrint(saveMap), false);
    }

    public static SaveData loadGame(User user) {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        if (!file.exists()) return null;

        Json json = new Json();
        SaveDataMap saveMap = json.fromJson(SaveDataMap.class, file.readString());

        return saveMap.saves.get(user.getUsername());
    }

    public static boolean hasSave(User user) {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        if (!file.exists()) return false;

        Json json = new Json();
        SaveDataMap saveMap = json.fromJson(SaveDataMap.class, file.readString());

        return saveMap.saves.containsKey(user.getUsername());
    }

    private static Json createJson() {
        Json json = new Json();
        json.setElementType(SaveData.class, "monsters",      MonsterData.class);
        json.setElementType(SaveData.class, "bullets",       BulletData.class);
        json.setElementType(SaveData.class, "enemyBullets",  BulletData.class);
        json.setElementType(SaveData.class, "seeds",         SeedData.class);

        json.setIgnoreUnknownFields(true);
        return json;
    }
}
