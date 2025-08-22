package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifier;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@JsonAdapter(MPRDamagedEvent.Serializer.class)
public class MPRDamagedEvent extends MPROnHitEvent {
    public MPRDamagedEvent(MPRModifier.@Nullable Operation damageModifierOperation, @Nullable MPRRange damageAmount, @Nullable MPRRange damageModifier, @Nullable MPRRange healthLeft, boolean flatHealthLeft, MPRHurtData hurtData, ResourceLocation id, Target target, CommandFunction.@Nullable CacheableFunction function, @Nullable List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
        super(damageModifierOperation, damageAmount, damageModifier, healthLeft, flatHealthLeft, hurtData, id, target, function, applyProperties, conditions);
    }

    public static void onDamaged(LivingDamageEvent event) {
        LivingEntity mob = event.getEntity();
		DamageSource source = event.getSource();

		// Return only if there is an attacker and it's not a LivingEntity
		if (source.getEntity() != null && !(source.getEntity() instanceof LivingEntity))
			return;

		LivingEntity other = source.getEntity() instanceof LivingEntity le ? le : null;
		boolean directHit = source.getDirectEntity() == source.getEntity();
		float amount = event.getAmount();

        //Get on hit events of the damaged entity
         List<MPRDamagedEvent> events = getEvents(mob, MPRDamagedEvent.class);
        for (MPRDamagedEvent damagedEvent : events)
            damagedEvent.hit(event, mob, other, source, amount, directHit);
    }

    public static class Serializer implements JsonDeserializer<MPROnHitEvent>, JsonSerializer<MPROnHitEvent> {
        @Override
        public MPROnHitEvent deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            MPRRange damageAmount = GsonHelper.getAsObject(jObject, "damage_amount", null, context, MPRRange.class);
            MPRRange damageModifier = GsonHelper.getAsObject(jObject, "damage_modifier", null, context, MPRRange.class);
            MPRModifier.Operation damageModifierOperation = GsonHelper.getAsObject(jObject, "damage_modifier_operation", null, context, MPRModifier.Operation.class);
            if (damageModifier != null && damageModifierOperation == null)
                throw new JsonParseException("damage_modifier_operation is required when damage_modifier is set");
            MPRRange healthLeft = GsonHelper.getAsObject(jObject, "health_left", null, context, MPRRange.class);
            String id = GsonHelper.getAsString(jObject, "id");
            Target target = GsonHelper.getAsObject(jObject, "target", context, Target.class);
            CommandFunction.CacheableFunction function = deserializeFunction(jObject);
            List<MPRCondition> conditions = MPRCondition.deserializeConditions(jObject, context);
            List<MPRProperty> properties = MPRProperty.deserializeList(jObject, "apply_properties", context);

            return new MPRDamagedEvent(damageModifierOperation, damageAmount, damageModifier, healthLeft, GsonHelper.getAsBoolean(jObject, "flat_health_left", false), MPRHurtData.deserialize(jObject, context), ResourceLocation.parse(id), target, function, properties, conditions);
        }

        @Override
        public JsonElement serialize(MPROnHitEvent src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();

            return src.endSerialization(jObject, context, false);
        }
    }
}
