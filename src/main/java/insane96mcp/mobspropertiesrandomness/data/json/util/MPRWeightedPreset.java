package insane96mcp.mobspropertiesrandomness.data.json.util;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.weightedrandom.IWeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject_old;
import insane96mcp.mobspropertiesrandomness.data.json.MPRPresetOld;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import static insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener.MPR_PRESETS;

public class MPRWeightedPreset implements IMPRObject_old, IWeightedRandom {

	@JsonAdapter(ResourceLocation.Serializer.class)
	public transient ResourceLocation id;

	@SerializedName("weight")
	private MPRModifiableValue modifiableWeight;

	private transient int _weight;

	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

	@Override
	public void validate() throws JsonValidationException {
		if (this.id == null)
			throw new JsonValidationException("Missing id in Weighted Preset. " + this);
		boolean found = false;
		for (MPRPresetOld preset : MPR_PRESETS) {
			if (preset.id.equals(this.id)) {
				found = true;
				break;
			}
		}
		if (!found)
			Logger.info("Preset " + this.id + " does not exist");

		if (this.modifiableWeight == null) {
			Logger.info("Weight value missing for %s, will default to 1", this);
			this.modifiableWeight = new MPRModifiableValue(1f);
		}
		this.modifiableWeight.validate();

		if (this.worldWhitelist != null)
			this.worldWhitelist.validate();
	}

	/**
	 * Returns this MPRWeightedPreset with the weight calculated based off the modifiers, or null if the world whitelist doesn't match
	 */
	@Nullable
	public MPRWeightedPreset computeAndGet(LivingEntity entity, Level world) {
		if (this.worldWhitelist != null && !this.worldWhitelist.isWhitelisted(entity))
			return null;

		this._weight = (int) this.modifiableWeight.getValue(entity, world);

		return this;
	}

	@Override
	public String toString() {
		return String.format("WeightedPreset{id: %s, weight: %s, world_whitelist: %s}", this.id, this.modifiableWeight, this.worldWhitelist);
	}

	@Override
	public int getWeight() {
		return this._weight;
	}
}
