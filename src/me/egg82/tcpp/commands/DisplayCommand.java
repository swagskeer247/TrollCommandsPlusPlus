package me.egg82.tcpp.commands;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.egg82.tcpp.enums.CommandErrorType;
import me.egg82.tcpp.enums.MessageType;
import me.egg82.tcpp.enums.PermissionsType;
import me.egg82.tcpp.services.DisplayBlockRegistry;
import me.egg82.tcpp.services.DisplayLocationRegistry;
import me.egg82.tcpp.services.DisplayRegistry;
import me.egg82.tcpp.util.DisplayHelper;
import ninja.egg82.events.CommandEvent;
import ninja.egg82.patterns.IRegistry;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.PluginCommand;
import ninja.egg82.plugin.enums.SpigotCommandErrorType;
import ninja.egg82.plugin.enums.SpigotMessageType;
import ninja.egg82.plugin.utils.CommandUtil;

public class DisplayCommand extends PluginCommand {
	//vars
	private IRegistry displayRegistry = (IRegistry) ServiceLocator.getService(DisplayRegistry.class);
	private IRegistry displayBlockRegistry = (IRegistry) ServiceLocator.getService(DisplayBlockRegistry.class);
	private IRegistry displayLocationRegistry = (IRegistry) ServiceLocator.getService(DisplayLocationRegistry.class);
	
	private DisplayHelper displayHelper = (DisplayHelper) ServiceLocator.getService(DisplayHelper.class);
	
	//constructor
	public DisplayCommand(CommandSender sender, Command command, String label, String[] args) {
		super(sender, command, label, args);
	}
	
	//public
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (!CommandUtil.hasPermission(sender, PermissionsType.COMMAND_DISPLAY)) {
			sender.sendMessage(SpigotMessageType.NO_PERMISSIONS);
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.NO_PERMISSIONS);
			return;
		}
		if (!CommandUtil.isArrayOfAllowedLength(args, 1)) {
			sender.sendMessage(SpigotMessageType.INCORRECT_USAGE);
			sender.getServer().dispatchCommand(sender, "help " + command.getName());
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.INCORRECT_USAGE);
			return;
		}
		
		Player player = CommandUtil.getPlayerByName(args[0]);
		
		if (player == null) {
			sender.sendMessage(SpigotMessageType.PLAYER_NOT_FOUND);
			dispatch(CommandEvent.ERROR, SpigotCommandErrorType.PLAYER_NOT_FOUND);
			return;
		}
		if (CommandUtil.hasPermission(player, PermissionsType.IMMUNE)) {
			sender.sendMessage(MessageType.PLAYER_IMMUNE);
			dispatch(CommandEvent.ERROR, CommandErrorType.PLAYER_IMMUNE);
			return;
		}
		
		e(player.getUniqueId().toString(), player);
		
		dispatch(CommandEvent.COMPLETE, null);
	}
	private void e(String uuid, Player player) {
		if (displayRegistry.hasRegister(uuid)) {
			displayHelper.unsurround(player.getLocation());
			displayRegistry.setRegister(uuid, Player.class, null);
			displayBlockRegistry.setRegister(uuid, Set.class, null);
			displayLocationRegistry.setRegister(uuid, Location.class, null);
			
			sender.sendMessage(player.getName() + " is no longer on display.");
		} else {
			Location playerLocation = player.getLocation().clone();
			playerLocation.setX(playerLocation.getBlockX() + 0.5d);
			playerLocation.setY(playerLocation.getBlockY());
			playerLocation.setZ(playerLocation.getBlockZ() + 0.5d);
			
			displayHelper.surround(playerLocation, Material.GLASS, Material.THIN_GLASS);
			displayRegistry.setRegister(uuid, Player.class, player);
			displayBlockRegistry.setRegister(uuid, Set.class, displayHelper.getBlockLocationsAround(playerLocation));
			displayLocationRegistry.setRegister(uuid, Location.class, playerLocation);
			
			player.teleport(playerLocation);
			
			sender.sendMessage(player.getName() + " is now on display.");
		}
	}
}
