package insane96mcp.mobspropertiesrandomness.json.utils;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.MPRPreset;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

import java.io.File;

import static insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener.MPR_PRESETS;

public class MPRWeightedPreset extends WeightedRandom.Item implements IMPRObject {
	public String name;
	private MPRModifiableValue weight;

	//TODO MPRWorldWhitelist

	public MPRWeightedPreset(int itemWeightIn) {
		super(itemWeightIn);
	}

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (this.name == null)
			throw new InvalidJsonException("Missing name in Weighted Preset. " + this, file);
		boolean found = false;
		for (MPRPreset preset : MPR_PRESETS) {
			if (preset.name.equals(this.name)) {
				found = true;
				continue;
			}
		}
		if (!found)
			Logger.info("Preset " + this.name + " does not exist");

		if (this.weight == null)
			throw new InvalidJsonException("Missing weight in Weighted Preset. " + this, file);
		this.weight.validate(file);
	}

	/**
	 * Returns an MPRItem with the weight modifier applied to the item's weight
	 * @param world
	 * @return
	 */
	public MPRWeightedPreset getPresetWithModifiedWeight(MobEntity entity, World world) {
		MPRWeightedPreset weightedPreset = this.copy();

		weightedPreset.itemWeight = (int) this.weight.getValue(entity, world);

		return weightedPreset;
	}

	protected MPRWeightedPreset copy() {
		MPRWeightedPreset mprWeightedPreset = new MPRWeightedPreset(this.itemWeight);
		mprWeightedPreset.name = this.name;
		mprWeightedPreset.weight = this.weight;
		return mprWeightedPreset;
	}

	@Override
	public String toString() {
		return String.format("WeightedPreset{name: %s, weight: %s}", name, weight);
	}
}
