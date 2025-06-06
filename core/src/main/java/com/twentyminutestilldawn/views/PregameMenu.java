package com.twentyminutestilldawn.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.twentyminutestilldawn.controllers.PregameController;
import com.twentyminutestilldawn.models.GameCharacter;
import com.twentyminutestilldawn.models.Weapon;

import java.util.List;

public class PregameMenu implements Screen {
    private final Skin skin;
    private Stage stage;
    private final PregameController controller;

    private final Image portraitImage = new Image();
    private final Label nameLabel;
    private final Label hpLabel;
    private final Label speedLabel;
    private final Image characterSelectorBubble;

    private final Label weaponNameLabel;
    private final Label weaponStatsLabel;
    private final Image weaponIconImage;
    private final Image weaponSelectorBubble;

    private SelectBox<String> durationSelect;

    public PregameMenu(PregameController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        controller.setView(this);

        nameLabel = new Label("", skin);
        hpLabel = new Label("", skin);
        speedLabel = new Label("", skin);

        characterSelectorBubble = new Image(new Texture(Gdx.files.internal("ui/T_SelectorBubble.png")));
        characterSelectorBubble.setSize(70, 70);
        characterSelectorBubble.setVisible(false);

        weaponNameLabel = new Label("", skin);
        weaponStatsLabel = new Label("", skin);
        weaponIconImage = new Image();

        weaponSelectorBubble = new Image(new Texture(Gdx.files.internal("ui/T_SelectorBubble.png")));
        weaponSelectorBubble.setSize(70, 70);
        weaponSelectorBubble.setVisible(false);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        root.top();

        HorizontalGroup characterRow = new HorizontalGroup();
        characterRow.space(40);

        for (GameCharacter character : controller.getAllCharacters()) {
            AnimatedImage animatedIcon = new AnimatedImage(character.animatedIdlePortrait);
            animatedIcon.setSize(64, 64);
            animatedIcon.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.selectCharacter(character);
                    moveSelectorTo(characterSelectorBubble, animatedIcon);
                }
            });
            characterRow.addActor(animatedIcon);
        }

        HorizontalGroup weaponRow = new HorizontalGroup();
        weaponRow.space(40);

        for (Weapon weapon : controller.getAllWeapons()) {
            Image icon = new Image(new TextureRegionDrawable(new TextureRegion(weapon.icon)));
            icon.setSize(64, 64);
            icon.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.selectWeapon(weapon);
                    updateWeaponDetails(weapon);
                    moveSelectorTo(weaponSelectorBubble, icon);
                }
            });
            weaponRow.addActor(icon);
        }

        Table leftInfo = new Table();
        leftInfo.add(new Label("Weapon", skin)).padBottom(10).row();
        leftInfo.add(weaponIconImage).size(64, 64).padBottom(5).row();
        leftInfo.add(weaponNameLabel).pad(2).row();
        leftInfo.add(weaponStatsLabel).pad(2).row();

        Table rightInfo = new Table();
        rightInfo.add(portraitImage).size(256, 256).padBottom(10).row();
        rightInfo.add(nameLabel).pad(5).row();
        rightInfo.add(hpLabel).pad(5).row();
        rightInfo.add(speedLabel).pad(5).row();

        Table durationRow = new Table();
        Label durationLabel = new Label("Duration (min):", skin);
        durationSelect = new SelectBox<>(skin);
        durationSelect.setItems("2", "5", "10", "20");
        durationSelect.setWidth(80);
        durationRow.add(durationLabel).right().padRight(8);
        durationRow.add(durationSelect).left();

        TextButton playButton = new TextButton("Play", skin);
        playButton.pad(10);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.onPlayClicked();
            }
        });
        rightInfo.add(playButton).padTop(20).width(200).row();

        root.add(characterRow).colspan(2).center().padTop(30).row();

        Table centerPanel = new Table();
        centerPanel.add(leftInfo).expand().top().left().pad(30);
        centerPanel.add(rightInfo).expand().top().right().pad(30);

        root.add(centerPanel).expand().fill().colspan(2).row();
        root.add(weaponRow).colspan(2).center().padBottom(60).row();
        root.add(durationRow).colspan(5).center().padBottom(35).row();

        stage.addActor(root);
        stage.addActor(characterSelectorBubble);
        stage.addActor(weaponSelectorBubble);

        List<GameCharacter> characters = controller.getAllCharacters();
        if (!characters.isEmpty()) {
            GameCharacter first = characters.get(0);
            controller.selectCharacter(first);
            updateCharacterDetails(first);
            Actor firstActor = characterRow.getChildren().first();
            stage.addAction(Actions.run(() -> moveSelectorTo(characterSelectorBubble, firstActor)));
        }
        List<Weapon> weapons = controller.getAllWeapons();
        if (!weapons.isEmpty()) {
            Weapon first = weapons.get(0);
            controller.selectWeapon(first);
            updateWeaponDetails(first);
            Actor firstActor = weaponRow.getChildren().first();
            stage.addAction(Actions.run(() -> moveSelectorTo(weaponSelectorBubble, firstActor)));
        }
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
    }

    public void updateCharacterDetails(GameCharacter character) {
        portraitImage.setDrawable(new TextureRegionDrawable(new TextureRegion(character.portrait)));
        nameLabel.setText("Name: " + character.name);
        hpLabel.setText("HP: " + character.getCurrentHp());
        speedLabel.setText("Speed: " + character.speed);
    }

    public void updateWeaponDetails(Weapon weapon) {
        weaponIconImage.setDrawable(new TextureRegionDrawable(new TextureRegion(weapon.icon)));
        weaponNameLabel.setText("Weapon: " + weapon.name);
        weaponStatsLabel.setText("DMG: " + weapon.damage + "  |  Ammo: " + weapon.maxAmmo + "  |  Reload: " + weapon.reloadTime + "s");
    }

    private void moveSelectorTo(Image selector, Actor target) {
        selector.setVisible(true);
        selector.setPosition(
            target.getX() + target.getParent().getX() + target.getWidth() / 2f - selector.getWidth() / 2f,
            target.getY() + target.getParent().getY() + target.getHeight() / 2f - selector.getHeight() / 2f
        );
    }

    public int getSelectedDurationMinutes() {
        String str = durationSelect.getSelected();
        return Integer.parseInt(str);
    }
}
