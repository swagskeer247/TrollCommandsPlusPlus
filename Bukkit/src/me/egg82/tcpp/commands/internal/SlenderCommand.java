package me.egg82.tcpp.commands.internal;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.egg82.tcpp.enums.LanguageType;
import me.egg82.tcpp.enums.PermissionsType;
import me.egg82.tcpp.exceptions.PlayerImmuneException;
import me.egg82.tcpp.util.MetricsHelper;
import ninja.egg82.events.CompleteEventArgs;
import ninja.egg82.events.ExceptionEventArgs;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.commands.PluginCommand;
import ninja.egg82.plugin.enums.SpigotLanguageType;
import ninja.egg82.plugin.exceptions.IncorrectCommandUsageException;
import ninja.egg82.plugin.exceptions.InvalidPermissionsException;
import ninja.egg82.plugin.exceptions.PlayerNotFoundException;
import ninja.egg82.plugin.utils.BlockUtil;
import ninja.egg82.plugin.utils.CommandUtil;
import ninja.egg82.plugin.utils.LanguageUtil;
import ninja.egg82.utils.MathUtil;

public class SlenderCommand extends PluginCommand {
	//vars
	private MetricsHelper metricsHelper = ServiceLocator.getService(MetricsHelper.class);
	
	//constructor
	public SlenderCommand() {
		super();
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
		if (!CommandUtil.hasPermission(sender, PermissionsType.COMMAND_SLENDER)) {
			sender.sendMessage(LanguageUtil.getString(SpigotLanguageType.INVALID_PERMISSIONS));
			onError().invoke(this, new ExceptionEventArgs<InvalidPermissionsException>(new InvalidPermissionsException(sender, PermissionsType.COMMAND_SLENDER)));
			return;
		}
		if (!CommandUtil.isArrayOfAllowedLength(args, 1)) {
			sender.sendMessage(LanguageUtil.getString(SpigotLanguageType.INCORRECT_COMMAND_USAGE));
			String name = getClass().getSimpleName();
			name = name.substring(0, name.length() - 7).toLowerCase();
			sender.getServer().dispatchCommand(sender, "troll help " + name);
			onError().invoke(this, new ExceptionEventArgs<IncorrectCommandUsageException>(new IncorrectCommandUsageException(sender, this, args)));
			return;
		}
		
		List<Player> players = CommandUtil.getPlayers(CommandUtil.parseAtSymbol(args[0], CommandUtil.isPlayer(sender) ? ((Player) sender).getLocation() : null));
		if (players.size() > 0) {
			for (Player player : players) {
				if (CommandUtil.hasPermission(player, PermissionsType.IMMUNE)) {
					continue;
				}
				
				e(player);
			}
		} else {
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
			
			e(player);
		}
		
		onComplete().invoke(this, CompleteEventArgs.EMPTY);
	}
	private void e(Player player) {
		Location playerLocation = player.getLocation();
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 500, 3), true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 500, 3), true);
		
		Location r = BlockUtil.getTopWalkableBlock(new Location(playerLocation.getWorld(), MathUtil.random(playerLocation.getX() - 10.0d, playerLocation.getX() + 10.0d), playerLocation.getY(), MathUtil.random(playerLocation.getZ() - 10.0d, playerLocation.getZ() + 10.0d)));
		Enderman e = player.getWorld().spawn(r, Enderman.class);
		e.setTarget(player);
		
		metricsHelper.commandWasRun(this);
		
		sender.sendMessage(player.getName() + " is now playing Slender.");
	}
	
	protected void onUndo() {
		
	}
}
