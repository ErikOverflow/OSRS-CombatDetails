package com.combatdetails;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.*;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;

public class PlayerDetailsOverlay extends OverlayPanel{
    private static final DecimalFormat KPH_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat HIT_PERCENT_FORMAT = new DecimalFormat("#.##");
    private static final int BORDER_SIZE = 2;
    private final CombatDetailsConfig config;
    private final Client client;
    private final PlayerCombatDetails playerCombatDetails;

    private final TitleComponent playerDps = TitleComponent.builder().build();
    private final TitleComponent playerAccuracy = TitleComponent.builder().build();
    private final TitleComponent playerDamage = TitleComponent.builder().build();
    private final TitleComponent playerKillsPerHour = TitleComponent.builder().build();
    private final TitleComponent playerTitle = TitleComponent.builder().build();

    @Inject
    private PlayerDetailsOverlay(Client client, CombatDetailsPlugin plugin, CombatDetailsConfig config){
        super(plugin);
        this.config = config;
        this.client = client;
        this.playerCombatDetails = plugin.getPlayerCombatDetails();
        setPriority(PRIORITY_LOW);
        buildOverlays();
    }

    public void buildOverlays(){
        panelComponent.getChildren().clear();
        panelComponent.setBorder(new Rectangle(BORDER_SIZE,BORDER_SIZE,BORDER_SIZE,BORDER_SIZE));
        panelComponent.setGap(new Point(0,2));
        this.setResizable(true);
        setPreferredPosition(OverlayPosition.BOTTOM_LEFT);
        setSnappable(true);
        setResizable(true);

        playerTitle.setText("Player");
        playerDps.setText("DPS: ~");
        playerDps.setColor(config.playerTextColor());
        playerAccuracy.setText("Acc: ~");
        playerAccuracy.setColor(config.playerTextColor());
        playerDamage.setText("Dmg: ~");
        playerDamage.setColor(config.playerTextColor());
        playerKillsPerHour.setText("KPH: ~");
        playerKillsPerHour.setColor(config.playerTextColor());


        if(config.playerAccuracy() ||
                config.playerDamage() ||
                config.playerKillsPerHour() ||
                config.playerDps()){
            panelComponent.getChildren().add(playerTitle);
        }
        if(config.playerDps())
            panelComponent.getChildren().add(playerDps);
        if(config.playerAccuracy())
            panelComponent.getChildren().add(playerAccuracy);
        if(config.playerDamage())
            panelComponent.getChildren().add(playerDamage);
        if(config.playerKillsPerHour())
            panelComponent.getChildren().add(playerKillsPerHour);
    }

    @Override
    public Dimension render(Graphics2D graphics){
        //If the config does display contain any player data, do not display
        if(!config.playerAccuracy() &&
                !config.playerDamage() &&
                !config.playerKillsPerHour() &&
                !config.playerDps()){
            return null;
        }

        //If not in combat, do not display
        if(!playerCombatDetails.getInCombat()){
            return null;
        }

        //If the current combat is a "ghost combat" where the player is just interacting with an npc but not taking or dealing damage
        if(playerCombatDetails.getHitsTaken() == 0 && playerCombatDetails.getHitsDone() == 0){
            return null;
        }

        String playerAccuracyString = String.format("ACC: %s%%", HIT_PERCENT_FORMAT.format(playerCombatDetails.getPlayerAccuracy() * 100));
        String playerDpsString = String.format("DPS: %s",KPH_FORMAT.format(playerCombatDetails.getPlayerDps()));
        String playerDamageString = String.format("DMG: %d", playerCombatDetails.getDamageDealt());
        String playerKillersPerHourString = String.format("KPH: %s", KPH_FORMAT.format(playerCombatDetails.getKillsPerHour()));

        playerTitle.setText(client.getLocalPlayer().getName());
        playerAccuracy.setText(playerAccuracyString);
        playerDamage.setText(playerDamageString);
        playerDps.setText(playerDpsString);
        playerKillsPerHour.setText(playerKillersPerHourString);

        //Resize panel width to be big enough for player name or the accuracy string
        final FontMetrics fontMetrics = graphics.getFontMetrics();
        int panelWidth = Math.max(ComponentConstants.STANDARD_WIDTH/2, fontMetrics.stringWidth(playerAccuracyString) + ComponentConstants.STANDARD_BORDER + ComponentConstants.STANDARD_BORDER);
        panelWidth = Math.max(panelWidth, fontMetrics.stringWidth("" + client.getLocalPlayer().getName()) + ComponentConstants.STANDARD_BORDER + ComponentConstants.STANDARD_BORDER);
        panelComponent.setPreferredSize(new Dimension(panelWidth, 0));
        return panelComponent.render(graphics);
    }
}
