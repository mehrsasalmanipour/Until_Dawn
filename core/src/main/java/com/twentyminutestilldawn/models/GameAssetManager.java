package com.twentyminutestilldawn.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.files.FileHandle;

import java.util.*;

public class GameAssetManager {
    private static GameAssetManager instance;

    private final Skin skin;

    private final List<Texture> avatars = new ArrayList<>();
    private final Map<String, Texture> portraits = new HashMap<>();
    private final Map<String, Animation<TextureRegion>> idleAnimations = new HashMap<>();
    private final Map<String, Animation<TextureRegion>> walkAnimations = new HashMap<>();
    private final Map<String, Texture> weaponIcons = new HashMap<>();
    private final Map<String, Animation<TextureRegion>> weaponReloadAnimations = new HashMap<>();
    public Map<MonsterType, Animation<TextureRegion>> monsterWalkAnimations = new HashMap<>();
    private final Map<AbilityType, TextureRegion> abilityIcons = new EnumMap<>(AbilityType.class);
    private Texture deadHeartTexture;
    private Animation<TextureRegion> heartAnimation;
    private Texture bulletTexture;
    private TextureRegion seedRegion;
    private Texture whitePixel;

    private GameAssetManager() {
        skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

        for (int i = 0; i < 4; i++) {
            Texture avatar = new Texture(Gdx.files.internal("Avatar/avatar_" + i + ".png"));
            avatars.add(avatar);
        }

        bulletTexture = new Texture(Gdx.files.internal("Weapon/bullet.png"));

        Texture seedTex = new Texture(Gdx.files.internal("Monster/T_DragonEgg.png"));
        seedRegion = new TextureRegion(seedTex);

        whitePixel = new Texture(Gdx.files.internal("ui/white_pixel.png"));

        loadCharacterAssets("Shana", 4, 4);
        loadCharacterAssets("Diamond", 7, 1);
        loadCharacterAssets("Scarlet", 3, 5);
        loadCharacterAssets("Lilith", 5, 3);
        loadCharacterAssets("Dasher", 2, 10);


        loadWeaponAssets("DualSMG");
        loadWeaponAssets("Shotgun");
        loadWeaponAssets("Revolver");

        loadMonsterAssets("Tree", MonsterType.TREE);
        loadMonsterAssets("Tentacle", MonsterType.TENTACLE);
        loadMonsterAssets("Eyebat", MonsterType.EYEBAT);
        loadMonsterAssets("Elder", MonsterType.ELDER);

        loadHeartAssets();

        for (AbilityType ability : AbilityType.values()) {
            String fileName = "Ability/" + ability.name() + ".png";
            Texture t = new Texture(Gdx.files.internal(fileName));
            abilityIcons.put(ability, new TextureRegion(t));
        }

    }

    public static GameAssetManager getGameAssetManager() {
        if (instance == null) {
            instance = new GameAssetManager();
        }
        return instance;
    }

    public Skin getSkin() {
        return skin;
    }

    public String getRandomAvatarPath() {
        int index = (int)(Math.random() * 4);
        return "Avatar/avatar_" + index + ".png";
    }

    public List<Texture> getAllAvatars() {
        return avatars;
    }

    public List<GameCharacter> getAllCharacters() {
        List<GameCharacter> characters = new ArrayList<>();

        if (portraits.containsKey("Shana")) {
            characters.add(new GameCharacter(
                "Shana", "Shana", 4, 4,
                portraits.get("Shana"),
                idleAnimations.get("Shana"),
                walkAnimations.get("Shana")
            ));
        }

        if (portraits.containsKey("Diamond")) {
            characters.add(new GameCharacter(
                "Diamond", "Diamond", 7, 1,
                portraits.get("Diamond"),
                idleAnimations.get("Diamond"),
                walkAnimations.get("Diamond")
            ));
        }

        if (portraits.containsKey("Scarlet")) {
            characters.add(new GameCharacter(
                    "Scarlet", "Scarlet", 3, 5,
                    portraits.get("Scarlet"),
                    idleAnimations.get("Scarlet"),
                    walkAnimations.get("Scarlet")
            ));
        }

        if (portraits.containsKey("Lilith")) {
            characters.add(new GameCharacter(
                    "Lilith", "Lilith", 5, 3,
                    portraits.get("Lilith"),
                    idleAnimations.get("Lilith"),
                    walkAnimations.get("Lilith")
            ));
        }

        if (portraits.containsKey("Dasher")) {
            characters.add(new GameCharacter(
                    "Dasher", "Dasher", 2, 10,
                    portraits.get("Dasher"),
                    idleAnimations.get("Dasher"),
                    walkAnimations.get("Dasher")
            ));
        }

        return characters;
    }

