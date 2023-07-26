package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRRange.Deserializer.class)
public class MPRRange extends MPRModifiableValue implements IMPRObject {
	private Float max;
	@SerializedName("modifiers_behaviour")
	private ModifiersBehaviour modifiersBehaviour;

	public MPRRange(Float min, @Nullable Float max, @Nullable ModifiersBehaviour modifiersBehaviour, @Nullable MPRDifficultyModifier difficultyModifier, @Nullable MPRWorldSpawnDistanceModifier worldSpawnDistanceModifier, @Nullable MPRDepthModifier depthModifier, @Nullable MPRTimeExistedModifier timeExistedModifier, @Nullable List<MPRConditionModifier> conditionsModifier, @Nullable Integer round) {
		super(min, difficultyModifier, worldSpawnDistanceModifier, depthModifier, timeExistedModifier, conditionsModifier, round);
		if (max != null)
			this.max = max;
		else
			this.max = min;
		this.modifiersBehaviour = modifiersBehaviour;
		if (this.modifiersBehaviour == null)
			this.modifiersBehaviour = ModifiersBehaviour.BOTH;
	}

	public MPRRange(Float min, Float max) {
		this(min, max, null, null, null, null, null, null, null);
	}

	public MPRRange(Float min) {
		this(min, null, null, null, null, null, null, null, null);
	}

	public void validate() throws JsonValidationException {
		if (this.value == null)
			throw new JsonValidationException("Missing \"min\" (or \"value\") in Range Object");

		if (this.max == null) {
			Logger.debug("Missing max for Range Object. Max will be equal to min");
			this.max = this.value;
		}

		if (this.max < this.value)
			throw new JsonValidationException("min cannot be greater than max in Range Object");

		if (this.modifiersBehaviour == null) {
			Logger.debug("Missing modifiers_behaviour for Range Object. modifiers_behaviour will be equal to 'both'");
			this.modifiersBehaviour = ModifiersBehaviour.BOTH;
		}

		super.validate();
	}

	public float getMin(LivingEntity entity) {
		if (this.modifiersBehaviour != ModifiersBehaviour.MAX_ONLY)
			return this.applyModifiersAndRound(entity, this.value);
		return this.value;
	}

	public float getMax(LivingEntity entity) {
		if (this.modifiersBehaviour != ModifiersBehaviour.MIN_ONLY)
			return this.applyModifiersAndRound(entity, this.max);
		return this.max;
	}

	@Override
	public float getValue(LivingEntity entity) {
		return this.getFloatBetween(entity);
	}

	/**
	 * Returns a random float value between min and max
	 */
	public float getFloatBetween(LivingEntity entity) {
		return Mth.nextFloat(entity.level.random, this.applyModifiers(entity, this.value), this.applyModifiers(entity, this.max));
	}

	/**
	 * Returns a random int value between min and max
	 */
	public int getIntBetween(LivingEntity entity) {
		return Mth.nextInt(entity.level.random, (int) this.getMin(entity), (int) this.getMax(entity));
	}

	@Override
	public String toString() {
		return String.format("Range{%s, max: %s}", super.toString(), this.max);
	}

	public static class Deserializer implements JsonDeserializer<MPRRange> {
		@Override
		public MPRRange deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonPrimitive())
				return new MPRRange(json.getAsFloat());
			Float min = context.deserialize(json.getAsJsonObject().get("min"), Float.class);
			if (min == null)
				min = context.deserialize(json.getAsJsonObject().get("value"), Float.class);
			return new MPRRange(min,
					context.deserialize(json.getAsJsonObject().get("max"), Float.class),
					context.deserialize(json.getAsJsonObject().get("modifiers_behaviour"), ModifiersBehaviour.class),
					context.deserialize(json.getAsJsonObject().get("difficulty_modifier"), MPRDifficultyModifier.class),
					context.deserialize(json.getAsJsonObject().get("world_spawn_distance_modifier"), MPRWorldSpawnDistanceModifier.class),
					context.deserialize(json.getAsJsonObject().get("depth_modifier"), MPRDepthModifier.class),
					context.deserialize(json.getAsJsonObject().get("time_existed_modifier"), MPRTimeExistedModifier.class),
					context.deserialize(json.getAsJsonObject().get("conditions_modifier"), new TypeToken<List<MPRConditionModifier>>() {}.getType()),
					context.deserialize(json.getAsJsonObject().get("round"), Integer.class));
		}
	}

	public enum ModifiersBehaviour {
		@SerializedName("both")
		BOTH,
		@SerializedName("min_only")
		MIN_ONLY,
		@SerializedName("max_only")
		MAX_ONLY
	}
}