package com.combatdetails;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;

public class OpponentDetailsOverlay extends OverlayPanel{
    private static final DecimalFormat HIT_PERCENT_FORMAT = new DecimalFormat("#.##");
    private static final int BORDER_SIZE = 2;
    private final CombatDetailsConfig config;
    private final PlayerCombatDetails playerCombatDetails;

    private final LineComponent opponentHits = LineComponent.builder().build();
    private final LineComponent opponentAccuracy = LineComponent.builder().build();
    private final LineComponent opponentDamage = LineComponent.builder().build();
    private final TitleComponent opponentTitle = TitleComponent.builder().build();

    @Inject
    private OpponentDetailsOverlay(CombatDetailsPlugin plugin, CombatDetailsConfig config){
        super(plugin);
        this.config = config;
        this.playerCombatDetails = plugin.getPlayerCombatDetails();
        setPriority(PRIORITY_LOW);
        buildOverlays();
    }

    public void buildOverlays(){
        panelComponent.getChildren().clear();
        panelComponent.setBorder(new Rectangle(BORDER_SIZE,BORDER_SIZE,BORDER_SIZE,BORDER_SIZE));
        panelComponent.setPreferredSize(new Dimension(ComponentConstants.STANDARD_WIDTH, 0));
        panelComponent.setPreferredLocation(new Point(0,0));

        opponentTitle.setText("Opponent details");
        opponentHits.setLeft("Atks:");
        opponentHits.setLeftColor(Color.WHITE);
        opponentHits.setRightColor(config.opponentTextColor());
        opponentHits.setRight("~");
        opponentDamage.setLeft("Dmg:");
        opponentDamage.setLeftColor(Color.WHITE);
        opponentDamage.setRight("~");
        opponentDamage.setRightColor(config.opponentTextColor());
        opponentAccuracy.setLeft("Acc%:");
        opponentAccuracy.setLeftColor(Color.WHITE);
        opponentAccuracy.setRight("~");
        opponentAccuracy.setRightColor(config.opponentTextColor());


        if(config.opponentAccuracy() ||
                config.opponentDamage() ||
                config.opponentHitCount()){
            panelComponent.getChildren().add(opponentTitle);
        }
        if(config.opponentHitCount())
            panelComponent.getChildren().add(opponentHits);
        if(config.opponentDamage())
            panelComponent.getChildren().add(opponentDamage);
        if(config.opponentAccuracy())
            panelComponent.getChildren().add(opponentAccuracy);
    }

    @Override
    public Dimension render(Graphics2D graphics){
        //If not in combat at all
        if(!playerCombatDetails.getInCombat()){
            return null;
        }
        //If the current combat is a "ghost combat" where the player is just interacting with an npc
        if(playerCombatDetails.getHitsTaken() == 0 && playerCombatDetails.getHitsDone() == 0){
            return null;
        }
        opponentHits.setRight(Integer.toString(playerCombatDetails.getHitsTaken()));
        opponentDamage.setRight(Integer.toString(playerCombatDetails.getDamageTaken()));
        opponentAccuracy.setRight(HIT_PERCENT_FORMAT.format(playerCombatDetails.getOpponentAccuracy()));

        return panelComponent.render(graphics);
    }
}
