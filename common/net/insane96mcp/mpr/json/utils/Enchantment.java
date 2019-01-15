package net.insane96mcp.mpr.json.utils;

import java.io.File;

import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.insane96mcp.mpr.json.IJsonObject;
import net.insane96mcp.mpr.lib.Logger;

public class Enchantment implements IJsonObject{

	public String id;
	public RangeMinMax level;
	public Chance chance;
	
	@Override
	public String toString() {
		return String.format("Enchantment{id: %s, level: %s, chance: %s}", id, level, chance);
	}

	public void Validate(final File file) throws InvalidJsonException{
		if (id == null)
			throw new InvalidJsonException("Missing Enchantment ID for " + this, file);
		else if (net.minecraft.enchantment.Enchantment.getEnchantmentByLocation(id) == null)
			Logger.Warning("Failed to find enchantment with id " + id);
		
		if (level != null)
			level.Validate(file);
		else {
			Logger.Debug("Missing Enchantment Level for " + this + ". Will default to 1");
			level = new RangeMinMax(1, 1);
		}
		
		if (chance != null) 
			chance.Validate(file);
		else
			throw new InvalidJsonException("Missing chance for " + this, file);
		
	}
	
}
