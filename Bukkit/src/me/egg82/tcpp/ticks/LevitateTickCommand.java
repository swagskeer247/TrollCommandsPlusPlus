package me.egg82.tcpp.ticks;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.egg82.tcpp.services.registries.LevitateRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.plugin.utils.CommandUtil;

public class LevitateTickCommand extends TickCommand {
	//vars
	private IVariableRegistry<UUID> levitateRegistry = ServiceLocator.getService(LevitateRegistry.class);
	
	//constructor
	public LevitateTickCommand() {
		super(2L);
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		for (UUID key : levitateRegistry.getKeys()) {
			e(CommandUtil.getPlayerByUuid(key));
		}
	}
	private void e(Player player) {
		if (player == null) {
			return;
		}
		
		player.setVelocity(new Vector(0.0d, 0.1d, 0.0d));
	}
}
