package me.egg82.tcpp.util;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;

public class DisplayHelper {
	//vars
	private ArrayList<Location> addedBlockLocations = new ArrayList<Location>();
	
	//constructor
	public DisplayHelper() {
		
	}
	
	//public
	public void surround(Location loc, Material blockMaterial, Material sideMaterial) {
		loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		
		for (int x = -1; x < 2; x++) {
			for (int z = -1; z < 2; z++) {
				for (int y = -1; y < 3; y++) {
					// Player is here, don't add blocks
					if (x == 0 && z == 0 && (y == 0 || y == 1)) {
						continue;
					}
					
					Location newLoc = loc.clone().add(x, y, z);
					if (newLoc.getBlock().getType() == Material.AIR) {
						if (y == -1 || y == 2) {
							// Top or bottom
							newLoc.getBlock().setType(blockMaterial);
						} else {
							// Sides
							newLoc.getBlock().setType(sideMaterial);
						}
						
						addedBlockLocations.add(newLoc);
					}
				}
			}
		}
	}
	public void unsurround(Location loc) {
		loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		
		for (int x = -1; x < 2; x++) {
			for (int z = -1; z < 2; z++) {
				for (int y = -1; y < 3; y++) {
					// Player is here, don't remove blocks
					if (x == 0 && z == 0 && (y == 0 || y == 1)) {
						continue;
					}
					
					Location newLoc = loc.clone().add(x, y, z);
					if (addedBlockLocations.contains(newLoc)) {
						newLoc.getBlock().setType(Material.AIR);
						addedBlockLocations.remove(newLoc);
					}
				}
			}
		}
	}
	
	//private
	
}
