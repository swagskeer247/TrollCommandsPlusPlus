package me.egg82.tcpp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.egg82.tcpp.commands.base.BasePluginCommand;
import me.egg82.tcpp.enums.PermissionsType;
import me.egg82.tcpp.enums.PluginServiceType;
import ninja.egg82.events.patterns.command.CommandEvent;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.registry.interfaces.IRegistry;

public class PopupCommand extends BasePluginCommand {
	//vars
	private IRegistry reg = (IRegistry) ServiceLocator.getService(PluginServiceType.POPUP_REGISTRY);
	
	//constructor
	public PopupCommand(CommandSender sender, Command command, String label, String[] args) {
		super(sender, command, label, args);
	}
	
	//public
	public void onLogin(String name, Player player) {
		if (reg.contains(name)) {
			reg.setRegister(name, player);
		}
	}
	
	//private
	protected void execute() {
		if (isValid(false, PermissionsType.COMMAND_POPUP, new int[]{1}, new int[]{0})) {
			Player player = Bukkit.getPlayer(args[0]);
			e(player.getName(), player);
			
			dispatch(CommandEvent.COMPLETE, null);
		}
	}
	private void e(String name, Player player) {
		if (reg.contains(name.toLowerCase())) {
			sender.sendMessage(name + "'s inventory is no longer opening and closing randomly.");
			reg.setRegister(name.toLowerCase(), null);
		} else {
			sender.sendMessage(name + "'s inventory is now opening and closing randomly.");
			reg.setRegister(name.toLowerCase(), player);
		}
	}
}