    public GameCharacter getCharacterById(String id) {
        Texture portrait = portraits.get(id);
        Animation<TextureRegion> idle = idleAnimations.get(id);
        Animation<TextureRegion> walk = walkAnimations.get(id);

        int hp = 4;
        int speed = 4;

        switch (id) {
            case "Diamond":
                hp = 7; speed = 1; break;
            case "Scarlet":
                hp = 3; speed = 5; break;
            case "Lilith":
                hp = 5; speed = 3; break;
            case "Dasher":
                hp = 2; speed = 10; break;
            case "Shana":
            default:
                hp = 4; speed = 4; break;
        }

        if (portrait != null && idle != null && walk != null) {
            return new GameCharacter(id, id, hp, speed, portrait, idle, walk);
        }

        return null;
    }


    private void loadCharacterAssets(String id, int hp, int speed) {
        String folder = "Hero/" + id;
        String portraitPath = folder + "/T_" + id + "_Portrait.png";

        Texture portrait = new Texture(Gdx.files.internal(portraitPath));
        portraits.put(id, portrait);

        int idleCount = countFrames(folder, "Idle_");
        int walkCount = countFrames(folder, "Walk_");

        idleAnimations.put(id, loadAnimation(folder + "/Idle_", idleCount));
        walkAnimations.put(id, loadAnimation(folder + "/Walk_", walkCount));
    }

    public List<Weapon> getAllWeapons() {
        List<Weapon> weapons = new ArrayList<>();

        weapons.add(new Weapon(
            "Revolver", "Revolver",
            20, 1, 6, 1f,
            weaponIcons.get("Revolver"),
            weaponReloadAnimations.get("Revolver")
        ));

        weapons.add(new Weapon(
            "Shotgun", "Shotgun",
            10, 4, 2, 1f,
            weaponIcons.get("Shotgun"),
            weaponReloadAnimations.get("Shotgun")
        ));

        weapons.add(new Weapon(
            "DualSMG", "SMGs Dual",
            8, 1, 24, 2f,
            weaponIcons.get("DualSMG"),
            weaponReloadAnimations.get("DualSMG")
        ));

        return weapons;
    }

    public Weapon getWeaponById(String id) {
        switch (id) {
            case "Revolver":
                return new Weapon(
                        "Revolver", "Revolver",
                        20, 1, 6, 1f,
                        weaponIcons.get("Revolver"),
                        weaponReloadAnimations.get("Revolver")
                );
            case "Shotgun":
                return new Weapon(
                        "Shotgun", "Shotgun",
                        10, 4, 2, 1f,
                        weaponIcons.get("Shotgun"),
                        weaponReloadAnimations.get("Shotgun")
                );
            case "DualSMG":
                return new Weapon(
                        "DualSMG", "SMGs Dual",
                        8, 1, 24, 2f,
                        weaponIcons.get("DualSMG"),
                        weaponReloadAnimations.get("DualSMG")
                );
            default:
                return null;
        }
    }

    private void loadWeaponAssets(String id) {
        String iconPath = "Weapon/" + id + "/" + id + "Still.png";
        weaponIcons.put(id, new Texture(Gdx.files.internal(iconPath)));

        String reloadPrefix = id + "Reload_";
        int reloadFrames = countFrames("Weapon/" + id, reloadPrefix);

        if (reloadFrames > 0) {
            weaponReloadAnimations.put(id, loadAnimation("Weapon/" + id + "/" + reloadPrefix, reloadFrames));
        }
    }

    private void loadMonsterAssets(String id, MonsterType type) {
        int frameCount = countFrames("Monster/" + id, id + "_");

        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++) {
            Texture tex = new Texture(Gdx.files.internal("Monster/" + id + "/" + id + "_" + i + ".png"));
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            frames.add(new TextureRegion(tex));
        }

        monsterWalkAnimations.put(type, new Animation<>(0.1f, frames, Animation.PlayMode.LOOP));
    }

    private void loadHeartAssets() {
        deadHeartTexture = new Texture(Gdx.files.internal("Heart/DeadHeart.png"));
        deadHeartTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        int frameCount = countFrames("Heart", "HeartAnimation_");

        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++) {
            String path = "Heart/HeartAnimation_" + i + ".png";
            Texture tex = new Texture(Gdx.files.internal(path));
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

            frames.add(new TextureRegion(tex));
        }

        heartAnimation = new Animation<>(0.2f, frames, Animation.PlayMode.LOOP);
    }

    public Animation<TextureRegion> getHeartAnimation() {
        return heartAnimation;
    }

    public Texture getDeadHeartTexture() {
        return deadHeartTexture;
    }

    public Texture getBulletTexture() {
        return bulletTexture;
    }

    public TextureRegion getSeedRegion() {
        return seedRegion;
    }

    public TextureRegion getAbilityIcon(AbilityType type) {
        return abilityIcons.get(type);
    }

    public Texture  getWhitePixel() {
        return whitePixel;
    }

    private Animation<TextureRegion> loadAnimation(String pathPrefix, int frameCount) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++) {
            Texture tex = new Texture(Gdx.files.internal(pathPrefix + i + ".png"));
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            frames.add(new TextureRegion(tex));
        }
        return new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
    }

    private int countFrames(String folderPath, String prefix) {
        FileHandle dir = Gdx.files.internal(folderPath);
        int count = 0;
        while (dir.child(prefix + count + ".png").exists()) {
            count++;
        }
        return count;
    }

}
