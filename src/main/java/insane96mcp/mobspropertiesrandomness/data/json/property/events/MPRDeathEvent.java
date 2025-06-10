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

@JsonAdapter(MPRDeathEvent.Serializer.class)
public class MPRDeathEvent extends MPREvent {
	public MPRHurtData hurtData;
	//TODO Allow canceling death

	public MPRDeathEvent(MPRHurtData hurtData, ResourceLocation id, Target target, @Nullable CommandFunction.CacheableFunction function, @Nullable List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
		super(id, target, function, applyProperties, conditions);
		this.hurtData = hurtData;
	}

	public void death(LivingEntity entity, @Nullable LivingEntity other, DamageSource source, boolean isDirectDamage) {
		if (!this.hurtData.shouldApply(source, isDirectDamage))
			return;

		this.execute(entity, other);
	}

	public static void onDeath(LivingDeathEvent event) {
		LivingEntity living = event.getEntity();
		LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
		List<MPRDeathEvent> events = getEvents(living, MPRDeathEvent.class);
		for (MPRDeathEvent deathEvent : events)
			deathEvent.death(living, attacker, event.getSource(), event.getSource().getDirectEntity() == event.getSource().getEntity());
	}

	public static class Serializer implements JsonDeserializer<MPRDeathEvent>, JsonSerializer<MPRDeathEvent> {
		@Override
		public MPRDeathEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			String id = GsonHelper.getAsString(jObject, "id");
			Target target = GsonHelper.getAsObject(jObject, "target", context, Target.class);
			CommandFunction.CacheableFunction function = deserializeFunction(jObject);
			List<MPRCondition> conditions = MPRCondition.deserializeConditions(jObject, context);
			List<MPRProperty> properties = MPRProperty.deserializeList(jObject, "apply_properties", context);

			return new MPRDeathEvent(MPRHurtData.deserialize(jObject, context), ResourceLocation.parse(id), target, function, properties, conditions);
		}

		@Override
		public JsonElement serialize(MPRDeathEvent src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();

			return src.endSerialization(jObject, context, false);
		}
	}
}
