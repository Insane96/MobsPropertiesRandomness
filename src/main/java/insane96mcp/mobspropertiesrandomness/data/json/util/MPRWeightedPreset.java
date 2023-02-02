package insane96mcp.mobspropertiesrandomness.data.json.util;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.weightedrandom.IWeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.MPRPreset;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import static insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener.MPR_PRESETS;

public class MPRWeightedPreset implements IMPRObject, IWeightedRandom {

	@JsonAdapter(ResourceLocation.Serializer.class)
	public ResourceLocation id;

	@SerializedName("weight")
	private MPRModifiableValue modifiableWeight;

	private transient int _weight;

	private MPRModifiableValue chance;

	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

	@Override
	public void validate() throws JsonValidationException {
		if (this.id == null)
			throw new JsonValidationException("Missing id in Weighted Preset. " + this);
		boolean found = false;
		for (MPRPreset preset : MPR_PRESETS) {
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

		if (this.chance != null)
			this.chance.validate();
	}

	/**
	 * Returns this MPRWeightedPreset with the weight calculated based off the modifiers, or null if the world whitelist / chance doesn't match
	 */
	@Nullable
	public MPRWeightedPreset computeAndGet(LivingEntity entity, Level level) {
		if (this.worldWhitelist != null && !this.worldWhitelist.isWhitelisted(entity))
			return null;
		if (this.chance != null && level.random.nextDouble() >= this.chance.getValue(entity, level))
			return null;

		this._weight = (int) this.modifiableWeight.getValue(entity, level);

		return this;
	}

	@Override
	public String toString() {
		return String.format("WeightedPreset{id: %s, weight: %s, world_whitelist: %s, chance: %s}", this.id, this.modifiableWeight, this.worldWhitelist, this.chance);
	}

	@Override
	public int getWeight() {
		return this._weight;
	}
}
