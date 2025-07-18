package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

@JsonAdapter(MPRRange.Serializer.class)
public class MPRRange extends MPRModifiableValue {
	private final Double max;
	private final Bias bias;
	private ModifiersBehaviour modifiersBehaviour;

	public static final MPRRange ZERO = new MPRRange(0d);
	public static final MPRRange ONE = new MPRRange(1d);

	public MPRRange(Double value) {
		this(value, null, null, Bias.NONE, List.of(), null);
	}

	public MPRRange(Double min, @Nullable Double max, @Nullable ModifiersBehaviour modifiersBehaviour, Bias bias, List<MPRModifier> conditionsModifier, @Nullable Integer round) {
		super(min, conditionsModifier, round);
        this.max = max != null ? max : min;
		this.modifiersBehaviour = modifiersBehaviour;
		if (this.modifiersBehaviour == null)
			this.modifiersBehaviour = ModifiersBehaviour.BOTH;
		this.bias = bias;
	}

	public double getMin(LivingEntity living) {
		if (this.modifiersBehaviour != ModifiersBehaviour.MAX_ONLY)
			return this.applyModifiersAndRound(this.value, living);
		return this.value;
	}

	public double getMax(LivingEntity living) {
		if (this.modifiersBehaviour != ModifiersBehaviour.MIN_ONLY)
			return this.applyModifiersAndRound(this.max, living);
		return this.max;
	}

	@Override
	public double getValue(LivingEntity living) {
		return this.getDoubleBetween(living);
	}

	/**
	 * Returns a random double value between min and max
	 */
	public double getDoubleBetween(LivingEntity entity) {
		RandomSource random = entity.level().random;
		double min = this.applyModifiers(this.value, entity);
		double max = this.applyModifiers(this.max, entity);
        if (this.bias == Bias.NONE)
            return Mth.nextDouble(random, min, max);
		else {
			double t = random.nextDouble();
			double biased = random.nextDouble() * t;
			return this.bias == Bias.MIN
					? min + biased * (max - min)
					: max - biased * (max - min);
		}
	}

	/**
	 * Returns a random int value between min and max
	 */
	public int getIntBetween(LivingEntity entity) {
		return (int) this.getDoubleBetween(entity);
	}

	public boolean isBetween(LivingEntity entity, double value) {
		return value >= this.getMin(entity) && value <= this.getMax(entity);
	}

	public static class Serializer implements JsonSerializer<MPRRange>, JsonDeserializer<MPRRange> {
		@Override
		public MPRRange deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonPrimitive())
				return new MPRRange(json.getAsDouble());
			JsonObject jObject = json.getAsJsonObject();
			double min;
			if (!jObject.has("min")) {
				if (!jObject.has("value"))
					throw new JsonParseException("Missing min or value");
				else min = GsonHelper.getAsDouble(jObject, "value");
			}
			else {
				min = GsonHelper.getAsDouble(jObject, "min");
			}
			double max = GsonHelper.getAsDouble(jObject, "max", min);
			if (min > max)
				throw new JsonParseException("Min cannot be greater than max");

			Bias bias = jObject.has("bias")
					? GsonHelper.getAsObject(jObject, "bias", context, Bias.class)
					: Bias.NONE;
			return new MPRRange(
					min,
					max,
					GsonHelper.getAsObject(jObject, "modifiers_behaviour", null, context, ModifiersBehaviour.class),
					bias,
					deserializeList(jObject, context),
					GsonHelper.getAsObject(jObject, "round", null, context, Integer.class)
			);
		}

		@Override
		public JsonElement serialize(MPRRange src, Type typeOfSrc, JsonSerializationContext context) {
			if (Objects.equals(src.value, src.max) && src.modifiersBehaviour == ModifiersBehaviour.BOTH && src.modifiers.isEmpty() && src.round == null)
				return new JsonPrimitive(src.value);
			JsonObject jObject = new JsonObject();
			jObject.addProperty("min", src.value);
			jObject.addProperty("max", src.max);
			if (src.modifiersBehaviour != ModifiersBehaviour.BOTH)
				jObject.add("modifiers_behaviour", context.serialize(src.modifiersBehaviour));
			return src.endSerialization(jObject, context);
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

	public enum Bias {
		@SerializedName("none")
		NONE,
		@SerializedName("min")
		MIN,
		@SerializedName("max")
		MAX
	}
}