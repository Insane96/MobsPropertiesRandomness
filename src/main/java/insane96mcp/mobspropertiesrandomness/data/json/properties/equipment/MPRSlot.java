package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.weightedrandom.WeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class MPRSlot implements IMPRObject {

	@SerializedName("keep_spawned")
	public boolean keepSpawned;
	@SerializedName("replace_only")
	public boolean replaceOnly;
	public MPRModifiableValue chance;
	public List<MPRItem> items;

	@Override
	public void validate() throws JsonValidationException {
		if (this.chance != null)
			this.chance.validate();

		if (this.replaceOnly && this.keepSpawned)
		{
			Logger.debug("keep_spawned has been set to false since replace_only is true. " + this);
			this.keepSpawned = false;
		}

		if (this.items == null || this.items.isEmpty())
			throw new JsonValidationException("Missing items. " + this);
		else {
			for (MPRItem item : this.items) {
				item.validate();
			}
		}
	}

	private List<MPRItem> getItems(LivingEntity entity){
		ArrayList<MPRItem> items = new ArrayList<>();
		for (MPRItem item : this.items) {
			MPRItem mprItem = item.computeAndGet(entity);
			if (mprItem != null)
				items.add(mprItem);
		}
		return items;
	}

	/**
	 * Returns a random item from the pool based of weights, dimensions whitelist and biomes whitelist
	 * @return an Item or null if no items were available
	 */
	public MPRItem getRandomItem(LivingEntity entity) {
		List<MPRItem> items = getItems(entity);
		if (items.isEmpty())
			return null;
		return WeightedRandom.getRandomItem(entity.level().random, items);
	}

	@Override
	public String toString() {
		return String.format("Slot{keep_spawned: %s, replace_only: %s, chance: %s, items: %s}", this.keepSpawned, this.replaceOnly, this.chance, this.items);
	}
}
