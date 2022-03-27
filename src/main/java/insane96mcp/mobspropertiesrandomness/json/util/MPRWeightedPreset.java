package insane96mcp.mobspropertiesrandomness.json.util;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.MPRPreset;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import insane96mcp.mobspropertiesrandomness.util.weightedrandom.IWeightedRandom;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.io.File;

import static insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener.MPR_PRESETS;

public class MPRWeightedPreset implements IMPRObject, IWeightedRandom {
	public String name;

	@SerializedName("weight")
	private MPRModifiableValue modifiableWeight;

	private transient int _weight;

	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (this.name == null)
			throw new InvalidJsonException("Missing name in Weighted Preset. " + this, file);
		boolean found = false;
		for (MPRPreset preset : MPR_PRESETS) {
			if (preset.name.equals(this.name)) {
				found = true;
				break;
			}
		}
		if (!found)
			Logger.info("Preset " + this.name + " does not exist");

		if (this.modifiableWeight == null)
			throw new InvalidJsonException("Missing weight in Weighted Preset. " + this, file);
		this.modifiableWeight.validate(file);

		if (worldWhitelist != null)
			worldWhitelist.validate(file);
	}

	/**
	 * Returns this MPRWeightedPreset with the weight calculated based off the modifiers, or null if the world whitelist doesn't match
	 */
	@Nullable
	public MPRWeightedPreset computeAndGet(LivingEntity entity, Level world) {
		if (worldWhitelist != null && !worldWhitelist.isWhitelisted(entity))
			return null;

		this._weight = (int) this.modifiableWeight.getValue(entity, world);

		return this;
	}

	@Override
	public String toString() {
		return String.format("WeightedPreset{name: %s, weight: %s, world_whitelist: %s}", name, modifiableWeight, worldWhitelist);
	}

	@Override
	public int getWeight() {
		return this._weight;
	}
}
