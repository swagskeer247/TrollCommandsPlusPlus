package me.egg82.tcpp.commands;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.egg82.events.patterns.command.CommandEvent;
import com.egg82.patterns.ServiceLocator;
import com.egg82.plugin.commands.PluginCommand;
import com.egg82.registry.interfaces.IRegistry;

import me.egg82.tcpp.enums.CommandErrorType;
import me.egg82.tcpp.enums.MessageType;
import me.egg82.tcpp.enums.PermissionsType;
import me.egg82.tcpp.enums.PluginServiceType;

public class FreezeCommand extends PluginCommand {
	//vars
	IRegistry freezeRegistry = (IRegistry) ServiceLocator.getService(PluginServiceType.FREEZE_REGISTRY);
	
	//constructor
	public FreezeCommand(CommandSender sender, Command command, String label, String[] args) {
		super(sender, command, label, args);
	}
	
	//public
	
	//private
	protected void execute() {
		if (sender instanceof Player && !permissionsManager.playerHasPermission((Player) sender, PermissionsType.COMMAND_USE)) {
			sender.sendMessage(MessageType.NO_PERMISSIONS);
			dispatch(CommandEvent.ERROR, CommandErrorType.NO_PERMISSIONS);
			return;
		}
		
		if (args.length == 1) {
			freeze(args[0], Bukkit.getPlayer(args[0]));
		} else {
			sender.sendMessage(MessageType.INCORRECT_USAGE);
			sender.getServer().dispatchCommand(sender, "help " + command.getName());
			dispatch(CommandEvent.ERROR, CommandErrorType.INCORRECT_USAGE);
		}
	}
	private void freeze(String name, Player player) {
		if (player == null) {
			sender.sendMessage(MessageType.PLAYER_NOT_FOUND);
			dispatch(CommandEvent.ERROR, CommandErrorType.PLAYER_NOT_FOUND);
			return;
		}
		if (permissionsManager.playerHasPermission(player, PermissionsType.IMMUNE)) {
			sender.sendMessage(MessageType.PLAYER_IMMUNE);
			dispatch(CommandEvent.ERROR, CommandErrorType.PLAYER_IMMUNE);
			return;
		}
		
		if (ArrayUtils.contains(freezeRegistry.registryNames(), name.toLowerCase())) {
			sender.sendMessage(name + " is no longer frozen.");
			freezeRegistry.setRegister(name.toLowerCase(), null);
		} else {
			sender.sendMessage(name + " is now frozen.");
			freezeRegistry.setRegister(name.toLowerCase(), player);
		}
		
		dispatch(CommandEvent.COMPLETE, null);
	}
}
