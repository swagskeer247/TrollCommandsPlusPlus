package me.egg82.tcpp.events.player.playerJoin;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import me.egg82.tcpp.services.registries.FoolsGoldRegistry;
import me.egg82.tcpp.util.FoolsGoldHelper;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.events.EventCommand;
import ninja.egg82.plugin.utils.TaskUtil;

public class FoolsGoldEventCommand extends EventCommand<PlayerJoinEvent> {
	//vars
	private IVariableRegistry<UUID> foolsGoldRegistry = ServiceLocator.getService(FoolsGoldRegistry.class);
	private FoolsGoldHelper foolsGoldHelper = ServiceLocator.getService(FoolsGoldHelper.class);
	
	//constructor
	public FoolsGoldEventCommand() {
		super();
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (foolsGoldRegistry.hasRegister(uuid)) {
			TaskUtil.runAsync(new Runnable() {
				public void run() {
					foolsGoldHelper.updatePlayer(uuid, player, player.getLocation());
				}
			}, 80L);
		}
	}
}
