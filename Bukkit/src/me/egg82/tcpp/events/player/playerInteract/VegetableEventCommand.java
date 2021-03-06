package me.egg82.tcpp.events.player.playerInteract;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import me.egg82.tcpp.enums.PermissionsType;
import me.egg82.tcpp.services.registries.VegetableRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.events.EventCommand;
import ninja.egg82.plugin.utils.CommandUtil;

public class VegetableEventCommand extends EventCommand<PlayerInteractEvent> {
	//vars
	private IVariableRegistry<UUID> vegetableRegistry = ServiceLocator.getService(VegetableRegistry.class);
	
	//constructor
	public VegetableEventCommand() {
		super();
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (vegetableRegistry.hasRegister(event.getPlayer().getUniqueId())) {
			if (!CommandUtil.hasPermission(player, PermissionsType.FREECAM_WHILE_VEGETABLE)) {
				event.setCancelled(true);
			}
		}
	}
}
