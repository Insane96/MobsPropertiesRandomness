package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRProperty;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRTickEvent.Serializer.class)
public class MPRTickEvent extends MPREvent {
	//TODO Random update interval
	public MPRModifiableValue updateInterval;

	public MPRTickEvent(MPRModifiableValue updateInterval, ResourceLocation id, Target target, @Nullable CommandFunction.CacheableFunction function, @Nullable List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
		super(id, target, function, applyProperties, conditions);
		this.updateInterval = updateInterval;
	}

	public void tick(LivingEntity entity) {
		int updateInterval = (int) (this.updateInterval.getValue(entity) * 20);
		if (updateInterval <= 0)
			return;
		if (entity.tickCount % updateInterval != 0)
			return;
		this.execute(entity, entity);
	}

	public static void tickEvents(LivingEntity entity) {
		List<MPRTickEvent> events = getEvents(entity, MPRTickEvent.class);
		for (MPRTickEvent tickEvent : events)
			tickEvent.tick(entity);
	}

	public static class Serializer implements JsonDeserializer<MPRTickEvent>, JsonSerializer<MPRTickEvent> {
		@Override
		public MPRTickEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			String id = GsonHelper.getAsString(jObject, "id");
			//Target target = GsonHelper.getAsObject(jObject, "target", context, Target.class);
			CommandFunction.CacheableFunction function = deserializeFunction(jObject);
			List<MPRCondition> conditions = MPRCondition.deserializeConditions(jObject, context);
			List<MPRProperty> properties = MPRProperty.deserializeList(jObject, "apply_properties", context);

			return new MPRTickEvent(GsonHelper.getAsObject(jObject, "update_interval", new MPRModifiableValue(20d), context, MPRModifiableValue.class), ResourceLocation.parse(id), Target.THIS, function, properties, conditions);
		}

		@Override
		public JsonElement serialize(MPRTickEvent src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			jObject.add("update_interval", context.serialize(src.updateInterval));
			return src.endSerialization(jObject, context, false);
		}
	}
}
