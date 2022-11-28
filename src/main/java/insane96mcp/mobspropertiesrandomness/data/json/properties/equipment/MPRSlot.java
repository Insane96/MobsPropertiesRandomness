package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.weightedrandom.WeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

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
	public void validate() throws JsonValidationException {
		if (chance != null)
			chance.validate();

		if (replaceOnly && !override)
		{
			Logger.info("override has been set to true since replace_only is true. " + this);
			override = true;
		}

		if (items == null || items.isEmpty())
			throw new JsonValidationException("Missing items. " + this);
		else {
			for (MPRItem item : items) {
				item.validate();
			}
		}
	}

	private List<MPRItem> getItems(LivingEntity entity, Level world){
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
	public MPRItem getRandomItem(LivingEntity entity, Level world) {
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
