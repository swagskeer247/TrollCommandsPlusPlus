package me.egg82.tcpp.events.player.playerRespawn;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.egg82.tcpp.services.registries.LsdRegistry;
import ninja.egg82.concurrent.IConcurrentDeque;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.patterns.tuples.Triplet;
import ninja.egg82.plugin.commands.events.EventCommand;

public class LsdEventCommand extends EventCommand<PlayerRespawnEvent> {
	//vars
	private IVariableRegistry<UUID> lsdRegistry = ServiceLocator.getService(LsdRegistry.class);
	
	//constructor
	public LsdEventCommand() {
		super();
	}
	
	//public
	
	//private
	@SuppressWarnings("unchecked")
	protected void onExecute(long elapsedMilliseconds) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (lsdRegistry.hasRegister(uuid)) {
			IConcurrentDeque<Triplet<String, Integer, Integer>> bLocs = lsdRegistry.getRegister(uuid, IConcurrentDeque.class);
			bLocs.clear();
		}
	}
}
