package com.twentyminutestilldawn.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.twentyminutestilldawn.Main;
import com.twentyminutestilldawn.models.*;
import com.twentyminutestilldawn.views.GameView;
import com.twentyminutestilldawn.views.MainMenu;
import com.twentyminutestilldawn.views.StartMenu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameController {
    private final User user;

    private final float MAP_WIDTH = 1280 * 2;
    private final float MAP_HEIGHT = 720 * 2;

    private final GameCharacter character;
    private final OrthographicCamera camera;
    private WeaponController weaponController;
    private final MonsterController monsterController;

    private float stateTime = 0f;
    private boolean isMoving = false;
    private boolean flipCharacter = false;

    private float monsterStateTime = 0f;
    private final List<Seed> seeds = new ArrayList<>();

    private boolean showAbilityMenu = false;
    private boolean showPauseMenu = false;
    private final List<AbilityType> availableChoices = new ArrayList<>(3);
    private final List<AbilityType> gainedAbilities = new ArrayList<>();

    private final int totalGameLengthSeconds;
    private float elapsedTime = 0f;

    private boolean godModeEnabled = false;
    private boolean gameOver = false;
    private GameEndReason endReason = null;

    private GameView gameView;

    public void setView(GameView view) {
        this.gameView = view;
    }

    public GameController(User user, GameCharacter character, Weapon weapon, int totalGameLengthSeconds, OrthographicCamera camera) {
        this.user = user;
        this.character = character;
        this.weaponController = new WeaponController(weapon, camera);
        this.totalGameLengthSeconds = totalGameLengthSeconds;
        this.camera = camera;
        this.character.setPosition(MAP_WIDTH / 2f, MAP_HEIGHT / 2f);
        this.monsterController = new MonsterController(totalGameLengthSeconds);
        this.character.setLevelUpListener(new GameCharacter.LevelUpListener() {
            @Override
            public void onLevelUp(int newLevel) {
                showAbilityMenu = true;
                pickThreeRandomAbilities();
            }
        });
    }

    public void update(float delta, Vector2 weaponTipPosition) {
        if (showAbilityMenu || showPauseMenu) {
            return;
        }

        if (character.getCurrentHp() <= 0) {
            gameOver = true;
            endReason = GameEndReason.DEAD;
            return;
        }

        if (getRemainingTimeSeconds() <= 0) {
            gameOver = true;
            endReason = GameEndReason.WIN;
            return;
        }

        elapsedTime += delta;
        stateTime += delta;
        monsterStateTime  += delta;

        monsterController.setGameTime(elapsedTime);

        character.updateSpeedBuff(delta);
        float speed = 50 + character.getEffectiveSpeed() * 20;
        isMoving = false;
        Vector2 direction = new Vector2();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) direction.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) direction.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) direction.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) direction.x += 1;
        flipCharacter = Gdx.input.isKeyPressed(Input.Keys.A);

        Vector2 playerPosition = character.getPosition();
        if (!direction.isZero()) {
            direction.nor().scl(speed * delta);
            character.addToPosition(direction);
            isMoving = true;
        }

        TextureRegion idleFrame = character.animatedIdlePortrait.getKeyFrame(0f, true);
        float halfWidth  = idleFrame.getRegionWidth()  / 2f;
        float halfHeight = idleFrame.getRegionHeight() / 2f;
        playerPosition.x = Math.max(halfWidth,  Math.min(MAP_WIDTH  - halfWidth,  playerPosition.x));
        playerPosition.y = Math.max(halfHeight, Math.min(MAP_HEIGHT - halfHeight, playerPosition.y));

        monsterController.update(delta, playerPosition);

        weaponController.setWeaponTip(weaponTipPosition);
        weaponController.update(delta);

        handleBulletMonsterCollisions();

        TextureRegion pFrame = character.animatedIdlePortrait.getKeyFrame(0f, true);
        float pW = pFrame.getRegionWidth() * 2f;
        float pH = pFrame.getRegionHeight() * 2f;
        float pX = playerPosition.x - pW / 2f;
        float pY = playerPosition.y - pH / 2f;

        Iterator<Seed> sit = seeds.iterator();
        while (sit.hasNext()) {
            Seed s = sit.next();

            float sW = s.region.getRegionWidth();
            float sH = s.region.getRegionHeight();
            float sX = s.position.x - sW / 2f;
            float sY = s.position.y - sH / 2f;

            boolean overlapX = (pX < sX + sW) && (pX + pW > sX);
            boolean overlapY = (pY < sY + sH) && (pY + pH > sY);

            if (overlapX && overlapY) {
                character.addXp(3);
                sit.remove();
            }
        }

        character.updateInvincibility(delta);

        List<Monster> allMonsters = monsterController.getMonsters();
        for (Monster m : allMonsters) {
            if (m.type == MonsterType.EYEBAT) {
                m.updateEyebatTimer(delta);

                if (m.canEyebatShoot()) {
                    Vector2 spawn = m.position;
                    Vector2 aim   = playerPosition;

                    Bullet eb = new Bullet(
                            spawn,
                            aim,
                            GameAssetManager.getGameAssetManager().getBulletTexture(),
                            1
                    );

                    weaponController.getEnemyBullets().add(eb);
                    m.resetEybatCooldown();
                }
            }
            else if (m.type == MonsterType.ELDER) {
                m.updateElderTimer(delta);

                if (m.canElderShoot()) {
                    Vector2 spawn = m.position;
                    Vector2 aim   = playerPosition;

                    Bullet bossBullet = new Bullet(
                            spawn,
                            aim,
                            GameAssetManager.getGameAssetManager().getBulletTexture(),
                            1
                    );

                    weaponController.getEnemyBullets().add(bossBullet);
                    m.resetElderCooldown();
                }
            }
        }

        for (Monster m : allMonsters) {
            TextureRegion mFrame = m.walkAnimation.getKeyFrame(monsterStateTime, true);
            float mWidth  = mFrame.getRegionWidth();
            float mHeight = mFrame.getRegionHeight();
            float mX = m.position.x - mWidth  / 2f;
            float mY = m.position.y - mHeight / 2f;

            boolean overlapX = (pX < mX + mWidth) && (pX + pW > mX);
            boolean overlapY = (pY < mY + mHeight) && (pY + pH > mY);

            if (overlapX && overlapY) {
                if (!character.isInvincible()) {
                    character.takeDamage(1);
                    character.setInvincible(1.0f);
                }
            }
        }

        handleEnemyBulletPlayerCollisions();
    }

    public GameCharacter getCharacter() {
        return character;
    }

    public boolean isFlippingLeft() {
        return flipCharacter;
    }

    public TextureRegion getCurrentPlayerFrame() {
        if (isMoving) {
            return character.animatedWalkPortrait.getKeyFrame(stateTime, true);
        } else {
            return character.animatedIdlePortrait.getKeyFrame(stateTime, true);
        }
    }

    public WeaponController getWeaponController () {
        return weaponController;
    }

    public MonsterController getMonsterController () {
        return monsterController;
    }

    private void handleBulletMonsterCollisions() {
        List<Bullet>  bullets  = weaponController.getBullets();
        List<Monster> monsters = monsterController.getMonsters();

        Iterator<Bullet> bit = bullets.iterator();
        while (bit.hasNext()) {
            Bullet b = bit.next();
            float bX = b.sprite.getX();
            float bY = b.sprite.getY();
            float bW = b.sprite.getWidth();
            float bH = b.sprite.getHeight();

            if (bX + bW < 0 || bX > MAP_WIDTH || bY + bH < 0 || bY > MAP_HEIGHT) {
                bit.remove();
                continue;
            }

            boolean bulletRemoved = false;

            Iterator<Monster> mit = monsters.iterator();
            while (mit.hasNext()) {
                Monster m = mit.next();

                TextureRegion mFrame = m.walkAnimation.getKeyFrame(monsterStateTime, true);
                float mWidth  = mFrame.getRegionWidth();
                float mHeight = mFrame.getRegionHeight();
                float mX = m.position.x - mWidth  / 2f;
                float mY = m.position.y - mHeight / 2f;

                boolean overlapX = (bX < mX + mWidth) && (bX + bW > mX);
                boolean overlapY = (bY < mY + mHeight) && (bY + bH > mY);

                if (overlapX && overlapY) {
                    m.takeDamage(b.damage);

                    bit.remove();
                    bulletRemoved = true;

                    if (m.isDead()) {
                        Vector2 seedPos = new Vector2(m.position.x, m.position.y);
                        TextureRegion seedRegion = GameAssetManager
                                .getGameAssetManager()
                                .getSeedRegion();
                        seeds.add(new Seed(seedPos, seedRegion));

                        mit.remove();
                    }
                    break;
                }
            }

            if (bulletRemoved) {
                continue;
            }
        }
    }

    private void handleEnemyBulletPlayerCollisions() {
        List<Bullet>  enemyBullets = weaponController.getEnemyBullets();
        Vector2 playerPos = character.getPosition();

        TextureRegion pFrame = character.animatedIdlePortrait.getKeyFrame(0f, true);
        float pW = pFrame.getRegionWidth()  * 2f;
        float pH = pFrame.getRegionHeight() * 2f;
        float pX = playerPos.x - pW / 2f;
        float pY = playerPos.y - pH / 2f;

        Iterator<Bullet> ebit = enemyBullets.iterator();
        while (ebit.hasNext()) {
            Bullet eb = ebit.next();

            float bX = eb.sprite.getX();
            float bY = eb.sprite.getY();
            float bW = eb.sprite.getWidth();
            float bH = eb.sprite.getHeight();

            boolean overlapX = (bX < pX + pW) && (bX + bW > pX);
            boolean overlapY = (bY < pY + pH) && (bY + bH > pY);

            if (overlapX && overlapY) {
                if (!character.isInvincible() && !godModeEnabled) {
                    character.takeDamage(1);
                    character.setInvincible(1.0f);
                }

                ebit.remove();
            }
        }
    }

    public List<Seed> getSeeds() {
        return seeds;
    }

    private void pickThreeRandomAbilities() {
        availableChoices.clear();
        AbilityType[] all = AbilityType.values();

        for (int i = all.length - 1; i > 0; i--) {
            int rnd = (int)(Math.random() * (i + 1));
            AbilityType tmp = all[i];
            all[i] = all[rnd];
            all[rnd] = tmp;
        }

        for (int i = 0; i < 3 && i < all.length; i++) {
            availableChoices.add(all[i]);
        }
    }

    public void applyAbility(AbilityType ability) {
        if (!gainedAbilities.contains(ability)) gainedAbilities.add(ability);

        switch (ability) {
            case VITALITY:
                character.addVitalityPoint();
                break;

            case DAMAGER:
                weaponController.applyTemporaryDamageMultiplier(1.25f, 10f);
                break;

            case PROCREASE:
                weaponController.increaseProjectileBonus(1);
                break;

            case AMOCREASE:
                weaponController.increaseMaxAmmoBy(5);
                break;

            case SPEEDY:
                character.applyTemporarySpeedMultiplier(2f, 10f);
                break;
        }

        showAbilityMenu = false;
    }

    public boolean isShowingAbilityMenu() {
        return showAbilityMenu;
    }

    public List<AbilityType> getGainedAbilities() {
        return gainedAbilities;
    }

    public void handlePauseButton() {
        showPauseMenu = true;
        if (gameView != null) {
            gameView.showPauseMenu();
        }
    }

    public void resumeGame() {
        showPauseMenu = false;
        if (gameView != null) {
            gameView.hidePauseMenu();
        }
    }

    public void handleGiveUp() {
        gameOver = true;
        endReason = GameEndReason.GIVE_UP;
        if (user != null) {
            Gdx.app.postRunnable(() -> {
                MainMenu mainMenu = new MainMenu(new MainMenuController(user), GameAssetManager.getGameAssetManager().getSkin(), user);
                Main.getMain().setScreen(mainMenu);
            });
        } else {
            Gdx.app.postRunnable(() -> {
                StartMenuController startMenuController = new StartMenuController();
                StartMenu startMenu = new StartMenu(startMenuController, GameAssetManager.getGameAssetManager().getSkin());
                Main.getMain().setScreen(startMenu);
            });
        }
    }

    public boolean isShowingPauseMenu() {
        return showPauseMenu;
    }

    public void setShowPauseMenu(boolean showPauseMenu) {
        this.showPauseMenu = showPauseMenu;
    }

    public List<AbilityType> getAvailableChoices() {
        return availableChoices;
    }

    public int getRemainingTimeSeconds() {
        return Math.max(0, totalGameLengthSeconds - (int) elapsedTime);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public GameEndReason getEndReason() {
        return endReason;
    }

    public User getUser() {
        return user;
    }

    public void cheatReduceTime() {
        this.elapsedTime = Math.min(totalGameLengthSeconds, this.elapsedTime + 60);
    }

    public void cheatLevelUp() {
        character.addXp(character.xpToNextLevel());

        showAbilityMenu = true;
        pickThreeRandomAbilities();
    }

    public void cheatReviveIfDead() {
        if (character.getCurrentHp() < character.getMaxHp()) {
            character.heal(1);
        }
    }

    public void cheatBossFight() {
        this.elapsedTime = totalGameLengthSeconds / 2f;
    }
}

