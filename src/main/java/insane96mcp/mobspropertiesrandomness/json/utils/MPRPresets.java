package insane96mcp.mobspropertiesrandomness.json.utils;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.MPRPreset;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.WeightedRandom;
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
		if (list == null)
			throw new InvalidJsonException("Missing list in Presets. " + this, file);
	}

	public boolean apply(MobEntity entity, World world) {
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

	private List<MPRWeightedPreset> getPresets(MobEntity entity, World world){
		ArrayList<MPRWeightedPreset> weightedPresets = new ArrayList<>();
		for (MPRWeightedPreset weightedPreset : this.list) {
			MPRWeightedPreset mprWeightedPreset = weightedPreset.getPresetWithModifiedWeight(entity, world);
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
	public MPRWeightedPreset getRandomPreset(MobEntity entity, World world) {
		List<MPRWeightedPreset> items = getPresets(entity, world);
		if (items.isEmpty())
			return null;
		return WeightedRandom.getRandomItem(world.random, items);
	}

	public enum Mode {
		EXCLUSIVE,
		BEFORE,
		AFTER
	}

	@Override
	public String toString() {
		return String.format("Presets{chance: %s, list: %s}", chance, list);
	}
}