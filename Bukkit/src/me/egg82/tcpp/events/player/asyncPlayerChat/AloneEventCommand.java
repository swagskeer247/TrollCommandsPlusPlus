package me.egg82.tcpp.events.player.asyncPlayerChat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.egg82.tcpp.services.registries.AloneRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.events.EventCommand;

public class AloneEventCommand extends EventCommand<AsyncPlayerChatEvent> {
	//vars
	private IVariableRegistry<UUID> aloneRegistry = ServiceLocator.getService(AloneRegistry.class);
	
	//constructor
	public AloneEventCommand() {
		super();
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (aloneRegistry.hasRegister(player.getUniqueId())) {
			event.setCancelled(true);
			return;
		}
		
		Set<Player> players = event.getRecipients();
		HashSet<Player> removedPlayers = new HashSet<Player>();
		
		for (Player p : players) {
			if (aloneRegistry.hasRegister(p.getUniqueId())) {
				removedPlayers.add(p);
			}
		}
		players.removeAll(removedPlayers);
	}
}
