package com.twentyminutestilldawn.models;

import java.util.ArrayList;
import java.util.List;

public class SaveData {
    public String characterId;
    public float characterX, characterY;
    public String weaponId;
    public int level;
    public int currentHp, maxHp;
    public int xp;
    public float elapsedTime;
    public List<String> abilities = new ArrayList<>();

    public List<MonsterData> monsters = new ArrayList<>();
    public List<BulletData> bullets = new ArrayList<>();
    public List<BulletData> enemyBullets = new ArrayList<>();
    public List<SeedData> seeds = new ArrayList<>();
}

