package me.egg82.tcpp.events.inventory.inventoryOpen;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import me.egg82.tcpp.services.registries.RandomMenuMenuRegistry;
import me.egg82.tcpp.services.registries.RandomMenuRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.events.EventCommand;
import ninja.egg82.plugin.reflection.type.TypeFilterHelper;
import ninja.egg82.utils.MathUtil;

public class RandomMenuEventCommand extends EventCommand<InventoryOpenEvent> {
	//vars
	private IVariableRegistry<UUID> randomMenuRegistry = ServiceLocator.getService(RandomMenuRegistry.class);
	private IVariableRegistry<UUID> randomMenuMenuRegistry = ServiceLocator.getService(RandomMenuMenuRegistry.class);
	
	private InventoryType[] types = null;
	
	//constructor
	public RandomMenuEventCommand() {
		super();
		
		TypeFilterHelper<InventoryType> typeFilterHelper = new TypeFilterHelper<InventoryType>(InventoryType.class);
		types = typeFilterHelper.filter(
				typeFilterHelper.filter(
					typeFilterHelper.getAllTypes(),
				"crafting", false),
				"creative", false);
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		Player player = (Player) event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (event.isCancelled()) {
			return;
		}
		
		if (!randomMenuRegistry.hasRegister(uuid) || randomMenuMenuRegistry.hasRegister(uuid)) {
			return;
		}
		
		event.setCancelled(true);
		Inventory inv = Bukkit.createInventory(event.getInventory().getHolder(), types[MathUtil.fairRoundedRandom(0, types.length - 1)]);
		for (int i = 0; i < Math.min(inv.getSize(), event.getInventory().getSize()); i++) {
			inv.setItem(i, event.getInventory().getItem(i));
		}
		randomMenuMenuRegistry.setRegister(uuid, null);
		player.openInventory(inv);
	}
}
