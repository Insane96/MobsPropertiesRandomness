package insane96mcp.mobspropertiesrandomness.json.util;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.MPRPreset;
import insane96mcp.mobspropertiesrandomness.util.weightedrandom.WeightedRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener.MPR_PRESETS;

public class MPRPresets implements IMPRObject {

	public MPRModifiableValue chance;
	public Mode mode;
	public List<MPRWeightedPreset> list;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (this.chance != null)
			this.chance.validate(file);
		if (this.mode == null)
			this.mode = Mode.EXCLUSIVE;
		if (list == null || list.size() == 0)
			throw new InvalidJsonException("Missing or empty list in Presets. " + this, file);
		else {
			for (MPRWeightedPreset weightedPreset : this.list) {
				weightedPreset.validate(file);
			}
		}
	}

	public boolean apply(LivingEntity entity, World world) {
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

	private List<MPRWeightedPreset> getPresets(LivingEntity entity, World world){
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
	public MPRWeightedPreset getRandomPreset(LivingEntity entity, World world) {
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