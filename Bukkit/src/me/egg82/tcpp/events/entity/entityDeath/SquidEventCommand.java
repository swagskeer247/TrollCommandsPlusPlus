package me.egg82.tcpp.events.entity.entityDeath;

import java.util.UUID;

import org.bukkit.event.entity.EntityDeathEvent;

import me.egg82.tcpp.services.registries.SquidDeathRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.events.EventCommand;

public class SquidEventCommand extends EventCommand<EntityDeathEvent> {
	//vars
	private IVariableRegistry<UUID> squidDeathRegistry = ServiceLocator.getService(SquidDeathRegistry.class);
	
	//constructor
	public SquidEventCommand() {
		super();
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		UUID uuid = event.getEntity().getUniqueId();
		
		if (squidDeathRegistry.hasRegister(uuid)) {
			event.getDrops().clear();
			event.setDroppedExp(0);
			squidDeathRegistry.removeRegister(uuid);
		}
	}
}
