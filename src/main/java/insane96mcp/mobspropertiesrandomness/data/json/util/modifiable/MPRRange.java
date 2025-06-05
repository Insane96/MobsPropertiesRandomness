package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

@JsonAdapter(MPRRange.Serializer.class)
public class MPRRange extends MPRModifiableValue {
	private final Double max;
	@SerializedName("modifiers_behaviour")
	private ModifiersBehaviour modifiersBehaviour;

	public MPRRange(Double value) {
		this(value, null, null, List.of(), null);
	}

	public MPRRange(Double min, @Nullable Double max, @Nullable ModifiersBehaviour modifiersBehaviour, List<MPRModifier> conditionsModifier, @Nullable Integer round) {
		super(min, conditionsModifier, round);
        this.max = max != null ? max : min;
		this.modifiersBehaviour = modifiersBehaviour;
		if (this.modifiersBehaviour == null)
			this.modifiersBehaviour = ModifiersBehaviour.BOTH;
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
		return Mth.nextDouble(entity.level().random, this.applyModifiers(this.value, entity), this.applyModifiers(this.max, entity));
	}

	/**
	 * Returns a random int value between min and max
	 */
	public int getIntBetween(LivingEntity entity) {
		return Mth.nextInt(entity.level().random, (int) this.getMin(entity), (int) this.getMax(entity));
	}

	public boolean isBetween(LivingEntity entity, double value) {
		return value >= this.getMin(entity) && value <= this.getMax(entity);
	}

	public static class Serializer implements JsonSerializer<MPRRange>, JsonDeserializer<MPRRange> {
		@Override
		public MPRRange deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonPrimitive())
				return new MPRRange(json.getAsDouble(), null, null, List.of(), null);
			JsonObject jObject = json.getAsJsonObject();
			double min = 0;
			if (!jObject.has("min")) {
				if (!jObject.has("value"))
					throw new JsonParseException("Missing min or value");
				else min = GsonHelper.getAsDouble(jObject, "value");
			}
			else {
				min = GsonHelper.getAsDouble(jObject, "min");
			}
			return new MPRRange(
					min,
					GsonHelper.getAsDouble(jObject, "max", min),
					GsonHelper.getAsObject(jObject, "modifiers_behaviour", null, context, ModifiersBehaviour.class),
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
}