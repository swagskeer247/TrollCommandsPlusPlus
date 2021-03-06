package me.egg82.tcpp.events.inventory.inventoryClose;

import java.util.UUID;

import org.bukkit.event.inventory.InventoryCloseEvent;

import me.egg82.tcpp.services.registries.TrollInventoryRegistry;
import me.egg82.tcpp.services.registries.TrollPageRegistry;
import me.egg82.tcpp.services.registries.TrollPlayerRegistry;
import me.egg82.tcpp.services.registries.TrollSearchRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.events.EventCommand;

public class TrollEventCommand extends EventCommand<InventoryCloseEvent> {
	//vars
	private IVariableRegistry<UUID> trollInventoryRegistry = ServiceLocator.getService(TrollInventoryRegistry.class);
	private IVariableRegistry<UUID> trollPlayerRegistry = ServiceLocator.getService(TrollPlayerRegistry.class);
	private IVariableRegistry<UUID> trollPageRegistry = ServiceLocator.getService(TrollPageRegistry.class);
	private IVariableRegistry<UUID> trollSearchRegistry = ServiceLocator.getService(TrollSearchRegistry.class);
	
	//constructor
	public TrollEventCommand() {
		super();
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		UUID uuid = event.getPlayer().getUniqueId();
		
		// This is one lookup faster than not having it. Optimize where we can.
		if (!trollInventoryRegistry.hasRegister(uuid)) {
			return;
		}
		
		trollInventoryRegistry.removeRegister(uuid);
		trollPlayerRegistry.removeRegister(uuid);
		trollPageRegistry.removeRegister(uuid);
		trollSearchRegistry.removeRegister(uuid);
	}
}
