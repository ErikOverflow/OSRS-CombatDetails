package com.combatdetails;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;

public class OpponentDetailsOverlay extends OverlayPanel{
    private static final DecimalFormat HIT_PERCENT_FORMAT = new DecimalFormat("#.##");
    private static final int BORDER_SIZE = 2;
    private final CombatDetailsConfig config;
    private final PlayerCombatDetails playerCombatDetails;

    private final TitleComponent opponentAtks = TitleComponent.builder().build();
    private final TitleComponent opponentDamage = TitleComponent.builder().build();
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
        panelComponent.setGap(new Point(0,2));
        setPreferredPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setSnappable(true);
        setResizable(true);

        opponentTitle.setText("Opponent");
        opponentAtks.setText("~/~ (~)");
        opponentAtks.setColor(config.opponentTextColor());
        opponentDamage.setText("~");
        opponentDamage.setColor(config.opponentTextColor());


        if(config.opponentAccuracy() ||
                config.opponentDamage() ||
                config.opponentAttacks()){
            panelComponent.getChildren().add(opponentTitle);
        }
        if(config.opponentAttacks())
            panelComponent.getChildren().add(opponentAtks);
        if(config.opponentDamage())
            panelComponent.getChildren().add(opponentDamage);
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

        String opponentAttackString = String.format("%d/%d", playerCombatDetails.getRedHitsplatsTaken(), playerCombatDetails.getHitsTaken());
        if(config.playerAccuracy()){
            opponentAttackString = String.format("%d/%d (%s)%%", playerCombatDetails.getRedHitsplatsTaken(), playerCombatDetails.getHitsTaken(), HIT_PERCENT_FORMAT.format(playerCombatDetails.getOpponentAccuracy() * 100));
        }
        String opponentDamageString = String.format("Dmg: %d", playerCombatDetails.getDamageTaken());
        opponentAtks.setText(opponentAttackString);
        opponentDamage.setText(opponentDamageString);

        final FontMetrics fontMetrics = graphics.getFontMetrics();
        int panelWidth = Math.max(ComponentConstants.STANDARD_WIDTH/2, fontMetrics.stringWidth(opponentAttackString) + ComponentConstants.STANDARD_BORDER + ComponentConstants.STANDARD_BORDER);
        panelComponent.setPreferredSize(new Dimension(panelWidth, 0));
        return panelComponent.render(graphics);
    }
}
