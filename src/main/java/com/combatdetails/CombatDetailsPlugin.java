package com.combatdetails;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@Slf4j
@PluginDescriptor(
	name = "Combat Details"
)
public class CombatDetailsPlugin extends Plugin
{
	//Number of grace ticks for an opponent to be given before removing them as an opponent
	public static final int MILLISECONDS_PER_TICK = 600;

	@Getter
	private PlayerCombatDetails playerCombatDetails = new PlayerCombatDetails();
	@Getter
	private Actor player;
	private int totalPlayerAttacks;
	private int totalPlayerRedHitsDealt;
	private int totalPlayerDefenses;
	private int totalPlayerRedHitsTaken;
	private int killsPerHour;
	private int outOfCombatTicks = 20;
	private final HashMap<Actor, NPCCombatDetails> combatOpponents = new HashMap<>();
	Iterator<Map.Entry<Actor, NPCCombatDetails>> iterator;

	@Inject
	private Client client;

	@Inject
	private CombatDetailsConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PlayerDetailsOverlay playerDetailsOverlay;

	@Inject
	private OpponentDetailsOverlay opponentDetailsOverlay;

	@Override
	protected void startUp() throws Exception
	{
		//Started
		overlayManager.add(playerDetailsOverlay);
		overlayManager.add(opponentDetailsOverlay);
		outOfCombatTicks = config.outOfCombatTicks();
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged configChanged){
		playerDetailsOverlay.buildOverlays();
		opponentDetailsOverlay.buildOverlays();

	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(playerDetailsOverlay);
		overlayManager.remove(opponentDetailsOverlay);
		//Stopped
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			player = client.getLocalPlayer();
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath actorDeath){
		playerCombatDetails.enemyKilled();
		combatOpponents.remove(actorDeath.getActor());
	}

	@Subscribe
	public void onGameTick(GameTick gameTick){
		iterator = combatOpponents.entrySet().iterator();
		if(playerCombatDetails.getInCombat()){
			playerCombatDetails.tickTimer();
		}
		//For testing. puts the interaction time above the opponent every tick
		while(iterator.hasNext()){
			playerCombatDetails.setResetTimer(outOfCombatTicks);
			Map.Entry<Actor, NPCCombatDetails> entry = iterator.next();
			Actor actor = entry.getKey();
			NPCCombatDetails combatDetails = entry.getValue();
			combatDetails.tickTimer();
			if(combatDetails.getResetTimer() <= 0){
				//check if still interacting
				if(actor.getInteracting() == player || player.getInteracting() == actor){
					//Reset the timer if they're still in combat/interacting
					combatDetails.setResetTimer(outOfCombatTicks);
				} else{
					//If not still interacting, remove from the list
					combatOpponents.remove(actor);
					return;
				}
			}
		}
		if(combatOpponents.isEmpty() && playerCombatDetails.getResetTimer() <= 0){
			playerCombatDetails.resetPlayerCombatDetails();
		}
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged interactingChanged){
		Actor source = interactingChanged.getSource();
		Actor target = interactingChanged.getTarget();
		if(source == player && target != null){
            combatOpponents.computeIfAbsent(target, key -> new NPCCombatDetails(outOfCombatTicks));
		}
		else if(target == player){
			combatOpponents.computeIfAbsent(source, key -> new NPCCombatDetails(outOfCombatTicks));
		} else {
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

		}else if(hitsplatApplied.getHitsplat().isMine() && combatOpponents.containsKey(hitsplatApplied.getActor())){
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
