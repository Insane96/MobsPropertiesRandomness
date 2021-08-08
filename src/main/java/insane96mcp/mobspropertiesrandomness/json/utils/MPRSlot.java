package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import insane96mcp.mobspropertiesrandomness.utils.MPRUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MPRSlot implements IMPRObject {

	//TODO Rename to override?
	@SerializedName("override_vanilla")
	public boolean overrideVanilla;
	@SerializedName("replace_only")
	public boolean replaceOnly;
	public MPRChance chance;
	public List<MPRItem> items;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (chance != null)
			chance.validate(file);
		else
			throw new InvalidJsonException("Missing Chance for " + this, file);

		if (replaceOnly && !overrideVanilla)
		{
			Logger.debug("override_vanilla has been set to true since replace_only is true for " + this);
			overrideVanilla = true;
		}

		if (items == null || items.isEmpty())
			throw new InvalidJsonException("There's no item set or item is missing in the slot " + this, file);
		else {
			for (MPRItem item : items) {
				item.validate(file);
			}
		}
	}

	private List<MPRItem> getItemsWithWeightDifficulty(LivingEntity entity, World world){
		ArrayList<MPRItem> items = new ArrayList<>();
		for (MPRItem item : this.items) {
			if (MPRUtils.doesDimensionMatch(entity, item.dimensionsList) && MPRUtils.doesBiomeMatch(entity, item.biomesList))
				items.add(item.getModifiedWeightItem(world));
		}
		return items;
	}

	/**
	 * Returns a random item from the pool based of weights, dimensions whitelist and biomes whitelist
	 * @param entity
	 * @param world
	 * @return an Item or null if no items were available
	 */
	public MPRItem getRandomItem(LivingEntity entity, World world) {
		List<MPRItem> items = getItemsWithWeightDifficulty(entity, world);
		if (items.isEmpty())
			return null;
		return WeightedRandom.getRandomItem(world.rand, items);
	}

	@Override
	public String toString() {
		return String.format("Slot{override_vanilla: %s, replace_only: %s, chance: %s, items: %s}", overrideVanilla, replaceOnly, chance, items);
	}
}
