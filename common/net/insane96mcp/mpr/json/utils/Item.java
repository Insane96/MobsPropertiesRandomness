package net.insane96mcp.mpr.json.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.insane96mcp.mpr.lib.Logger;
import net.minecraft.util.WeightedRandom;

public class Item extends WeightedRandom.Item{

	public Item(int itemWeightIn) {
		super(itemWeightIn);
	}

	public String id;
	public int data;
	private int weight;
	@SerializedName("drop_chance")
	public float dropChance;
	public List<Enchantment> enchantments;
	public String nbt;
	
	@Override
	public String toString() {
		return String.format("Item{id: %s, data: %d, weight: %d, dropChance: %f, enchantments: %s, nbt: %s}", id, data, weight, dropChance, enchantments, nbt);
	}

	public void Validate(final File file) throws InvalidJsonException{
		if (id == null)
			throw new InvalidJsonException("Missing Id for " + this, file);
		else if (net.minecraft.item.Item.getByNameOrId(id) == null)
			Logger.Warning("Failed to find item with id " + id);
		
		if (weight <= 0)
			throw new InvalidJsonException("Missing weight (or weight <= 0) for " + this, file);
		else
			itemWeight = weight;
		
		if (dropChance == 0f) {
			Logger.Debug("Drop Chance has been set to 0 (or omitted). Will now default to 8.5f. If you want mobs to not drop this item, set dropChance to -1");
			dropChance = 8.5f;
		}
		else if (dropChance == -1f) {
			Logger.Debug("Drop Chance has been set to -1. Mob no longer drops this item in any case");
			dropChance = Short.MIN_VALUE;
		}
		
		if (enchantments == null)
			enchantments = new ArrayList<Enchantment>();
		
		if (!enchantments.isEmpty()) {
			for (Enchantment enchantment : enchantments) {
				enchantment.Validate(file);
			}
		}
	}
}
