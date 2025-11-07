package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifier;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@JsonAdapter(MPRAttackEvent.Serializer.class)
public class MPRAttackEvent extends MPROnHitEvent {
    public MPRAttackEvent(MPRModifier.@Nullable Operation damageModifierOperation, @Nullable MPRRange damageAmount, @Nullable MPRRange damageModifier, @Nullable MPRRange healthLeft, boolean flatHealthLeft, MPRHurtData hurtData, ResourceLocation id, Target target, @Nullable List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
        super(damageModifierOperation, damageAmount, damageModifier, healthLeft, flatHealthLeft, hurtData, id, target, applyProperties, conditions);
    }

    public static void onAttack(LivingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity mob))
            return;
        LivingEntity other = event.getEntity();

        //Get on hit events of the attacker
        List<MPRAttackEvent> events = getEvents(mob, MPRAttackEvent.class);
        for (MPRAttackEvent attackEvent : events)
            attackEvent.hit(event, mob, other, event.getSource(), event.getAmount(), event.getSource().getDirectEntity() == event.getSource().getEntity());
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
            List<MPRCondition> conditions = MPRCondition.deserializeConditions(jObject, context);
            List<MPRProperty> properties = MPRProperty.deserializeList(jObject, "apply_properties", context);

            return new MPRAttackEvent(damageModifierOperation, damageAmount, damageModifier, healthLeft, GsonHelper.getAsBoolean(jObject, "flat_health_left", false), MPRHurtData.deserialize(jObject, context), ResourceLocation.parse(id), target, properties, conditions);
        }

        @Override
        public JsonElement serialize(MPROnHitEvent src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();

            return src.endSerialization(jObject, context, false);
        }
    }
}
