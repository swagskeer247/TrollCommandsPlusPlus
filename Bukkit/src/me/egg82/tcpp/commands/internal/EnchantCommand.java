package me.egg82.tcpp.commands.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.egg82.tcpp.enums.LanguageType;
import me.egg82.tcpp.enums.PermissionsType;
import me.egg82.tcpp.exceptions.InvalidTypeException;
import me.egg82.tcpp.services.databases.EnchantTypeSearchDatabase;
import me.egg82.tcpp.services.registries.EnchantNameRegistry;
import me.egg82.tcpp.util.MetricsHelper;
import ninja.egg82.events.CompleteEventArgs;
import ninja.egg82.events.ExceptionEventArgs;
import ninja.egg82.exceptions.ArgumentNullException;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.patterns.registries.IVariableRegistry;
import ninja.egg82.plugin.commands.PluginCommand;
import ninja.egg82.plugin.enums.SpigotLanguageType;
import ninja.egg82.plugin.exceptions.IncorrectCommandUsageException;
import ninja.egg82.plugin.exceptions.InvalidPermissionsException;
import ninja.egg82.plugin.exceptions.SenderNotAllowedException;
import ninja.egg82.plugin.reflection.player.IPlayerHelper;
import ninja.egg82.plugin.utils.CommandUtil;
import ninja.egg82.plugin.utils.LanguageUtil;
import ninja.egg82.sql.LanguageDatabase;

public class EnchantCommand extends PluginCommand {
	//vars
	private LanguageDatabase enchantTypeDatabase = ServiceLocator.getService(EnchantTypeSearchDatabase.class);
	private IPlayerHelper playerHelper = ServiceLocator.getService(IPlayerHelper.class);
	private IVariableRegistry<String> enchantNameRegistry = ServiceLocator.getService(EnchantNameRegistry.class);
	
	private MetricsHelper metricsHelper = ServiceLocator.getService(MetricsHelper.class);
	
	//constructor
	public EnchantCommand() {
		super();
	}
	
	//public
	public List<String> tabComplete() {
		if(args.length == 1) {
			ArrayList<String> retVal = new ArrayList<String>();
			
			if (args[0].isEmpty()) {
				for (String key : enchantNameRegistry.getKeys()) {
					retVal.add(enchantNameRegistry.getRegister(key, String.class));
				}
			} else {
				for (String key : enchantNameRegistry.getKeys()) {
					String value = enchantNameRegistry.getRegister(key, String.class);
					if (value.toLowerCase().startsWith(args[0].toLowerCase())) {
						retVal.add(value);
					}
				}
			}
			
			return retVal;
		}
		
		return null;
	}
	
	//private
	@SuppressWarnings("deprecation")
	protected void onExecute(long elapsedMilliseconds) {
		if (!CommandUtil.isPlayer(sender)) {
			sender.sendMessage(LanguageUtil.getString(SpigotLanguageType.SENDER_NOT_ALLOWED));
			onError().invoke(this, new ExceptionEventArgs<SenderNotAllowedException>(new SenderNotAllowedException(sender, this)));
			return;
		}
		if (!CommandUtil.hasPermission(sender, PermissionsType.COMMAND_ENCHANT)) {
			sender.sendMessage(LanguageUtil.getString(SpigotLanguageType.INVALID_PERMISSIONS));
			onError().invoke(this, new ExceptionEventArgs<InvalidPermissionsException>(new InvalidPermissionsException(sender, PermissionsType.COMMAND_ENCHANT)));
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
		
		String search = "";
		for (int i = 0; i < args.length; i++) {
			search += args[i] + " ";
		}
		search = search.trim();
		
		Enchantment type = Enchantment.getByName(search.replaceAll("[^a-zA-Z]", "").replaceAll(" ", "_").toUpperCase());
		
		if (type == null) {
			// Effect not found. It's possible it was just misspelled. Search the database.
			String[] types = enchantTypeDatabase.getValues(enchantTypeDatabase.naturalLanguage(search, false), 0);
			
			if (types == null || types.length == 0) {
				sender.sendMessage(LanguageUtil.getString(LanguageType.INVALID_TYPE));
				onError().invoke(this, new ExceptionEventArgs<InvalidTypeException>(new InvalidTypeException(search)));
				return;
			}
			
			type = Enchantment.getByName(types[0].toUpperCase());
			if (type == null) {
				sender.sendMessage(LanguageUtil.getString(LanguageType.INVALID_TYPE));
				onError().invoke(this, new ExceptionEventArgs<InvalidTypeException>(new InvalidTypeException(search)));
				return;
			}
		}
		
		int level = 1;
		try {
			level = Integer.parseInt(args[args.length - 1]);
		} catch (Exception ex) {
			// Ignored
		}
		
		ItemStack item = playerHelper.getItemInMainHand((Player) sender);
		if (item != null && item.getType() != Material.AIR) {
			e(item, type, level);
			((Player) sender).updateInventory();
		}
		
		onComplete().invoke(this, CompleteEventArgs.EMPTY);
	}
	private void e(ItemStack item, Enchantment enchant, int level) {
		if (item.getType() == Material.BOOK) {
			item.setType(Material.ENCHANTED_BOOK);
		}
		
		if (item.getType() == Material.ENCHANTED_BOOK) {
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
			if (meta.hasStoredEnchant(enchant)) {
				meta.removeStoredEnchant(enchant);
				applyLore(meta);
				item.setItemMeta(meta);
				if (meta.getEnchants().size() == 0) {
					item.setType(Material.BOOK);
				}
				sender.sendMessage("The item you are holding is no longer enchanted with " + enchantNameRegistry.getRegister(enchant.getName()) + ".");
			} else {
				meta.addStoredEnchant(enchant, level, true);
				applyLore(meta);
				item.setItemMeta(meta);
				sender.sendMessage("The item you are holding is now enchanted with " + enchantNameRegistry.getRegister(enchant.getName()) + "!");
			}
		} else {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasEnchant(enchant)) {
				meta.removeEnchant(enchant);
				applyLore(meta);
				item.setItemMeta(meta);
				sender.sendMessage("The item you are holding is no longer enchanted with " + enchantNameRegistry.getRegister(enchant.getName()) + ".");
			} else {
				meta.addEnchant(enchant, level, true);
				applyLore(meta);
				item.setItemMeta(meta);
				sender.sendMessage("The item you are holding is now enchanted with " + enchantNameRegistry.getRegister(enchant.getName()) + "!");
			}
		}
		
		metricsHelper.commandWasRun(this);
	}
	
