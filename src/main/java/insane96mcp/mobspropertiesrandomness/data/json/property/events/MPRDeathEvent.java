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
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
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

	/// returns true if the death should be canceled
	public boolean death(LivingEntity entity, @Nullable LivingEntity other, DamageSource source, boolean isDirectDamage) {
		if (!this.hurtData.shouldApply(source, isDirectDamage)
				|| !MPRCondition.conditionsApply(this.conditions, entity, other))
			return false;

		boolean cancel = this.cancelDeath && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
		//Set the health before applying the properties, so they can change it (e.g. heal)
		if (cancel)
			entity.setHealth(0.01f);
		this.execute(entity, other);
		return cancel;
	}

	public static void onDeath(LivingDeathEvent event) {
		LivingEntity living = event.getEntity();
		// Return only if there is a killer and it's not a LivingEntity, same as damaged events
		if (event.getSource().getEntity() != null && !(event.getSource().getEntity() instanceof LivingEntity))
			return;

		LivingEntity killer = event.getSource().getEntity() instanceof LivingEntity le ? le : null;
		List<MPRDeathEvent> events = getEvents(living, MPRDeathEvent.class);
		for (MPRDeathEvent deathEvent : events) {
			if (deathEvent.death(living, killer, event.getSource(), event.getSource().getDirectEntity() == event.getSource().getEntity()))
				event.setCanceled(true);
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
