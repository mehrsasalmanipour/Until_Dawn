package com.twentyminutestilldawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.twentyminutestilldawn.Main;
import com.twentyminutestilldawn.controllers.MainMenuController;
import com.twentyminutestilldawn.controllers.TalentController;
import com.twentyminutestilldawn.models.GameAssetManager;
import com.twentyminutestilldawn.models.GameCharacter;

public class TalentMenu implements Screen {

    private final Stage stage;
    private final TalentController controller;
    private final Skin skin;
    private final Table table;

    private final Label title;
    private final TextButton keyBindingsButton;
    private final TextButton cheatCodeButton;
    private final TextButton abilityButton;
    private final TextButton backButton;

    public TalentMenu(TalentController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.top().padTop(40);

        title = new Label("Talent Menu", skin, "title");
        title.setFontScale(1.4f);
        title.setColor(Color.GOLD);
        table.add(title).padBottom(30).colspan(3).center().row();

        Table heroTable = new Table();

        java.util.List<GameCharacter> allCharacters = GameAssetManager.getGameAssetManager().getAllCharacters();
        for (GameCharacter character : allCharacters) {
            addHeroCard(heroTable, character);
        }

        ScrollPane scrollPane = new ScrollPane(heroTable, skin);
        scrollPane.setFadeScrollBars(false);
        table.add(scrollPane).colspan(3).height(300).width(700).padBottom(20).row();

        keyBindingsButton = new TextButton("Key Bindings", skin);
        cheatCodeButton = new TextButton("Cheat Codes", skin);
        abilityButton = new TextButton("Abilities", skin);
        backButton = new TextButton("Back", skin);

        table.add(keyBindingsButton).width(220).pad(10);
        table.add(cheatCodeButton).width(220).pad(10);
        table.add(abilityButton).width(220).pad(10).row();
        table.add(backButton).width(150).pad(15).left().expand().bottom().colspan(3).row();

        stage.addActor(table);
        addButtonListeners();
    }

    private void addHeroCard(Table target, GameCharacter character) {
        Table card = new Table();

        Image img = new Image(character.getPortrait());
        img.setSize(100, 100);
        card.add(img).size(100).row();

        card.add(new Label(character.getName(), skin)).padTop(5).row();
        card.add(new Label("HP: " + character.getMaxHp(), skin)).row();
        card.add(new Label("Speed: " + character.getSpeed(), skin)).row();

        target.add(card).pad(10);
    }

    private void addButtonListeners() {
        keyBindingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                showDialog("Key Bindings", "WASD to move\n" +
                                "Mouse to aim\n" +
                                "Left-click to shoot"
                );
            }
        });

        cheatCodeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                showDialog("Cheat Codes",
                        "reduce_time: Set time to 60 seconds left\n" +
                                "boss_fight: Jump to halfway point (spawn Elder)\n" +
                                "level_up: Instantly level up and choose an ability\n" +
                                "heal: Restore 1 HP if not full"
                );
            }
        });

        abilityButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                showDialog("Abilities",
                        "VITALITY: +1 Max HP\n" +
                                "DAMAGER: +25% weapon damage for 10 seconds\n" +
                                "PROCREASE: +1 projectile\n" +
                                "AMOCREASE: +5 max ammo\n" +
                                "SPEEDY: 2x movement speed for 10 seconds"
                );
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                Main.getMain().setScreen(new MainMenu(new MainMenuController(controller.getUser()), GameAssetManager.getGameAssetManager().getSkin(), controller.getUser()));
            }
        });
    }

    private void showDialog(String title, String content) {
        Dialog dialog = new Dialog(title, skin);
        dialog.setMovable(false);
        dialog.getContentTable().pad(12);

        Label label = new Label(content, skin);
        label.setWrap(true);
        label.setAlignment(Align.left);

        dialog.getContentTable().add(label).width(400).left();
        dialog.button("OK").padTop(10);
        dialog.key(com.badlogic.gdx.Input.Keys.ESCAPE, false);
        dialog.show(stage);
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }

    @Override public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.07f, 0.1f, 1);
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
    }
}