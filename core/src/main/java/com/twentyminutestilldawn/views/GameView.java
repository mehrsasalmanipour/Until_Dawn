package com.twentyminutestilldawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.twentyminutestilldawn.Main;
import com.twentyminutestilldawn.controllers.GameController;
import com.twentyminutestilldawn.controllers.MainMenuController;
import com.twentyminutestilldawn.controllers.StartMenuController;
import com.twentyminutestilldawn.models.*;

import java.util.List;

public class GameView implements Screen {
    private User user;
    private Texture avatarTexture;

    private final GameCharacter playerCharacter;
    private final Weapon selectedWeapon;
    private GameController gameController;

    private static final float MAP_WIDTH = 1280 * 2;
    private static final float MAP_HEIGHT = 720 * 2;

    OrthographicCamera camera = new OrthographicCamera();
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture background;

    private Stage uiStage;
    private TextButton pauseButton;

    private final int gameDurationSeconds;

    private float monsterStateTime = 0f;

    private Animation<TextureRegion> heartAnimation;
    private Texture deadHeartTexture;
    private float heartStateTime = 0f;

    BitmapFont font = new BitmapFont();

    private float[] btnX = new float[3];
    private float[] btnY = new float[3];
    private float[] btnW = new float[3];
    private float[] btnH = new float[3];

    private Window pauseWindow;
    private TextButton resumeButton;
    private TextButton cheatButton;
    private TextButton abilitiesButton;
    private TextButton giveUpButton;
    private TextButton toggleBWButton;
    private TextButton saveQuitButton;

    private Window cheatWindow;
    private TextField cheatField;
    private Label cheatHintLabel;

    private Window abilitiesWindow;

    private ShaderProgram grayscaleShader;
    private boolean useGrayscale = false;

    private boolean dialogShown = false;

