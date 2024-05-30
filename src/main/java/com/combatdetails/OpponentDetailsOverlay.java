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

    private final TitleComponent opponentAccuracy = TitleComponent.builder().build();
    private final TitleComponent opponentDamage = TitleComponent.builder().build();
    private final TitleComponent opponentDps = TitleComponent.builder().build();
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
        setPreferredPosition(OverlayPosition.BOTTOM_LEFT);
        setSnappable(true);
        setResizable(true);

        opponentTitle.setText("Opponent");
        opponentAccuracy.setText("ACC: ~");
        opponentAccuracy.setColor(config.opponentTextColor());
        opponentDamage.setText("DMG: ~");
        opponentDamage.setColor(config.opponentTextColor());
        opponentDps.setText("DPS: ~");
        opponentDps.setColor(config.opponentTextColor());


        if(config.opponentAccuracy() ||
                config.opponentDamage() ||
                config.opponentDps()){
            panelComponent.getChildren().add(opponentTitle);
        }
        if(config.opponentDps())
            panelComponent.getChildren().add(opponentDps);
        if(config.opponentAccuracy())
            panelComponent.getChildren().add(opponentAccuracy);
        if(config.opponentDamage())
            panelComponent.getChildren().add(opponentDamage);
    }

    @Override
    public Dimension render(Graphics2D graphics){
        //if the config does not display any opponent data, do not display
        if(!config.opponentAccuracy() &&
                !config.opponentDamage() &&
                !config.opponentDps()){
            return null;
        }

        //If not in combat at all
        if(!playerCombatDetails.getInCombat()){
            return null;
        }

        //If the current combat is a "ghost combat" where the player is just interacting with an npc with no hits done/taken
        if(playerCombatDetails.getHitsTaken() == 0 && playerCombatDetails.getHitsDone() == 0){
            return null;
        }

        String opponentAttackString = String.format("ACC: %s%%", HIT_PERCENT_FORMAT.format(playerCombatDetails.getOpponentAccuracy() * 100));
        String opponentDamageString = String.format("DMG: %d", playerCombatDetails.getDamageTaken());
        String opponentDpsString = String.format("DPS: %s%%", HIT_PERCENT_FORMAT.format(playerCombatDetails.getOpponentAccuracy() * 100));

        opponentAccuracy.setText(opponentAttackString);
        opponentDamage.setText(opponentDamageString);
        opponentDps.setText(opponentDpsString);

        final FontMetrics fontMetrics = graphics.getFontMetrics();
        int panelWidth = Math.max(ComponentConstants.STANDARD_WIDTH/2, fontMetrics.stringWidth(opponentAttackString) + ComponentConstants.STANDARD_BORDER + ComponentConstants.STANDARD_BORDER);
        panelComponent.setPreferredSize(new Dimension(panelWidth, 0));
        return panelComponent.render(graphics);
    }
}
