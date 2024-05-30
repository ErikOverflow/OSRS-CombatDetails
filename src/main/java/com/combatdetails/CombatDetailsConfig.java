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
            description = "The number of ticks to wait before resetting.",
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
            keyName = "playerAccuracy",
            name = "Display Player Accuracy",
            description = "Display the accuracy of the player's attacks on monsters. A value over 1.0 means that a player succesfully hits multiple times per attack (multi-target attacks, cannon, etc)",
            position = 2,
            section = playerDetails
    )
    default boolean playerAccuracy() {
        return true;
    }

    @ConfigItem(
            keyName = "playerDamage",
            name = "Display Player Damage Amount",
            description = "Display the total amount of damage a player has done in combat",
            position = 3,
            section = playerDetails
    )
    default boolean playerDamage() {
        return true;
    }

    @ConfigItem(
            keyName = "playerKillsPerHour",
            name = "Display Kills Per Hour",
            description = "Display the total number of kills per hour a player achieves",
            position = 4,
            section = playerDetails
    )
    default boolean playerKillsPerHour() {
        return true;
    }

    @ConfigItem(
            keyName = "playerTextColor",
            name = "Text Color",
            description = "The text color for the player details",
            position = 6,
            section = playerDetails
    )
    default Color playerTextColor() {
        return new Color(146,171,255);
    }



    @ConfigSection(
            name = "Opponent Details",
            description = "Details regarding the opponent's combat stats.",
            position = 2
    )
    String opponentDetails = "opponentDetails";

    @ConfigItem(
            keyName = "opponentAccuracy",
            name = "Display Opponent Accuracy",
            description = "Display the accuracy of the opponent's hits.",
            position = 2,
            section = opponentDetails
    )
    default boolean opponentAccuracy() {
        return true;
    }

    @ConfigItem(
            keyName = "opponentDamage",
            name = "Display Opponent Damage Amount",
            description = "Display the total amount of damage a player has received in combat",
            position = 3,
            section = opponentDetails
    )
    default boolean opponentDamage() {
        return true;
    }


    @ConfigItem(
            keyName = "opponentTextColor",
            name = "Text Color",
            description = "The text color for the opponent details",
            position = 4,
            section = opponentDetails
    )
    default Color opponentTextColor() {
        return new Color(255,136,136);
    }
}
