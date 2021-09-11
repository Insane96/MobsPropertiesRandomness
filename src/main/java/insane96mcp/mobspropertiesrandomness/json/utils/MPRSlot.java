package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MPRSlot implements IMPRObject {

	@SerializedName("override")
	public boolean override;
	@SerializedName("replace_only")
	public boolean replaceOnly;
	public MPRChance chance;
	public List<MPRItem> items;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (chance == null)
			throw new InvalidJsonException("Missing chance. " + this, file);
		chance.validate(file);

		if (replaceOnly && !override)
		{
			Logger.info("override has been set to true since replace_only is true. " + this);
			override = true;
		}

		if (items == null || items.isEmpty())
			throw new InvalidJsonException("Missing items. " + this, file);
		else {
			for (MPRItem item : items) {
				item.validate(file);
			}
		}
	}

	private List<MPRItem> getItemsWithWeightDifficulty(MobEntity entity, World world){
		ArrayList<MPRItem> items = new ArrayList<>();
		for (MPRItem item : this.items) {
			if (item.worldWhitelist != null && !item.worldWhitelist.isWhitelisted(entity))
				continue;
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
	public MPRItem getRandomItem(MobEntity entity, World world) {
		List<MPRItem> items = getItemsWithWeightDifficulty(entity, world);
		if (items.isEmpty())
			return null;
		return WeightedRandom.getRandomItem(world.rand, items);
	}

	@Override
	public String toString() {
		return String.format("Slot{override: %s, replace_only: %s, chance: %s, items: %s}", override, replaceOnly, chance, items);
	}
}