	private void applyLore(ItemMeta meta) {
		if (meta == null) {
			throw new ArgumentNullException("meta");
		}
		
		Map<Enchantment, Integer> enchants = null;
		
		if (meta instanceof EnchantmentStorageMeta) {
			enchants = ((EnchantmentStorageMeta) meta).getStoredEnchants();
		} else {
			enchants = meta.getEnchants();
		}
		
		if (enchants.size() == 0) {
			if (meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)) {
				meta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			}
			if (meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
				meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
		} else {
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS);
		}
		
		ArrayList<Enchantment> loreEnchants = new ArrayList<Enchantment>();
		List<String> lore = new ArrayList<String>();
		if (meta.hasLore()) {
			// Remove any lines that are enchantments but aren't in the enchant list
			for (String line : meta.getLore()) {
				String tempLine = line;
				
				for (ChatColor color : ChatColor.values()) {
					tempLine = tempLine.replace(color.toString(), "");
				}
				
				int lastSpace = tempLine.lastIndexOf(' ');
				
				if (lastSpace == -1) {
					lore.add(line);
					continue;
				}
				String enchantName = tempLine.substring(0, lastSpace);
				if (!enchantNameRegistry.hasValue(enchantName)) {
					lore.add(line);
					continue;
				}
				Enchantment enchant = Enchantment.getByName(enchantNameRegistry.getKey(enchantName));
				if (enchant == null) {
					lore.add(line);
					continue;
				}
				if (enchants.containsKey(enchant)) {
					lore.add(line);
					loreEnchants.add(enchant);
				}
			}
		}
		
		// Add enchant lines to the lore
		for (Entry<Enchantment, Integer> kvp : enchants.entrySet()) {
			if (!loreEnchants.contains(kvp.getKey())) {
				lore.add(((kvp.getKey().isCursed()) ? ChatColor.RED : ChatColor.GRAY) + ((String) enchantNameRegistry.getRegister(kvp.getKey().getName())) + " " + getNumerals(kvp.getValue().intValue()));
				loreEnchants.add(kvp.getKey());
			}
		}
		
		meta.setLore(lore);
	}
	
	private String getNumerals(int level) {
		StringBuilder retVal = new StringBuilder();
		
		if (level < 0) {
			retVal.append('-');
			level *= -1;
		}
		
		while (level >= 1000) {
			retVal.append('M');
			level -= 1000;
		}
		while (level >= 500) {
			retVal.append('D');
			level -= 500;
		}
		while (level >= 100) {
			retVal.append('C');
			level -= 100;
		}
		while (level >= 50) {
			retVal.append('L');
			level -= 50;
		}
		while (level >= 10) {
			retVal.append('X');
			level -= 10;
		}
		while (level >= 5) {
			retVal.append('V');
			level -= 5;
		}
		while (level >= 1) {
			retVal.append('I');
			level -= 1;
		}
		
		return retVal.toString();
	}
	
	protected void onUndo() {
		
	}
}
