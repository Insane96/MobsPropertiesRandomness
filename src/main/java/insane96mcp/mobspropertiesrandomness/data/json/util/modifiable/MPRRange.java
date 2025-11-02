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
public class MPRRange extends MPRModifiable {
	public final MPRModifiableValue min;
	public final MPRModifiableValue max;
	private final Bias bias;

	public static final MPRRange ZERO = new MPRRange(MPRModifiableValue.ZERO);
	public static final MPRRange ONE = new MPRRange(MPRModifiableValue.ONE);
    /**
     * Range with min set to -Double.MAX_VALUE and max set to Double.MAX_VALUE
     */
	public static final MPRRange UNLIMITED = new MPRRange(-Double.MAX_VALUE, Double.MAX_VALUE);

    public MPRRange(MPRModifiableValue value) {
        this(value, null, Bias.NONE, List.of(), null);
    }

    public MPRRange(double value) {
        this(new MPRModifiableValue(value));
    }

    public MPRRange(MPRModifiableValue min, MPRModifiableValue max) {
        this(min, max, Bias.NONE, List.of(), null);
    }

    public MPRRange(double min, double max) {
        this(new MPRModifiableValue(min), new MPRModifiableValue(max), Bias.NONE, List.of(), null);
    }

	public MPRRange(MPRModifiableValue min, @Nullable MPRModifiableValue max, Bias bias, List<MPRModifier> modifiers, @Nullable Integer round) {
		super(modifiers, round);
		this.min = min;
        this.max = max != null ? max : min;
		this.bias = bias;
	}

	public double getMin(LivingEntity living) {
		return this.applyModifiers(this.min.getValue(living), living);
	}

	public double getMax(LivingEntity living) {
		return this.applyModifiers(this.max.getValue(living), living);
	}

	/**
	 * Returns a random double value between min and max
	 */
	public double getDoubleBetween(LivingEntity entity) {
		RandomSource random = entity.level().random;
		double min = this.getMin(entity);
		double max = this.getMax(entity);
		if (min > max)
			return min;
        if (this.bias == Bias.NONE)
            return Mth.nextDouble(random, min, max);
		if (this.bias == Bias.MIDDLE)
			return random.triangle((min + max) / 2.0, (max - min) / 2.0);
		//If MIN or MAX
		double t = random.nextDouble();
		double biased = random.nextDouble() * t;
		return this.bias == Bias.MIN
				? min + biased * (max - min)
				: max - biased * (max - min);
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MPRRange range && this.min.equals(range.min) && this.max.equals(range.max) && this.bias == range.bias && this.modifiers.equals(range.modifiers) && Objects.equals(this.round, range.round);
    }

    public static class Serializer implements JsonSerializer<MPRRange>, JsonDeserializer<MPRRange> {
		@Override
		public MPRRange deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonPrimitive())
				return new MPRRange(json.getAsDouble());
			JsonObject jObject = json.getAsJsonObject();
			MPRModifiableValue min;
			if (!jObject.has("min")) {
				if (!jObject.has("value"))
					throw new JsonParseException("Missing min or value");
				else
					min = GsonHelper.getAsObject(jObject, "value", context, MPRModifiableValue.class);
			}
			else
				min = GsonHelper.getAsObject(jObject, "min", context, MPRModifiableValue.class);
			MPRModifiableValue max = GsonHelper.getAsObject(jObject, "max", min, context, MPRModifiableValue.class);

			Bias bias = jObject.has("bias")
					? GsonHelper.getAsObject(jObject, "bias", context, Bias.class)
					: Bias.NONE;
			return new MPRRange(
					min,
					max,
					bias,
					deserializeList(jObject, context),
					GsonHelper.getAsObject(jObject, "round", null, context, Integer.class)
			);
		}

		@Override
		public JsonElement serialize(MPRRange src, Type typeOfSrc, JsonSerializationContext context) {
			if (Objects.equals(src.min, src.max) && src.modifiers.isEmpty() && src.round == null)
				return new JsonPrimitive(src.min.value);
			JsonObject jObject = new JsonObject();
			jObject.add("min", context.serialize(src.min));
			if (!Objects.equals(src.min, src.max))
				jObject.add("max", context.serialize(src.max));
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
		MAX,
		@SerializedName("middle")
		MIDDLE
	}
}