package me.egg82.tcpp.commands.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import me.egg82.tcpp.enums.LanguageType;
import me.egg82.tcpp.enums.PermissionsType;
import me.egg82.tcpp.exceptions.PlayerImmuneException;
import me.egg82.tcpp.services.registries.TrollInventoryRegistry;
import me.egg82.tcpp.services.registries.TrollPageRegistry;
import me.egg82.tcpp.services.registries.TrollPlayerRegistry;
import me.egg82.tcpp.services.registries.TrollSearchRegistry;
import me.egg82.tcpp.util.GuiUtil;
import me.egg82.tcpp.util.MetricsHelper;
import ninja.egg82.events.CompleteEventArgs;
import ninja.egg82.events.ExceptionEventArgs;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.PluginCommand;
import ninja.egg82.plugin.enums.SpigotLanguageType;
import ninja.egg82.plugin.exceptions.IncorrectCommandUsageException;
import ninja.egg82.plugin.exceptions.InvalidPermissionsException;
import ninja.egg82.plugin.exceptions.PlayerNotFoundException;
import ninja.egg82.plugin.exceptions.SenderNotAllowedException;
import ninja.egg82.plugin.utils.CommandUtil;
import ninja.egg82.plugin.utils.LanguageUtil;

public class SearchCommand extends PluginCommand {
	//vars
	private IVariableRegistry<UUID> trollInventoryRegistry = ServiceLocator.getService(TrollInventoryRegistry.class);
	private IVariableRegistry<UUID> trollPlayerRegistry = ServiceLocator.getService(TrollPlayerRegistry.class);
	private IVariableRegistry<UUID> trollPageRegistry = ServiceLocator.getService(TrollPageRegistry.class);
	private IVariableRegistry<UUID> trollSearchRegistry = ServiceLocator.getService(TrollSearchRegistry.class);
	
	private ArrayList<String> commandNames = new ArrayList<String>();
	
	private MetricsHelper metricsHelper = ServiceLocator.getService(MetricsHelper.class);
	
	//constructor
	public SearchCommand() {
		super();
		
		String[] list = ((String) ServiceLocator.getService(JavaPlugin.class).getDescription().getCommands().get("troll").get("usage")).replaceAll("\r\n", "\n").split("\n");
		for (String entry : list) {
			if (entry.contains("-= Available Commands =-")) {
				continue;
			}
			
			commandNames.add(entry.substring(0, entry.indexOf(':')).trim().split(" ")[1]);
		}
	}
	
	//public
	public List<String> tabComplete() {
		if (args.length == 1) {
			ArrayList<String> retVal = new ArrayList<String>();
			
			if (args[0].isEmpty()) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					retVal.add(player.getName());
				}
			} else {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
						retVal.add(player.getName());
					}
				}
			}
			
			return retVal;
		}
		
		return null;
	}
	
	//private
	protected void onExecute(long elapsedMilliseconds) {
		if (!CommandUtil.hasPermission(sender, PermissionsType.COMMAND_SEARCH)) {
			sender.sendMessage(LanguageUtil.getString(SpigotLanguageType.INVALID_PERMISSIONS));
			onError().invoke(this, new ExceptionEventArgs<InvalidPermissionsException>(new InvalidPermissionsException(sender, PermissionsType.COMMAND_SEARCH)));
			return;
		}
		if (args.length == 0) {
			sender.sendMessage(LanguageUtil.getString(SpigotLanguageType.INCORRECT_COMMAND_USAGE));
			String name = getClass().getSimpleName();
			name = name.substring(0, name.length() - 7).toLowerCase();
			sender.getServer().dispatchCommand(sender, "troll help " + name);
			onError().invoke(this, new ExceptionEventArgs<IncorrectCommandUsageException>(new IncorrectCommandUsageException(sender, this, args)));
			return;
		}
		if (!CommandUtil.isPlayer(sender)) {
			sender.sendMessage(LanguageUtil.getString(SpigotLanguageType.SENDER_NOT_ALLOWED));
			onError().invoke(this, new ExceptionEventArgs<SenderNotAllowedException>(new SenderNotAllowedException(sender, this)));
			return;
		}
		
		Player player = CommandUtil.getPlayerByName(args[0]);
		
		if (player == null) {
			sender.sendMessage(LanguageUtil.getString(SpigotLanguageType.PLAYER_NOT_FOUND));
			onError().invoke(this, new ExceptionEventArgs<PlayerNotFoundException>(new PlayerNotFoundException(args[0])));
			return;
		}
		if (CommandUtil.hasPermission(player, PermissionsType.IMMUNE)) {
			sender.sendMessage(LanguageUtil.getString(LanguageType.PLAYER_IMMUNE));
			onError().invoke(this, new ExceptionEventArgs<PlayerImmuneException>(new PlayerImmuneException(player)));
			return;
		}
		
		String search = "";
		if (args.length >= 2) {
			for (int i = 1; i < args.length; i++) {
				search += args[i] + " ";
			}
			search = search.trim();
		}
		
		e(player, ((Player) sender).getUniqueId(), (Player) sender, search);
		
		onComplete().invoke(this, CompleteEventArgs.EMPTY);
	}
	private void e(Player player, UUID senderUuid, Player senderPlayer, String search) {
		Inventory inv = GuiUtil.createInventory(senderPlayer, search, 0);
		trollInventoryRegistry.setRegister(senderUuid, inv);
		trollPlayerRegistry.setRegister(senderUuid, player.getName());
		trollPageRegistry.setRegister(senderUuid, 0);
		trollSearchRegistry.setRegister(senderUuid, search);
		
		senderPlayer.openInventory(inv);
		
		metricsHelper.commandWasRun(this);
	}
	
	protected void onUndo() {
		
	}
}