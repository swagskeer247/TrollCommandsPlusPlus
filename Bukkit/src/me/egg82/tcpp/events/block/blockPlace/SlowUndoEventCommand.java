package me.egg82.tcpp.events.block.blockPlace;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import me.egg82.tcpp.services.registries.SlowUndoRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.events.EventCommand;
import ninja.egg82.plugin.core.BlockData;
import ninja.egg82.plugin.utils.BlockUtil;
import ninja.egg82.plugin.utils.TaskUtil;
import ninja.egg82.utils.MathUtil;

public class SlowUndoEventCommand extends EventCommand<BlockPlaceEvent> {
	//vars
	private IVariableRegistry<UUID> slowUndoRegistry = ServiceLocator.getService(SlowUndoRegistry.class);
	
	//constructor
	public SlowUndoEventCommand() {
		super();
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (!slowUndoRegistry.hasRegister(player.getUniqueId())) {
			return;
		}
		
		// Save block state
		Location blockLocation = event.getBlock().getLocation();
		BlockData blockData = new BlockData(null, event.getBlockReplacedState(), Material.AIR, null);
		
		// Wait 4-6 seconds
		TaskUtil.runSync(new Runnable() {
			public void run() {
				// "Undo" this event
				BlockUtil.setBlock(blockLocation, blockData, true);
			}
		}, MathUtil.fairRoundedRandom(80, 120));
	}
}
