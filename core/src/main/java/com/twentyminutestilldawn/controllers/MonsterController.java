package com.twentyminutestilldawn.controllers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.twentyminutestilldawn.models.GameAssetManager;
import com.twentyminutestilldawn.models.Monster;
import com.twentyminutestilldawn.models.MonsterType;

import java.util.ArrayList;
import java.util.List;

public class MonsterController {
    private final float MAP_WIDTH = 1280 * 2;
    private final float MAP_HEIGHT = 720 * 2;

    private final List<Monster> monsters = new ArrayList<>();

    private float gameTime = 0;
    private final float totalGameLength;

    private float lastTentacleSpawnTime = 0f;
    private float lastEyebatSpawnTime = 0f;
    private boolean elderSpawned = false;

    public MonsterController(float totalGameLength) {
        this.gameTime = gameTime;
        this.totalGameLength = totalGameLength;
    }

    public void spawnInitialTrees(int count) {
        for (int i = 0; i < count; i++) {
            float x = MathUtils.random(0, MAP_WIDTH);
            float y = MathUtils.random(0, MAP_HEIGHT);
            Vector2 position = new Vector2(x, y);

            Monster tree = new Monster(MonsterType.TREE, position, GameAssetManager.getGameAssetManager().monsterWalkAnimations.get(MonsterType.TREE), 1000);
            monsters.add(tree);
        }
    }

    public void update(float delta, Vector2 playerPosition) {
        gameTime += delta;

        for (Monster m : monsters) {
            if (m.type == MonsterType.TENTACLE) {
                m.moveTowards(playerPosition, 70f, delta);
            }

            if (m.type == MonsterType.EYEBAT) {
                m.moveTowards(playerPosition, 80f, delta);
            }

            if (m.type == MonsterType.ELDER) {
                m.moveTowards(playerPosition, 100f, delta);
            }
        }

        if (gameTime - lastTentacleSpawnTime >= 3f) {
            int toSpawn = (int)(gameTime / 30f);
            for (int i = 0; i < toSpawn; i++) {
                spawnTentacle();
            }
            lastTentacleSpawnTime = gameTime;
        }

        if (gameTime >= totalGameLength / 4f) {
            float sinceStartSpawning = gameTime - (totalGameLength / 4f);
            float intervalsPassed    = (float) Math.floor(sinceStartSpawning / 10f);
            float spawnTime = (totalGameLength / 4f) + intervalsPassed * 10f;

            if (spawnTime > lastEyebatSpawnTime) {
                float i = gameTime;
                float numerator = 4f * i - totalGameLength + 30f;
                int count = Math.max(0, (int) Math.floor(numerator / 30f));

                for (int k = 0; k < count; k++) {
                    spawnEyebat();
                }
                lastEyebatSpawnTime = spawnTime;
            }
        }

        if (!elderSpawned && gameTime >= totalGameLength / 2f) {
            spawnElderBoss();
            elderSpawned = true;
        }
    }

    private void spawnTentacle() {
        float x = MathUtils.random(0, MAP_WIDTH);
        float y = MathUtils.random(0, MAP_HEIGHT);
        Vector2 position = new Vector2(x, y);

        Monster tentacle = new Monster(MonsterType.TENTACLE, position, GameAssetManager.getGameAssetManager().monsterWalkAnimations.get(MonsterType.TENTACLE), 25);
        monsters.add(tentacle);
    }

    private void spawnEyebat() {
        float x = MathUtils.random(0, MAP_WIDTH);
        float y = MathUtils.random(0, MAP_HEIGHT);
        Vector2 pos = new Vector2(x, y);

        Monster eyebat = new Monster(
                MonsterType.EYEBAT,
                pos,
                GameAssetManager.getGameAssetManager().monsterWalkAnimations.get(MonsterType.EYEBAT),
                50
        );
        monsters.add(eyebat);
    }

    private void spawnElderBoss() {
        float x = MathUtils.random(0, MAP_WIDTH);
        float y = MathUtils.random(0, MAP_HEIGHT);
        Vector2 pos = new Vector2(x, y);

        Monster elder = new Monster(
                MonsterType.ELDER,
                pos,
                GameAssetManager.getGameAssetManager().monsterWalkAnimations.get(MonsterType.ELDER),
                400
        );
        monsters.add(elder);
    }

    public void setGameTime(float time) {
        this.gameTime = time;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }
}
