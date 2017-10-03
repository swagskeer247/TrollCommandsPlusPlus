package me.egg82.tcpp.commands.lucky;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import me.egg82.tcpp.core.LuckyCommand;
import me.egg82.tcpp.services.registries.LuckyVillagerRegistry;
import ninja.egg82.events.ExpireEventArgs;
import ninja.egg82.patterns.IExpiringRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.utils.BlockUtil;
import ninja.egg82.plugin.utils.LocationUtil;

public class SpawnVillagerCommand extends LuckyCommand {
	//vars
	private IExpiringRegistry<UUID> luckyVillagerRegistry = ServiceLocator.getService(LuckyVillagerRegistry.class);
	
	//constructor
	public SpawnVillagerCommand(Player player) {
		super(player);
		
		luckyVillagerRegistry.onExpire().attach((s, e) -> onExpire(s, e));
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		Villager v = player.getWorld().spawn(BlockUtil.getTopWalkableBlock(LocationUtil.getRandomPointAround(player.getLocation(), 1.5d)), Villager.class);
		luckyVillagerRegistry.setRegister(v.getUniqueId(), v);
		v.setCustomName(ChatColor.GOLD + "Lucky Villager");
		v.setCustomNameVisible(true);
	}
	
	private void onExpire(Object sender, ExpireEventArgs<UUID> e) {
		Villager v = (Villager) e.getValue();
		
		v.setCustomNameVisible(false);
		v.setCustomName(null);
	}
}
