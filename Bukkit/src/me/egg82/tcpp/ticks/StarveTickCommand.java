package me.egg82.tcpp.ticks;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.egg82.tcpp.services.registries.StarveRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.plugin.utils.CommandUtil;

public class StarveTickCommand extends TickCommand {
	//vars
	private IVariableRegistry<UUID> starveRegistry = ServiceLocator.getService(StarveRegistry.class);
	
	//constructor
	public StarveTickCommand() {
		super(15L);
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		for (UUID key : starveRegistry.getKeys()) {
			e(CommandUtil.getPlayerByUuid(key));
		}
	}
	private void e(Player player) {
		if (player == null) {
			return;
		}
		
		player.setFoodLevel(player.getFoodLevel() - 1);
	}
}
