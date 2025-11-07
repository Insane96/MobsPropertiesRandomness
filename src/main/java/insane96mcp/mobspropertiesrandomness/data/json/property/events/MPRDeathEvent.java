package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
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
	public boolean cancelDeath;

	public MPRDeathEvent(MPRHurtData hurtData, boolean cancelDeath, ResourceLocation id, Target target, @Nullable List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
		super(id, target, applyProperties, conditions);
		this.hurtData = hurtData;
		this.cancelDeath = cancelDeath;
	}

	/// returns true if the death should be cancelled
	public boolean death(LivingEntity entity, @Nullable LivingEntity other, DamageSource source, boolean isDirectDamage) {
		if (!this.hurtData.shouldApply(source, isDirectDamage))
			return false;

		return this.tryExecute(entity, other) && this.cancelDeath;
	}

	public static void onDeath(LivingDeathEvent event) {
		LivingEntity living = event.getEntity();
		if (!(event.getSource().getEntity() instanceof LivingEntity killer))
			return;

		List<MPRDeathEvent> events = getEvents(living, MPRDeathEvent.class);
		for (MPRDeathEvent deathEvent : events) {
			if (deathEvent.death(living, killer, event.getSource(), event.getSource().getDirectEntity() == event.getSource().getEntity())
					&& !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
				event.setCanceled(true);
				living.setHealth(0.01f);
			}
		}
	}

	public static class Serializer implements JsonDeserializer<MPRDeathEvent>, JsonSerializer<MPRDeathEvent> {
		@Override
		public MPRDeathEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			String id = GsonHelper.getAsString(jObject, "id");
			Target target = GsonHelper.getAsObject(jObject, "target", context, Target.class);
			List<MPRCondition> conditions = MPRCondition.deserializeConditions(jObject, context);
			List<MPRProperty> properties = MPRProperty.deserializeList(jObject, "apply_properties", context);

			return new MPRDeathEvent(MPRHurtData.deserialize(jObject, context), GsonHelper.getAsBoolean(jObject, "cancel_death", false), ResourceLocation.parse(id), target, properties, conditions);
		}

		@Override
		public JsonElement serialize(MPRDeathEvent src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			jObject.add("hurt_data", context.serialize(src.hurtData));
			if (src.cancelDeath)
				jObject.addProperty("cancel_death", true);
			return src.endSerialization(jObject, context, false);
		}
	}
}
