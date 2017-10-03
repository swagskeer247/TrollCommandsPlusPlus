package me.egg82.tcpp.events.entity.itemDespawn;

import java.util.UUID;

import org.bukkit.event.entity.ItemDespawnEvent;

import me.egg82.tcpp.services.registries.VegetableItemRegistry;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.EventCommand;

public class VegetableEventCommand extends EventCommand<ItemDespawnEvent> {
	//vars
	private IRegistry<UUID> vegetableItemRegistry = ServiceLocator.getService(VegetableItemRegistry.class);
	
	//constructor
	public VegetableEventCommand(ItemDespawnEvent event) {
		super(event);
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (event.isCancelled()) {
			return;
		}
		
		if (vegetableItemRegistry.hasValue(event.getEntity())) {
			event.setCancelled(true);
		}
	}
}
