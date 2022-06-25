package insane96mcp.mobspropertiesrandomness.json.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.util.modifier.MPRModifiable;
import insane96mcp.mobspropertiesrandomness.json.util.modifier.MPRPosModifier;
import insane96mcp.mobspropertiesrandomness.json.util.modifier.MPRTimeExistedModifier;
import insane96mcp.mobspropertiesrandomness.json.util.modifier.difficulty.MPRDifficultyModifier;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@JsonAdapter(MPRRange.Deserializer.class)
public class MPRRange extends MPRModifiable implements IMPRObject {
	private Float min;
	private Float max;

	public MPRRange(float min, @Nullable Float max, @Nullable MPRDifficultyModifier difficultyModifier, @Nullable MPRPosModifier posModifier, @Nullable MPRTimeExistedModifier timeExistedModifier, @Nullable Integer round) {
		super(difficultyModifier, posModifier, timeExistedModifier, round);
		this.min = min;
		if (max != null)
			this.max = Math.max(min, max);
	}

	public MPRRange(float min, @Nullable Float max) {
		this(min, max, null, null, null, null);
	}

	public MPRRange(float min) {
		this(min, min);
	}

	public void validate() throws JsonValidationException {
		if (min == null)
			throw new JsonValidationException("Missing min. " + this);

		if (max == null) {
			Logger.info("Missing max for " + this + ". Max will be equal to min.");
			max = min;
		}

		if (max < min)
			throw new JsonValidationException("Min cannot be greater than max. " + this);

		super.validate();
	}

	public float getMin(LivingEntity entity, Level world) {
		float min = this.min;

		if (this.difficultyModifier != null && !this.difficultyModifier.affectsMaxOnly)
			min = this.difficultyModifier.applyModifier(world.getDifficulty(), world.getCurrentDifficultyAt(entity.blockPosition()).getEffectiveDifficulty(), min);

		if (this.posModifier != null)
			min = this.posModifier.applyModifier(world, entity.position(), min);

		if (this.timeExistedModifier != null && !this.timeExistedModifier.affectsMaxOnly)
			min = this.timeExistedModifier.applyModifier(world, entity, min);

		return min;
	}

	public float getMax(LivingEntity entity, Level world) {
		float max = this.max;

		if (this.difficultyModifier != null)
			max = this.difficultyModifier.applyModifier(world.getDifficulty(), world.getCurrentDifficultyAt(entity.blockPosition()).getEffectiveDifficulty(), max);

		if (this.posModifier != null)
			max = this.posModifier.applyModifier(world, entity.position(), max);

		if (this.timeExistedModifier != null)
			max = this.timeExistedModifier.applyModifier(world, entity, max);

		return max;
	}

	/**
	 * Returns a random float value between min and max
	 */
	public float getFloatBetween(LivingEntity entity, Level world) {
		return this.round(Mth.nextFloat(world.random, this.getMin(entity, world), this.getMax(entity, world)));
	}

	/**
	 * Returns a random int value between min and max
	 */
	public int getIntBetween(LivingEntity entity, Level world) {
		return Mth.nextInt(world.random, (int) this.getMin(entity, world), (int) this.getMax(entity, world));
	}

	@Override
	public String toString() {
		return String.format("Range{min: %f, max: %f, %s}", min, max, super.toString());
	}

	public static class Deserializer implements JsonDeserializer<MPRRange> {
		@Override
		public MPRRange deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonPrimitive())
				return new MPRRange(json.getAsFloat());
			return new MPRRange(json.getAsJsonObject().get("min").getAsFloat(), context.deserialize(json.getAsJsonObject().get("max"), Float.class), context.deserialize(json.getAsJsonObject().get("difficulty_modifier"), MPRDifficultyModifier.class), context.deserialize(json.getAsJsonObject().get("pos_modifier"), MPRPosModifier.class), context.deserialize(json.getAsJsonObject().get("time_existed_modifier"), MPRTimeExistedModifier.class), context.deserialize(json.getAsJsonObject().get("round"), Integer.class));
		}
	}
}
