package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRKillEvent.Serializer.class)
public class MPRKillEvent extends MPREvent {
	public MPRHurtData hurtData;

	public MPRKillEvent(MPRHurtData hurtData, ResourceLocation id, Target target, @Nullable CommandFunction.CacheableFunction function, @Nullable List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
		super(id, target, function, applyProperties, conditions);
		this.hurtData = hurtData;
	}

	public void kill(LivingEntity entity, @Nullable LivingEntity killedEntity, DamageSource source, boolean isDirectDamage) {
		if (!this.hurtData.shouldApply(source, isDirectDamage))
			return;

		this.execute(entity, killedEntity);
	}

	public static void onKill(LivingDeathEvent event) {
		LivingEntity living = event.getEntity();
		LivingEntity killer = (LivingEntity) event.getSource().getEntity();
		List<MPRKillEvent> events = getEvents(killer, MPRKillEvent.class);
		for (MPRKillEvent deathEvent : events) {
			deathEvent.kill(killer, living, event.getSource(), event.getSource().getDirectEntity() == event.getSource().getEntity());
		}
	}

	public static class Serializer implements JsonDeserializer<MPRKillEvent>, JsonSerializer<MPRKillEvent> {
		@Override
		public MPRKillEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			String id = GsonHelper.getAsString(jObject, "id");
			Target target = GsonHelper.getAsObject(jObject, "target", context, Target.class);
			CommandFunction.CacheableFunction function = deserializeFunction(jObject);
			List<MPRCondition> conditions = MPRCondition.deserializeConditions(jObject, context);
			List<MPRProperty> properties = MPRProperty.deserializeList(jObject, "apply_properties", context);

			return new MPRKillEvent(MPRHurtData.deserialize(jObject, context), ResourceLocation.parse(id), target, function, properties, conditions);
		}

		@Override
		public JsonElement serialize(MPRKillEvent src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			jObject.add("hurt_data", context.serialize(src.hurtData));
			return src.endSerialization(jObject, context, false);
		}
	}
}
