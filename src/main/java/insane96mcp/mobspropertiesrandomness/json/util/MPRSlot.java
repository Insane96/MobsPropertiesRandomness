package insane96mcp.mobspropertiesrandomness.json.util;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import insane96mcp.mobspropertiesrandomness.util.weightedrandom.WeightedRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MPRSlot implements IMPRObject {

	@SerializedName("override")
	public boolean override;
	@SerializedName("replace_only")
	public boolean replaceOnly;
	public MPRModifiableValue chance;
	public List<MPRItem> items;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (chance != null)
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

	private List<MPRItem> getItems(LivingEntity entity, World world){
		ArrayList<MPRItem> items = new ArrayList<>();
		for (MPRItem item : this.items) {
			MPRItem mprItem = item.computeAndGet(entity, world);
			if (mprItem != null)
				items.add(mprItem);
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
		List<MPRItem> items = getItems(entity, world);
		if (items.isEmpty())
			return null;
		return WeightedRandom.getRandomItem(world.random, items);
	}

	@Override
	public String toString() {
		return String.format("Slot{override: %s, replace_only: %s, chance: %s, items: %s}", override, replaceOnly, chance, items);
	}
}
