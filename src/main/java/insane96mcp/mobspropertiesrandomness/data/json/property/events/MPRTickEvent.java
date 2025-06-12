package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.ModNBTData;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRTickEvent.Serializer.class)
public class MPRTickEvent extends MPREvent {
	private static final ResourceLocation TAG_UPDATE_INTERVAL = MPR.location("events/next_tick");

	public MPRRange updateInterval;

	public MPRTickEvent(MPRRange updateInterval, ResourceLocation id, Target target, @Nullable CommandFunction.CacheableFunction function, @Nullable List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
		super(id, target, function, applyProperties, conditions);
		this.updateInterval = updateInterval;
	}

	public void tick(LivingEntity living) {
        if (living.level().getGameTime() < ModNBTData.get(living, TAG_UPDATE_INTERVAL, Long.class))
			return;

		if (living.tickCount > 1)
			this.tryExecute(living, living);
		ModNBTData.put(living, TAG_UPDATE_INTERVAL, (int) (this.updateInterval.getDoubleBetween(living) * 20) + living.level().getGameTime());
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
			return new MPRTickEvent(
					GsonHelper.getAsObject(jObject, "update_interval", context, MPRRange.class),
					ResourceLocation.parse(GsonHelper.getAsString(jObject, "id")),
					Target.THIS,
					deserializeFunction(jObject),
					MPRProperty.deserializeList(jObject, "apply_properties", context),
					MPRCondition.deserializeConditions(jObject, context)
			);
		}

		@Override
		public JsonElement serialize(MPRTickEvent src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			jObject.add("update_interval", context.serialize(src.updateInterval));
			return src.endSerialization(jObject, context, false);
		}
	}
}
