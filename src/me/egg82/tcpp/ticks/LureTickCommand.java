package me.egg82.tcpp.ticks;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;

import me.egg82.tcpp.services.registries.LureRegistry;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.TickCommand;
import ninja.egg82.plugin.utils.CommandUtil;

public class LureTickCommand extends TickCommand {
	//vars
	private IRegistry<UUID> lureRegistry = ServiceLocator.getService(LureRegistry.class);
	
	//constructor
	public LureTickCommand() {
		super();
		ticks = 40L;
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		for (UUID key : lureRegistry.getKeys()) {
			e(CommandUtil.getPlayerByUuid(key));
		}
	}
	private void e(Player player) {
		if (player == null) {
			return;
		}
		
		List<Entity> entities = player.getNearbyEntities(100.0d, 100.0d, 100.0d);
		for (Entity e : entities) {
			EntityType type = e.getType();
			
			if (e instanceof Monster && e.getWorld().equals(player.getWorld())) {
				if (type == EntityType.PIG_ZOMBIE) {
					PigZombie pig = (PigZombie) e;
					pig.setAngry(true);
				}
				
				((Monster) e).setTarget(player);
				e.setVelocity(player.getLocation().toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.23d));
			}
		}
	}
}
