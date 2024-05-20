package com.combatdetails;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.*;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;

public class CombatDetailsOverlay extends OverlayPanel{
    private static final DecimalFormat KPH_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat HIT_PERCENT_FORMAT = new DecimalFormat("#.##");
    private static final int BORDER_SIZE = 2;
    private final CombatDetailsConfig config;
    private final PlayerCombatDetails playerCombatDetails;

    private final LineComponent combatTime = LineComponent.builder().build();
    private final LineComponent playerHits = LineComponent.builder().build();
    private final LineComponent playerAccuracy = LineComponent.builder().build();
    private final LineComponent playerDamage = LineComponent.builder().build();
    private final LineComponent playerKillsPerHour = LineComponent.builder().build();
    private final TitleComponent playerTitle = TitleComponent.builder().build();

    @Inject
    private CombatDetailsOverlay(CombatDetailsPlugin plugin, CombatDetailsConfig config){
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

        playerTitle.setText("Player details");

        combatTime.setLeft("Ticks:");
        combatTime.setLeftColor(Color.WHITE);
        combatTime.setRight("~");
        combatTime.setRightColor(config.playerTextColor());
        playerHits.setLeft("Atks:");
        playerHits.setLeftColor(Color.WHITE);
        playerHits.setRight("~");
        playerHits.setRightColor(config.playerTextColor());
        playerDamage.setLeft("Dmg:");
        playerDamage.setLeftColor(Color.WHITE);
        playerDamage.setRight("~");
        playerDamage.setRightColor(config.playerTextColor());
        playerAccuracy.setLeft("Acc%:");
        playerAccuracy.setLeftColor(Color.WHITE);
        playerAccuracy.setRight("~");
        playerAccuracy.setRightColor(config.playerTextColor());
        playerKillsPerHour.setLeft("KPH:");
        playerKillsPerHour.setLeftColor(Color.WHITE);
        playerKillsPerHour.setRight("~");
        playerKillsPerHour.setRightColor(config.playerTextColor());


        if(config.playerAccuracy() ||
                config.playerDamage() ||
                config.playerHitCount() ||
                config.playerKillsPerHour() ||
                config.timeInCombat()){
            panelComponent.getChildren().add(playerTitle);
        }
        if(config.playerHitCount())
            panelComponent.getChildren().add(playerHits);
        if(config.playerDamage())
            panelComponent.getChildren().add(playerDamage);
        if(config.playerAccuracy())
            panelComponent.getChildren().add(playerAccuracy);
        if(config.playerKillsPerHour())
            panelComponent.getChildren().add(playerKillsPerHour);
        if(config.timeInCombat())
            panelComponent.getChildren().add(combatTime);
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
        playerHits.setRight(Integer.toString(playerCombatDetails.getHitsDone()));
        playerDamage.setRight(Integer.toString(playerCombatDetails.getDamageDealt()));
        playerAccuracy.setRight(HIT_PERCENT_FORMAT.format(playerCombatDetails.getPlayerAccuracy()));
        playerKillsPerHour.setRight(KPH_FORMAT.format(playerCombatDetails.getKillsPerHour()));
        combatTime.setRight(Integer.toString(playerCombatDetails.getTimeInCombat()));
        return panelComponent.render(graphics);
    }
}
