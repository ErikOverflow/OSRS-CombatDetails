package com.combatdetails;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.*;
import net.runelite.client.ui.overlay.components.TextComponent;

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

    private final TitleComponent combatTime = TitleComponent.builder().build();
    private final TitleComponent playerAtks = TitleComponent.builder().build();
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
        setPreferredPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setSnappable(true);
        setResizable(true);

        playerTitle.setText("Player");
        combatTime.setText("~t");
        combatTime.setColor(config.playerTextColor());
        playerAtks.setText("~/~ (~)");
        playerAtks.setColor(config.playerTextColor());
        playerDamage.setText("~");
        playerDamage.setColor(config.playerTextColor());
        playerKillsPerHour.setText("KPH: ~");
        playerKillsPerHour.setColor(config.playerTextColor());


        if(config.playerAccuracy() ||
                config.playerDamage() ||
                config.playerAttacks() ||
                config.playerKillsPerHour() ||
                config.timeInCombat()){
            panelComponent.getChildren().add(playerTitle);
        }
        if(config.playerAttacks())
            panelComponent.getChildren().add(playerAtks);
        if(config.playerDamage())
            panelComponent.getChildren().add(playerDamage);
        if(config.timeInCombat())
            panelComponent.getChildren().add(combatTime);
        if(config.playerKillsPerHour())
            panelComponent.getChildren().add(playerKillsPerHour);
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
        String playerAttackString = String.format("%d/%d", playerCombatDetails.getRedHitsplatsDone(), playerCombatDetails.getHitsDone());
        if(config.playerAccuracy()){
            playerAttackString = String.format("%d/%d (%s)%%", playerCombatDetails.getRedHitsplatsDone(), playerCombatDetails.getHitsDone(), HIT_PERCENT_FORMAT.format(playerCombatDetails.getPlayerAccuracy() * 100));
        }
        playerTitle.setText(client.getLocalPlayer().getName());
        String playerDamageString = String.format("Dmg: %d", playerCombatDetails.getDamageDealt());
        String playerKillersPerHourString = String.format("KPH: %s", KPH_FORMAT.format(playerCombatDetails.getKillsPerHour()));
        String combatTimeString = String.format("%dt",playerCombatDetails.getTimeInCombat());
        playerAtks.setText(playerAttackString);
        playerDamage.setText(playerDamageString);
        playerKillsPerHour.setText(playerKillersPerHourString);
        combatTime.setText(combatTimeString);

        final FontMetrics fontMetrics = graphics.getFontMetrics();
        int panelWidth = Math.max(ComponentConstants.STANDARD_WIDTH/2, fontMetrics.stringWidth(playerAttackString) + ComponentConstants.STANDARD_BORDER + ComponentConstants.STANDARD_BORDER);
        panelWidth = Math.max(panelWidth, fontMetrics.stringWidth("" + client.getLocalPlayer().getName()) + ComponentConstants.STANDARD_BORDER + ComponentConstants.STANDARD_BORDER);
        panelComponent.setPreferredSize(new Dimension(panelWidth, 0));
        return panelComponent.render(graphics);
    }
}
