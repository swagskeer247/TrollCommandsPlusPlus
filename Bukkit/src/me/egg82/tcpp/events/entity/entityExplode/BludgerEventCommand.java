package me.egg82.tcpp.events.entity.entityExplode;

import java.util.UUID;

import org.bukkit.event.entity.EntityExplodeEvent;

import me.egg82.tcpp.services.registries.BludgerRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.events.EventCommand;

public class BludgerEventCommand extends EventCommand<EntityExplodeEvent> {
	//vars
	private IVariableRegistry<UUID> bludgerRegistry = ServiceLocator.getService(BludgerRegistry.class);
	
	//constructor
	public BludgerEventCommand() {
		super();
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (event.isCancelled()) {
			return;
		}
		
		UUID uuid = bludgerRegistry.getKey(event.getEntity());
		if (uuid != null) {
			bludgerRegistry.removeRegister(uuid);
		}
	}
}
