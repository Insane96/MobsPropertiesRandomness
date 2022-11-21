package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.weightedrandom.WeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.MPRPreset;
import insane96mcp.mobspropertiesrandomness.data.json.util.MPRWeightedPreset;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener.MPR_PRESETS;

public class MPRPresets implements IMPRObject {

	public MPRModifiableValue chance;
	public Mode mode;
	public List<MPRWeightedPreset> list;

	@Override
	public void validate() throws JsonValidationException {
		if (this.chance != null)
			this.chance.validate();
		if (this.mode == null)
			this.mode = Mode.EXCLUSIVE;
		if (list == null || list.size() == 0)
			throw new JsonValidationException("Missing or empty list in Presets. " + this);
		else {
			for (MPRWeightedPreset weightedPreset : this.list) {
				weightedPreset.validate();
			}
		}
	}

	public boolean apply(LivingEntity entity, Level world) {
		if (this.chance != null && world.random.nextDouble() >= this.chance.getValue(entity, world))
			return false;

		MPRWeightedPreset weightedPreset = this.getRandomPreset(entity, world);
		if (weightedPreset == null)
			return false;
		for (MPRPreset preset : MPR_PRESETS) {
			if (!preset.name.equals(weightedPreset.name))
				continue;

			return preset.apply(entity, world);
		}
		return false;
	}

	private List<MPRWeightedPreset> getPresets(LivingEntity entity, Level world){
		ArrayList<MPRWeightedPreset> weightedPresets = new ArrayList<>();
		for (MPRWeightedPreset weightedPreset : this.list) {
			MPRWeightedPreset mprWeightedPreset = weightedPreset.computeAndGet(entity, world);
			if (mprWeightedPreset != null)
				weightedPresets.add(mprWeightedPreset);
		}
		return weightedPresets;
	}

	/**
	 * Returns a random item from the pool based of weights, dimensions whitelist and biomes whitelist
	 * @param entity
	 * @param world
	 * @return a WeightedPreset or null if no items were available
	 */
	@Nullable
	public MPRWeightedPreset getRandomPreset(LivingEntity entity, Level world) {
		List<MPRWeightedPreset> items = getPresets(entity, world);
		if (items.isEmpty())
			return null;
		return WeightedRandom.getRandomItem(world.random, items);
	}

	public enum Mode {
		@SerializedName("exclusive")
		EXCLUSIVE,
		@SerializedName("before")
		BEFORE,
		@SerializedName("after")
		AFTER
	}

	@Override
	public String toString() {
		return String.format("Presets{chance: %s, list: %s}", chance, list);
	}
}