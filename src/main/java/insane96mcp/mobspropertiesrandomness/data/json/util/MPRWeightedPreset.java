package insane96mcp.mobspropertiesrandomness.data.json.util;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.weightedrandom.IWeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.MPRPreset;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import static insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener.MPR_PRESETS;

public class MPRWeightedPreset implements IMPRObject, IWeightedRandom {
	public String name;

	@SerializedName("weight")
	private MPRModifiableValue modifiableWeight;

	private transient int _weight;

	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

	@Override
	public void validate() throws JsonValidationException {
		if (this.name == null)
			throw new JsonValidationException("Missing name in Weighted Preset. " + this);
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
			throw new JsonValidationException("Missing weight in Weighted Preset. " + this);
		this.modifiableWeight.validate();

		if (worldWhitelist != null)
			worldWhitelist.validate();
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
