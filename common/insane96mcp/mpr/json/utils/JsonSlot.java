package insane96mcp.mpr.json.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.lib.Logger;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class JsonSlot implements IJsonObject{

	@SerializedName("override_vanilla")
	public boolean overrideVanilla;
	@SerializedName("replace_only")
	public boolean replaceOnly;
	public JsonChance chance;
	public ArrayList<JsonItem> items;
	
	@Override
	public String toString() {
		return String.format("Slot{overrideVanilla: %s, replaceOnly: %s, chance: %s, items: %s}", overrideVanilla, replaceOnly, chance, items);
	}
	
	private List<JsonItem> GetItemsWithWeightDifficulty(World world, BlockPos pos){
		ArrayList<JsonItem> items = new ArrayList<JsonItem>();
		for (JsonItem item : this.items) {
			if (item.HasDimension(world) && item.HasBiome(world, pos))
				items.add(item.GetWeightWithDifficulty(world));
		}
		return items;
	}
	
	/**
	 * Returns a random item from the pool based of weights, dimensions whitelist and biomes whitelist
	 * @param world
	 * @param pos
	 * @return an Item or null if no items were available
	 */
	public JsonItem GetRandomItem(World world, BlockPos pos) {
		List<JsonItem> items = GetItemsWithWeightDifficulty(world, pos);
		if (items.isEmpty())
			return null;
		return WeightedRandom.getRandomItem(world.rand, items);
	}

	public void Validate(final File file) throws InvalidJsonException{
		if (chance != null)
			chance.Validate(file);
		else
			throw new InvalidJsonException("Missing Chance for " + this, file);
		
		if (replaceOnly && !overrideVanilla)
		{
			Logger.Debug("overrideVanilla has been set to true since replaceOnly is true for " + this);
			overrideVanilla = true;
		}
		
		if (items == null || items.isEmpty())
			throw new InvalidJsonException("There's no item set or item is missing in the slot " + this, file);
		else {
			for (JsonItem item : items) {
				item.Validate(file);
			}
		}
	}
}