    public GameView(User user, GameCharacter character, Weapon weapon, int gameDurationSeconds, Skin skin) {
        this.user = user;
        this.playerCharacter = character;
        this.selectedWeapon = weapon;
        this.gameDurationSeconds = gameDurationSeconds;
        this.pauseButton = new TextButton("Pause", skin);
    }
    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("ui/map.png"));

        ShaderProgram.pedantic = false;
        grayscaleShader = new ShaderProgram(
                Gdx.files.internal("shaders/grayscale.vs"),
                Gdx.files.internal("shaders/grayscale.fs")
        );
        if (!grayscaleShader.isCompiled()) {
            Gdx.app.error("Shader", "Compilation failed:\n" + grayscaleShader.getLog());
        }

        gameController = new GameController(user, playerCharacter, selectedWeapon, gameDurationSeconds, camera);
        gameController.setView(this);

        gameController.getMonsterController().spawnInitialTrees(5);

        heartAnimation   = GameAssetManager.getGameAssetManager().getHeartAnimation();
        deadHeartTexture = GameAssetManager.getGameAssetManager().getDeadHeartTexture();

        uiStage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(uiStage);

        pauseButton.setSize(100f, 40f);
        pauseButton.setPosition(10f, 10f);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameController != null) {
                    gameController.handlePauseButton();
                }
            }
        });
        uiStage.addActor(pauseButton);
        pauseButton.toFront();

        Skin skin = GameAssetManager.getGameAssetManager().getSkin();

        pauseWindow = new Window("Paused Menu", skin);
        pauseWindow.setVisible(false);
        pauseWindow.clear();

        resumeButton = new TextButton("Resume", skin);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gameController != null) {
                    gameController.resumeGame();
                }
            }
        });
        cheatButton = new TextButton("Cheat Codes", skin);
        cheatButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cheatField.setText("");
                pauseWindow.setVisible(false);
                cheatWindow.setVisible(true);
            }
        });
        abilitiesButton = new TextButton("Abilities", skin);
        abilitiesButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                refreshAbilitiesWindow();
                abilitiesWindow.setVisible(true);
                pauseWindow.setVisible(false);
            }
        });
        giveUpButton = new TextButton("Give Up", skin);
        giveUpButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (gameController != null) {
                    gameController.handleGiveUp();
                }
            }
        });
        toggleBWButton = new TextButton("Toggle B/W Mode", skin);
        toggleBWButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                useGrayscale = !useGrayscale;
            }
        });
        saveQuitButton = new TextButton("Save & Quit", skin);
        saveQuitButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
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
        });

        pauseWindow.defaults().pad(10).minWidth(250).minHeight(60).expandX().fillX();
        pauseWindow.add(resumeButton).row();
        pauseWindow.add(cheatButton ).row();
        pauseWindow.add(abilitiesButton).row();
        pauseWindow.add(giveUpButton ).row();
        pauseWindow.add(toggleBWButton).row();
        pauseWindow.add(saveQuitButton);

        pauseWindow.pack();
        centerPauseWindow();

        uiStage.addActor(pauseWindow);

        cheatWindow = new Window("Cheat Codes", skin);
        cheatWindow.setVisible(false);
        cheatWindow.setMovable(false);

        cheatHintLabel = new Label(
                "Available Cheats:\nreduce_time\nboss_fight\nlevel_up\nheal", skin
        );
        cheatField = new TextField("", skin);
        cheatField.setMaxLength(20);
        cheatField.setMessageText("Enter cheat code");

        TextButton applyCheat = new TextButton("Apply", skin);
        applyCheat.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleCheatCode(cheatField.getText().trim().toLowerCase());
                cheatField.setText("");
            }
        });

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cheatWindow.setVisible(false);
                pauseWindow.setVisible(true);
            }
        });

        cheatWindow.defaults().pad(10).width(250).fillX();
        cheatWindow.add(cheatHintLabel).row();
        cheatWindow.add(cheatField).row();
        cheatWindow.add(applyCheat).row();
        cheatWindow.add(backButton).row();
        cheatWindow.pack();
        cheatWindow.setPosition(
                (uiStage.getWidth() - cheatWindow.getWidth()) / 2f,
                (uiStage.getHeight() - cheatWindow.getHeight()) / 2f
        );
        uiStage.addActor(cheatWindow);

        abilitiesWindow = new Window("Your Abilities", skin);
        abilitiesWindow.setVisible(false);
        abilitiesWindow.setMovable(false);
        uiStage.addActor(abilitiesWindow);

        if (user != null && user.getAvatarPath() != null) {
            avatarTexture = new Texture(Gdx.files.internal(user.getAvatarPath()));
        } else {
            avatarTexture = new Texture(Gdx.files.internal("ui/guest.png"));
        }

    }

    private void centerPauseWindow() {
        if (pauseWindow != null && uiStage != null) {
            float winW = pauseWindow.getWidth();
            float winH = pauseWindow.getHeight();
            float stageW = uiStage.getViewport().getWorldWidth();
            float stageH = uiStage.getViewport().getWorldHeight();
            pauseWindow.setPosition((stageW - winW) / 2f, (stageH - winH) / 2f);
        }
    }

    @Override
    public void render(float delta) {
        monsterStateTime += delta;
        heartStateTime   += delta;

        Vector2 playerPos = gameController.getCharacter().getPosition();

        float camHalfWidth = camera.viewportWidth / 2f;
        float camHalfHeight = camera.viewportHeight / 2f;
        float camX = Math.max(camHalfWidth, Math.min(MAP_WIDTH - camHalfWidth, playerPos.x));
        float camY = Math.max(camHalfHeight, Math.min(MAP_HEIGHT - camHalfHeight, playerPos.y));
        camera.position.set(camX, camY, 0);
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);

        if (useGrayscale) {
            batch.setShader(grayscaleShader);
        } else {
            batch.setShader(null);
        }

        batch.begin();

        batch.draw(background, 0, 0, MAP_WIDTH, MAP_HEIGHT);

        GameCharacter player = gameController.getCharacter();
        int lvl = player.getLevel();
        int curXp = player.getXp();
        int needed = 20 * lvl;
        String xpText = String.format("Level %d   XP: %d / %d", lvl, curXp, needed);

        float halfW = camera.viewportWidth  / 2f;
        float halfH = camera.viewportHeight / 2f;

        float textX = camX - halfW + 20f;
        float textY = camY + halfH - 60f;

        font.draw(batch, xpText, textX, textY);

        int timeLeft = gameController.getRemainingTimeSeconds();
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        String timeText = String.format("Time Left: %02d:%02d", minutes, seconds);
        font.draw(batch, timeText, textX, textY - 25f);

        if (gameController.getWeaponController().isReloading()) {
            float reloadTimeLeft = gameController.getWeaponController().getReloadTimeRemaining();
            font.draw(batch,
                String.format("Reloading... %.1fs", reloadTimeLeft),
                camera.position.x - 100,
                camera.position.y + 300
            );
        } else {
            font.draw(batch,
                "Ammo: " + gameController.getWeaponController().getCurrentAmmo() +
                    " / " + gameController.getWeaponController().getMaxAmmo(),
                camera.position.x - 100,
                camera.position.y + 300
            );
        }

        TextureRegion playerFrame = gameController.getCurrentPlayerFrame();
        float playerX = playerPos.x - playerFrame.getRegionWidth() / 2f;
        float playerY = playerPos.y - playerFrame.getRegionHeight() / 2f;


        float originXX = playerFrame.getRegionWidth() / 2f;
        float originYY = playerFrame.getRegionHeight() / 2f;

        float scaleX = gameController.isFlippingLeft() ? -2f : 2f;
        float scaleY = 2f;

        boolean drawPlayer = true;
        if (playerCharacter.isInvincible()) {
            drawPlayer = ((int)(monsterStateTime * 5)) % 2 == 0;
        }

        if (drawPlayer) {
            batch.draw(
                    playerFrame,
                    playerX, playerY,
                    originXX, originYY,
                    playerFrame.getRegionWidth(),
                    playerFrame.getRegionHeight(),
                    scaleX, scaleY,
                    0f
            );
        }

        TextureRegion weaponFrame = gameController.getWeaponController().getWeaponTexture();
        float originX = weaponFrame.getRegionWidth() / 2f;
        float originY = weaponFrame.getRegionHeight() / 2f;

        float orbitRadius = 15f;

        Vector3 unprojected = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 worldMouse = new Vector2(unprojected.x, unprojected.y);
        Vector2 playerCenter = new Vector2(playerX + playerFrame.getRegionWidth() / 2f, playerY + playerFrame.getRegionHeight() / 2f);
        float angleToMouse = worldMouse.sub(playerCenter).angleRad();

        float rotation = angleToMouse * MathUtils.radiansToDegrees;

        float weaponDrawX = playerX + playerFrame.getRegionWidth() / 2f + (float) Math.cos(angleToMouse) * orbitRadius - weaponFrame.getRegionWidth() / 2f;
        float weaponDrawY = playerY + playerFrame.getRegionHeight() / 2f + (float) Math.sin(angleToMouse) * orbitRadius - weaponFrame.getRegionHeight() / 2f;

        float weaponCenterX = weaponDrawX + originX;
        float weaponCenterY = weaponDrawY + originY;

        float offsetDistance = 5f;
        Vector2 weaponTip = new Vector2(
            weaponCenterX + MathUtils.cosDeg(rotation) * offsetDistance,
            weaponCenterY + MathUtils.sinDeg(rotation) * offsetDistance
        );

        gameController.getWeaponController().setWeaponTip(weaponTip);

        gameController.update(delta, weaponTip);

        if (gameController.isGameOver()) {
            if (gameController.getEndReason() == GameEndReason.WIN) {
                showWinDialog();
            } else if (gameController.getEndReason() == GameEndReason.DEAD
                    || gameController.getEndReason() == GameEndReason.GIVE_UP) {
                showLoseDialog();
            }
        }

        for (Monster monster : gameController.getMonsterController().getMonsters()) {
            Animation<TextureRegion> anim = monster.walkAnimation;
            if (anim == null) continue;

            TextureRegion frame = anim.getKeyFrame(monsterStateTime, true);

            boolean flipX = monster.getVelocity().x < 0;
            float texW = frame.getRegionWidth();
            float texH = frame.getRegionHeight();
            float x = monster.position.x - texW / 2f;
            float y = monster.position.y - texH / 2f;

            float originXMonster = texW / 2f;
            float originYMonster = texH / 2f;
            float scaleXMonster = flipX ? -1f : 1f;
            float scaleYMonstr = 1f;
            TextureRegion frameMonster = anim.getKeyFrame(monsterStateTime, true);

            batch.draw(
                    frameMonster,
                    x,
                    y,
                    originXMonster,
                    originYMonster,
                    texW,
                    texH,
                    scaleXMonster,
                    scaleYMonstr,
                    0f
            );
        }

        for (Seed s : gameController.getSeeds()) {
            TextureRegion seedRegion = s.region;
            float w = seedRegion.getRegionWidth();
            float h = seedRegion.getRegionHeight();
            float x = s.position.x - (w / 2f);
            float y = s.position.y - (h / 2f);
            batch.draw(seedRegion, x, y, w, h);
        }

        TextureRegion frameToDraw;
        if (gameController.getWeaponController().isReloading()) {
            frameToDraw = gameController.getWeaponController().getReloadAnimationFrame();
        } else {
            frameToDraw = gameController.getWeaponController().getWeaponTexture();
        }

        if (frameToDraw != null) {
            float drawX = weaponDrawX;
            float drawY = weaponDrawY;

            float scale = gameController.getWeaponController().isReloading() ? 1.2f : 2f;

            batch.draw(
                frameToDraw,
                drawX,
                drawY,
                originX,
                originY,
                frameToDraw.getRegionWidth(),
                frameToDraw.getRegionHeight(),
                scale, scale,
                rotation
            );
        }

        List<Bullet> playerBullets = gameController.getWeaponController().getBullets();
        List<Bullet> enemyBullets  = gameController.getWeaponController().getEnemyBullets();

        boolean menuUp = gameController.isShowingAbilityMenu();
        boolean pauseUp = gameController.isShowingPauseMenu();

        for (Bullet b : playerBullets) {
            b.sprite.draw(batch);
        }

        for (Bullet eb : enemyBullets) {
            if (!menuUp && !pauseUp) {
                eb.update(delta);
            }
            eb.sprite.draw(batch);
        }

        drawPlayerHearts();

        float cameraX = camera.position.x;
        float cameraY = camera.position.y;
        float camW = camera.viewportWidth;
        float camH = camera.viewportHeight;

        float padding = 20f;

        float avatarSize = 64f;
        float avatarX = cameraX + camW / 2f - avatarSize - padding;
        float avatarY = cameraY + camH / 2f - avatarSize - padding;

        batch.draw(avatarTexture, avatarX, avatarY, avatarSize, avatarSize);

        String displayName = (user != null) ? user.getUsername() : "Guest";
        font.getData().setScale(1f);
        font.setColor(1, 1, 1, 1);
        font.draw(batch, displayName, avatarX, avatarY - 10f);

        batch.end();

        uiStage.act(delta);
        uiStage.draw();

        if (gameController.isShowingAbilityMenu()) {
            renderAbilitySelectionScreen();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (uiStage != null) {
            uiStage.getViewport().update(width, height, true);
        }
        pauseWindow.pack();
        if (pauseWindow.isVisible())   pauseWindow.center();
        if (abilitiesWindow.isVisible()) refreshAbilitiesWindow();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        font.dispose();
        if (uiStage != null) {
            uiStage.dispose();
        }
    }

    private void drawPlayerHearts() {
        GameCharacter player = gameController.getCharacter();
        int currentHp = player.getCurrentHp();
        int maxHp     = player.getMaxHp();

        float camX   = camera.position.x;
        float camY   = camera.position.y;
        float halfW  = camera.viewportWidth  / 2f;
        float halfH  = camera.viewportHeight / 2f;

        float padX = 10f;
        float padY = 10f;

        TextureRegion sampleFrame = heartAnimation.getKeyFrame(0f, true);
        float heartW = sampleFrame.getRegionWidth();
        float heartH = sampleFrame.getRegionHeight();

        float startX = camX - halfW + padX;
        float startY = camY + halfH - heartH - padY;

        for (int i = 0; i < maxHp; i++) {
            float x = startX + i * (heartW + 4f);
            float y = startY;

            if (i < currentHp) {
                TextureRegion beatFrame = heartAnimation.getKeyFrame(heartStateTime, true);
                batch.draw(beatFrame, x, y, heartW, heartH);
            } else {
                batch.draw(deadHeartTexture, x, y, heartW, heartH);
            }
        }
    }

    private void renderAbilitySelectionScreen() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        Texture whitePx = GameAssetManager.getGameAssetManager().getWhitePixel();

        batch.setColor(0f, 0f, 0f, 0.7f);
        float fullW = camera.viewportWidth;
        float fullH = camera.viewportHeight;
        float leftX = camera.position.x - fullW/2f;
        float bottomY = camera.position.y - fullH/2f;
        batch.draw(whitePx, leftX, bottomY, fullW, fullH);
        batch.setColor(1f, 1f, 1f, 1f);

        float winW = 600f;
        float winH = 400f;
        float winX = camera.position.x - winW/2f;
        float winY = camera.position.y - winH/2f;
        batch.setColor(0.2f, 0.2f, 0.2f, 0.9f);
        batch.draw(whitePx, winX, winY, winW, winH);
        batch.setColor(1f, 1f, 1f, 1f);

        font.getData().setScale(1.5f);
        font.setColor(1f, 1f, 1f, 1f);
        String title = "Choose an Ability";
        float titleX = winX + (winW/2f) - 100f;
        float titleY = winY + winH - 30f;
        font.draw(batch, title, titleX, titleY);
        font.getData().setScale(1f);

        List<AbilityType> choices = gameController.getAvailableChoices();
        float boxW = 150f;
        float boxH = 150f;
        float padding = 30f;
        float startX = winX + (winW - (3*boxW + 2*padding)) / 2f;
        float boxY   = winY + winH/2f - boxH/2f;

        for (int i = 0; i < choices.size(); i++) {
            float x = startX + i * (boxW + padding);
            float y = boxY;
            btnX[i] = x;
            btnY[i] = y;
            btnW[i] = boxW;
            btnH[i] = boxH;

            batch.setColor(0.3f, 0.3f, 0.3f, 1f);
            batch.draw(whitePx, x, y, boxW, boxH);
            batch.setColor(1f, 1f, 1f, 1f);

            TextureRegion icon = GameAssetManager.getGameAssetManager().getAbilityIcon(choices.get(i));
            if (icon != null) {
                float iconSize = 80f;
                float iconX = x + boxW/2f - iconSize/2f;
                float iconY = y + boxH - iconSize - 10f;
                batch.draw(icon, iconX, iconY, iconSize, iconSize);
            }

            font.setColor(1f,1f,1f,1f);
            float textX = x + 10f;
            float textY = y + 30f;
            font.draw(batch, choices.get(i).name(), textX, textY);
        }
        batch.end();

        if (Gdx.input.justTouched()) {
            Vector3 worldTouch = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f));
            float tx = worldTouch.x, ty = worldTouch.y;
            for (int i = 0; i < choices.size(); i++) {
                if (tx >= btnX[i] && tx <= btnX[i] + btnW[i]
                        && ty >= btnY[i] && ty <= btnY[i] + btnH[i]) {
                    gameController.applyAbility(choices.get(i));
                    break;
                }
            }
        }
    }

    public TextButton getPauseButton() {
        return pauseButton;
    }

    public void showPauseMenu() {
        if (pauseWindow != null) {
            pauseWindow.setVisible(true);
        }
    }

    public void hidePauseMenu() {
        if (pauseWindow != null) {
            pauseWindow.setVisible(false);
        }
    }

    private void refreshAbilitiesWindow() {
        abilitiesWindow.clear();

        for (AbilityType a : gameController.getGainedAbilities()) {
            abilitiesWindow.add(a.name()).left().pad(5).row();
            TextureRegion icon = GameAssetManager.getGameAssetManager().getAbilityIcon(a);
            if (icon != null) {
                abilitiesWindow.add(new Image(icon)).pad(5).left().row();
            }
        }

        TextButton backButton = new TextButton("Back", GameAssetManager.getGameAssetManager().getSkin());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                abilitiesWindow.setVisible(false);
                pauseWindow.setVisible(true);
            }
        });
        abilitiesWindow.add(backButton).padTop(15).colspan(2).center().row();

        abilitiesWindow.pack();
        float cx = uiStage.getViewport().getWorldWidth()  / 2f;
        float cy = uiStage.getViewport().getWorldHeight() / 2f;
        abilitiesWindow.setPosition(cx - abilitiesWindow.getWidth()  / 2f,
                cy - abilitiesWindow.getHeight() / 2f);
    }

    private void showWinDialog() {
        if (dialogShown) return;
        dialogShown = true;

        Dialog winDialog = new Dialog("Victory!", GameAssetManager.getGameAssetManager().getSkin()) {
            @Override
            protected void result(Object object) {
                returnToMenu();
            }
        };

        winDialog.text("You survived!\nYour score will be increased.");
        winDialog.button("OK", true);
        winDialog.show(uiStage);

        if (user != null) {
            user.addScore(1);
            UserDatabase.save();
        }
    }


    private void showLoseDialog() {
        if (dialogShown) return;
        dialogShown = true;

        Dialog loseDialog = new Dialog("Game Over", GameAssetManager.getGameAssetManager().getSkin()) {
            @Override
            protected void result(Object object) {
                returnToMenu();
            }
        };

        loseDialog.text("You died. Better luck next time!");
        loseDialog.button("OK", true);
        loseDialog.show(uiStage);
    }

    private void returnToMenu() {
        if (user != null) {
            MainMenu mainMenu = new MainMenu(new MainMenuController(user), GameAssetManager.getGameAssetManager().getSkin(), user);
            Main.getMain().setScreen(mainMenu);
        } else {
            StartMenu startMenu = new StartMenu(new StartMenuController(), GameAssetManager.getGameAssetManager().getSkin());
            Main.getMain().setScreen(startMenu);
        }
    }


    private void handleCheatCode(String code) {
        if (gameController == null) return;

        switch (code) {
            case "reduce_time":
                gameController.cheatReduceTime();
                break;
            case "boss_fight":
                gameController.cheatBossFight();
                break;
            case "level_up":
                gameController.cheatLevelUp();
                break;
            case "heal":
                gameController.cheatReviveIfDead();
                break;
            default:
                break;
        }
    }

}
