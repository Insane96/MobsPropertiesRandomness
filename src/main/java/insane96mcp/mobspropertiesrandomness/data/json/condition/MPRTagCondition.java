package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRTagCondition.Serializer.class)
public class MPRTagCondition extends MPRCondition {
	public List<String> tags;

	public MPRTagCondition(List<String> tags, boolean inverted) {
		super(inverted);
		this.tags = tags;
	}

	@Override
	protected boolean conditionCheck(LivingEntity living) {
		return living.getTags().containsAll(this.tags);
	}

	public static class Serializer implements JsonDeserializer<MPRTagCondition>, JsonSerializer<MPRTagCondition> {
		@Override
		public MPRTagCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			List<String> values = SerializerUtils.deserializeList(jObject, "tags", context, String.class);
			if (values.isEmpty())
				throw new JsonParseException("No tags specified for Tag Condition");
			return new MPRTagCondition(values, MPRCondition.deserializeInverted(jObject));
		}

		@Override
		public JsonElement serialize(MPRTagCondition src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			jObject.add("tags", context.serialize(src.tags));
			return src.endSerialization(jObject);
		}
	}
}
