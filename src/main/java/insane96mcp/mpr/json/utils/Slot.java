package insane96mcp.mpr.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.utils.Logger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Slot implements IJsonObject{

	@SerializedName("override_vanilla")
	public boolean overrideVanilla;
	@SerializedName("replace_only")
	public boolean replaceOnly;
	public Chance chance;
	public ArrayList<JsonItem> items;
	
	@Override
	public String toString() {
		return String.format("Slot{overrideVanilla: %s, replaceOnly: %s, chance: %s, items: %s}", overrideVanilla, replaceOnly, chance, items);
	}
	
	private List<JsonItem> getItemsWithWeightDifficulty(World world, BlockPos pos){
		ArrayList<JsonItem> items = new ArrayList<>();
		for (JsonItem item : this.items) {
			if (item.hasDimension(world) && item.hasBiome(world, pos))
				items.add(item.getWeightWithDifficulty(world));
		}
		return items;
	}
	
	/**
	 * Returns a random item from the pool based of weights, dimensions whitelist and biomes whitelist
	 * @param world
	 * @param pos
	 * @return an Item or null if no items were available
	 */
	public JsonItem getRandomItem(World world, BlockPos pos) {
		List<JsonItem> items = getItemsWithWeightDifficulty(world, pos);
		if (items.isEmpty())
			return null;
		return WeightedRandom.getRandomItem(world.rand, items);
	}

	public void validate(final File file) throws InvalidJsonException{
		if (chance != null)
			chance.validate(file);
		else
			throw new InvalidJsonException("Missing Chance for " + this, file);
		
		if (replaceOnly && !overrideVanilla)
		{
			Logger.debug("overrideVanilla has been set to true since replaceOnly is true for " + this);
			overrideVanilla = true;
		}
		
		if (items == null || items.isEmpty())
			throw new InvalidJsonException("There's no item set or item is missing in the slot " + this, file);
		else {
			for (JsonItem item : items) {
				item.validate(file);
			}
		}
	}
}
