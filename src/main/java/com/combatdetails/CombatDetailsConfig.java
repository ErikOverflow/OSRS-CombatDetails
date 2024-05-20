package com.combatdetails;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(CombatDetailsConfig.GROUP)
public interface CombatDetailsConfig extends Config
{
	String GROUP = "combatdetails";

	@ConfigItem(
			keyName = "timeInCombat",
			name = "Show time in combat",
			description = "Configures whether or not to display time in combat",
			position = 0
	)
	default boolean timeInCombat()
	{
		return true;
	}

	@ConfigSection(
			name = "Attack Details",
			description = "Details regarding the player's attacks.",
			position = 0
	)
	String attackDetails = "attackDetails";
	@ConfigItem(
			keyName = "attackRedHitsplats",
			name = "Count the number of red hitsplats the player lands on their opponents while in combat",
			description = "Configures whether or not to display time in combat",
			position = 0
	)
	default boolean redHitsplats()
	{
		return true;
	}

	@ConfigSection(
			name = "Defense Details",
			description = "Details regarding the player's defense.",
			position = 0
	)
	String defenseDetails = "defenseDetails";
}
