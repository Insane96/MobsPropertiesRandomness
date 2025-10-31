package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRModifiableValue.Serializer.class)
public class MPRModifiableValue extends MPRModifiable {
	public final Double value;

	public static final MPRModifiableValue ZERO = new MPRModifiableValue(0d);
	public static final MPRModifiableValue ONE = new MPRModifiableValue(1d);

	public MPRModifiableValue(Double value) {
		this(value, List.of(), null);
	}

	public MPRModifiableValue(Double value, List<MPRModifier> modifiers, @Nullable Integer round) {
		super(modifiers, round);
		this.value = value;
	}

	public double getValue(LivingEntity living) {
		return this.applyModifiersAndRound(this.value, living);
	}

	public static class Serializer implements JsonSerializer<MPRModifiableValue>, JsonDeserializer<MPRModifiableValue> {
		@Override
		public MPRModifiableValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			if (json.isJsonPrimitive())
				return new MPRModifiableValue(json.getAsDouble(), List.of(), null);
			JsonObject jObject = json.getAsJsonObject();
			return new MPRModifiableValue(
					GsonHelper.getAsDouble(jObject, "value"),
					deserializeList(jObject, context),
					GsonHelper.getAsObject(jObject, "round", null, context, Integer.class)
			);
		}

		@Override
		public JsonElement serialize(MPRModifiableValue src, Type typeOfSrc, JsonSerializationContext context) {
			if (src.modifiers.isEmpty() && src.round == null)
				return new JsonPrimitive(src.value);
			JsonObject jObject = new JsonObject();
			jObject.addProperty("value", src.value);
			return src.endSerialization(jObject, context);
		}
	}
}
