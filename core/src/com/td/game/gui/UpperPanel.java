package com.td.game.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.td.game.Assets;
import com.td.game.PlayerInfo;

public class UpperPanel {
    private Group core;
    private PlayerInfo playerInfo;
    private Label moneyLabel;
    private Label hpLabel;
    private StringBuilder tmpSB;
    private BitmapFont font36;

    public UpperPanel(PlayerInfo playerInfo, Stage stage, int x, int y) {
        this.playerInfo = playerInfo;
        font36 = Assets.getInstance().getAssetManager().get("zorque36.ttf", BitmapFont.class);
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font36;
        core = new Group();
        tmpSB = new StringBuilder(20);
        hpLabel = new Label("", labelStyle);
        moneyLabel = new Label("", labelStyle);
        core.setPosition(x, y);
        hpLabel.setPosition(160, 30);
        moneyLabel.setPosition(400, 30);
        Image panelImage = new Image(Assets.getInstance().getAtlas().findRegion("upperPanel"));
        core.addActor(panelImage);
        core.addActor(hpLabel);
        core.addActor(moneyLabel);
        stage.addActor(core);
    }

    public void update() {
        tmpSB.setLength(0);
        tmpSB.append(playerInfo.getHp()).append(" / ").append(playerInfo.getHpMax());
        hpLabel.setText(tmpSB);
        tmpSB.setLength(0);
        tmpSB.append(playerInfo.getMoney());
        moneyLabel.setText(tmpSB);
    }
}
