package net.insane96mcp.mpr.json.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.insane96mcp.mpr.MobsPropertiesRandomness;
import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.minecraft.util.WeightedRandom;

public class Item extends WeightedRandom.Item{

	public Item(int itemWeightIn) {
		super(itemWeightIn);
	}

	public String id;
	public int data;
	private int weight;
	public List<Enchantment> enchantments;
	public String nbt;
	
	@Override
	public String toString() {
		return String.format("Item{id: %s, data: %d, weight: %d, enchantments: %s, nbt: %s}", id, data, weight, enchantments, nbt);
	}

	public void Validate(final File file) throws InvalidJsonException{
		if (id == null)
			throw new InvalidJsonException("Missing Id for " + this, file);
		else if (net.minecraft.item.Item.getByNameOrId(id) == null)
			MobsPropertiesRandomness.Warning("Failed to find item with id " + id);
		
		if (weight <= 0)
			throw new InvalidJsonException("Missing weight (or weight <= 0) for " + this, file);
		else
			itemWeight = weight;
		
		if (enchantments == null)
			enchantments = new ArrayList<Enchantment>();
		
		if (!enchantments.isEmpty()) {
			for (Enchantment enchantment : enchantments) {
				enchantment.Validate(file);
			}
		}
	}
}
