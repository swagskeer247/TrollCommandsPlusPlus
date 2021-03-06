package me.egg82.tcpp.events.player.playerJoin;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import me.egg82.tcpp.services.registries.AloneRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.events.EventCommand;
import ninja.egg82.plugin.utils.CommandUtil;

public class AloneEventCommand extends EventCommand<PlayerJoinEvent> {
	//vars
	private IVariableRegistry<UUID> aloneRegistry = ServiceLocator.getService(AloneRegistry.class);
	
	//constructor
	public AloneEventCommand() {
		super();
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (aloneRegistry.hasRegister(uuid)) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				player.hidePlayer(p);
			}
		} else {
			for (UUID key : aloneRegistry.getKeys()) {
				Player p = CommandUtil.getPlayerByUuid(key);
				if (p != null) {
					p.hidePlayer(player);
				}
			}
		}
	}
}
