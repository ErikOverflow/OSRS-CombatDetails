package com.combatdetails;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup(CombatDetailsConfig.GROUP)
public interface CombatDetailsConfig extends Config {
    String GROUP = "combatdetails";

    @ConfigItem(
            keyName = "outOfCombatTicks",
            name = "Out Of Combat Time",
            description = "The number of ticks to wait before considering combat over.",
            position = 0
    )
    default int outOfCombatTicks() { return 20; }

    @ConfigSection(
            name = "Player Details",
            description = "Details about the player's combat stats.",
            position = 1
    )
    String playerDetails = "playerDetails";

    @ConfigItem(
            keyName = "timeInCombat",
            name = "Show time in combat",
            description = "Configures whether or not to display time in combat",
            position = 1,
            section = playerDetails
    )
    default boolean timeInCombat() { return false; }

    @ConfigItem(
            keyName = "playerHitCount",
            name = "Display Player Hit Count",
            description = "Display the total number of attacks the player attempts. Multi-target attacks count as multiple hits",
            position = 0,
            section = playerDetails
    )
    default boolean playerHitCount() {
        return false;
    }

    @ConfigItem(
            keyName = "playerDamage",
            name = "Display Player Damage Amount",
            description = "Display the total amount of damage a player has done in combat",
            position = 0,
            section = playerDetails
    )
    default boolean playerDamage() {
        return true;
    }

    @ConfigItem(
            keyName = "playerAccuracy",
            name = "Display Player Accuracy",
            description = "Display the accuracy of the player's attacks on monsters. A value over 1.0 means that a player succesfully hits multiple times per attack (multi-target attacks, cannon, etc)",
            position = 0,
            section = playerDetails
    )
    default boolean playerAccuracy() {
        return true;
    }

    @ConfigItem(
            keyName = "playerKillsPerHour",
            name = "Display Kills Per Hour",
            description = "Display the total number of kills per hour a player achieves",
            position = 0,
            section = playerDetails
    )
    default boolean playerKillsPerHour() {
        return true;
    }

    @ConfigItem(
            keyName = "playerTextColor",
            name = "Text Color",
            description = "The text color for the player details",
            position = 5,
            section = playerDetails
    )
    default Color playerTextColor() {
        return Color.BLUE;
    }



    @ConfigSection(
            name = "Opponent Details",
            description = "Details regarding the opponent's combat stats.",
            position = 2
    )
    String opponentDetails = "opponentDetails";

    @ConfigItem(
            keyName = "opponentHitCount",
            name = "Display Opponent Hit Count",
            description = "Display the total number of hits the player receives in combat",
            position = 0,
            section = opponentDetails
    )
    default boolean opponentHitCount() {
        return false;
    }

    @ConfigItem(
            keyName = "opponentDamage",
            name = "Display Opponent Damage Amount",
            description = "Display the total amount of damage a player has received in combat",
            position = 0,
            section = opponentDetails
    )
    default boolean opponentDamage() {
        return true;
    }

    @ConfigItem(
            keyName = "opponentAccuracy",
            name = "Display Opponent Accuracy",
            description = "Display the accuracy of the opponent's hits on the player.",
            position = 0,
            section = opponentDetails
    )
    default boolean opponentAccuracy() {
        return true;
    }

    @ConfigItem(
            keyName = "opponentTextColor",
            name = "Text Color",
            description = "The text color for the opponent details",
            position = 5,
            section = opponentDetails
    )
    default Color opponentTextColor() {
        return Color.RED;
    }
}
