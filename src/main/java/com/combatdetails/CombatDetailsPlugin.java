package com.combatdetails;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.HashMap;

@Slf4j
@PluginDescriptor(
	name = "Combat Details"
)
public class CombatDetailsPlugin extends Plugin
{
	//Number of grace ticks for an opponent to be given before removing them as an opponent
	private final int NPC_DETAILS_RESET_TIME = 20;
	private final int PLAYER_DETAILS_RESET_TIME = 100;

	private PlayerCombatDetails playerCombatDetails = new PlayerCombatDetails(PLAYER_DETAILS_RESET_TIME);
	private Actor player;
	private int totalPlayerAttacks;
	private int totalPlayerRedHitsDealt;
	private int totalPlayerDefenses;
	private int totalPlayerRedHitsTaken;
	private int killsPerHour;
	private final HashMap<Actor, NPCCombatDetails> combatOpponents = new HashMap<>();

	@Inject
	private Client client;

	@Inject
	private CombatDetailsConfig config;

	@Override
	protected void startUp() throws Exception
	{
		//Started
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged configChanged){

	}

	@Override
	protected void shutDown() throws Exception
	{
		//Stopped
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.timeInCombat(), null);
			player = client.getLocalPlayer();
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath actorDeath){
		combatOpponents.remove(actorDeath.getActor());
	}

	@Subscribe
	public void onGameTick(GameTick gameTick){
		playerCombatDetails.tickTimer();
		//For testing. puts the interaction time above the opponent every tick
		combatOpponents.forEach(((actor, combatDetails) -> {
			if(combatDetails.getResetTimer() <= 0){
				//check if still interacting
				if(actor.getInteracting() == player || player.getInteracting() == actor){
					combatDetails.setResetTimer(NPC_DETAILS_RESET_TIME);
					playerCombatDetails.setResetTimer(PLAYER_DETAILS_RESET_TIME);
				} else{
					combatOpponents.remove(actor);
					return;
				}
			}
			//Take the following actions AFTER we may have removed the opponent, so we aren't trying to apply an overhead
			//text to a non-existent actor next frame.
			combatDetails.tickTimer();
			actor.setOverheadText(String.valueOf(combatDetails.getResetTimer()));
			log.info("Details. Player Hits Taken: " + String.valueOf(playerCombatDetails.getHitsTaken()));
		}));
		if(combatOpponents.isEmpty() && playerCombatDetails.getResetTimer() <= 0){
			//stop tracking. Player is now out of combat
		}
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged interactingChanged){
		Actor source = interactingChanged.getSource();
		Actor target = interactingChanged.getTarget();
		if(source == player && target != null){
            combatOpponents.computeIfAbsent(target, key -> new NPCCombatDetails(NPC_DETAILS_RESET_TIME));
		}
		else if(target == player){
			combatOpponents.computeIfAbsent(source, key -> new NPCCombatDetails(NPC_DETAILS_RESET_TIME));
		} else {
			log.info("removing combatant");
            combatOpponents.remove(source);
		}

	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied){
		//Only display the on-screen info box if a hitsplat occurs to the player or one of the enemies
		//If the hitsplat was applied to someone in our combat opponents
		if(hitsplatApplied.getActor() == player){
			//if the player was hit, add that damage to the player's details
			playerCombatDetails.takeDamage(hitsplatApplied.getHitsplat().getAmount());

		}else if(combatOpponents.containsKey(hitsplatApplied.getActor())){
			//If the opponent was hit, add that to the opponent's details.
			NPCCombatDetails npcCombatDetails = combatOpponents.get(hitsplatApplied.getActor());
			npcCombatDetails.takeDamage(hitsplatApplied.getHitsplat().getAmount());
			playerCombatDetails.playerDealtDamage(hitsplatApplied.getHitsplat().getAmount());
		}
	}

	@Provides
	CombatDetailsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CombatDetailsConfig.class);
	}
}
