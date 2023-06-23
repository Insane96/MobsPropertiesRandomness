package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRRange.Deserializer.class)
public class MPRRange implements IMPRObject {
	private MPRModifiableValue min;
	private MPRModifiableValue max;

	public MPRRange(MPRModifiableValue min, @Nullable MPRModifiableValue max, boolean applyMinModifiersToMax) {
		this.min = min;
		if (max != null)
			this.max = max;
		else
			this.max = this.min;
		this.applyMinModifiersToMax = applyMinModifiersToMax;
	}

	public void validate() throws JsonValidationException {
		if (this.min == null)
			throw new JsonValidationException("Missing min. " + this);
		else
			this.min.validate();

		if (this.max == null) {
			Logger.debug("Missing max for " + this + ". Max will be equal to min.");
			this.max = this.min;
		}
		else
			this.max.validate();
	}

	public float getMin(LivingEntity entity) {
		return this.min.getValue(entity);
		float min = this.min;

		if (this.difficultyModifier != null && !this.difficultyModifier.doesAffectMaxOnly())
			min = this.difficultyModifier.applyModifier(entity, min);

		if (this.posModifier != null && !this.posModifier.doesAffectMaxOnly())
			min = this.posModifier.applyModifier(entity, min);

		if (this.timeExistedModifier != null && !this.timeExistedModifier.doesAffectMaxOnly())
			min = this.timeExistedModifier.applyModifier(entity, min);

		if (this.conditionModifiers != null) {
			for (MPRConditionModifier conditionModifier : this.conditionModifiers) {
				if (!conditionModifier.doesAffectMaxOnly()) {
					min = conditionModifier.applyModifier(entity, min);
				}
			}
		}

		return min;
	}

	public float getMax(LivingEntity entity, Level level) {
		float max = this.max;

		if (this.difficultyModifier != null)
			max = this.difficultyModifier.applyModifier(level.getDifficulty(), max);

		if (this.posModifier != null)
			max = this.posModifier.applyModifier(level, entity.position(), max);

		if (this.timeExistedModifier != null)
			max = this.timeExistedModifier.applyModifier(level, entity, max);

		if (this.conditionModifiers != null) {
			for (MPRConditionModifier conditionModifier : this.conditionModifiers) {
				max = conditionModifier.applyModifier(entity, max);
			}
		}

		return max;
	}

	/**
	 * Returns a random float value between min and max
	 */
	public float getFloat(LivingEntity entity, Level level) {
		return this.round(Mth.nextFloat(level.random, this.getMin(entity, level), this.getMax(entity, level)));
	}

	/**
	 * Returns a random int value between min and max
	 */
	public int getInt(LivingEntity entity, Level level) {
		return Mth.nextInt(level.random, (int) this.getMin(entity, level), (int) this.getMax(entity, level));
	}

	@Override
	public String toString() {
		return String.format("Range{min: %f, max: %f, %s}", this.min, this.max, super.toString());
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
					context.deserialize(json.getAsJsonObject().get("difficulty_modifier"), MPRDifficultyModifier.class),
					context.deserialize(json.getAsJsonObject().get("pos_modifier"), MPRPosModifier.class),
					context.deserialize(json.getAsJsonObject().get("time_existed_modifier"), MPRTimeExistedModifier.class),
					context.deserialize(json.getAsJsonObject().get("conditions_modifier"), new TypeToken<List<MPRConditionModifier>>() {}.getType()),
					context.deserialize(json.getAsJsonObject().get("round"), Integer.class));
		}
	}


}