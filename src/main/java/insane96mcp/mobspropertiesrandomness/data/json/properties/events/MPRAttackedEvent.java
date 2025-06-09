package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRProperty;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifier;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@JsonAdapter(MPRAttackedEvent.Serializer.class)
public class MPRAttackedEvent extends MPROnHitEvent {
    public MPRAttackedEvent(MPRModifier.@Nullable Operation damageModifierOperation, @Nullable MPRRange damageAmount, @Nullable MPRModifiableValue damageModifier, @Nullable MPRRange healthLeft, boolean flatHealthLeft, MPRHurtData hurtData, ResourceLocation id, Target target, CommandFunction.@Nullable CacheableFunction function, @Nullable List<MPRProperty> applyProperties, List<MPRCondition> conditions) {
        super(damageModifierOperation, damageAmount, damageModifier, healthLeft, flatHealthLeft, hurtData, id, target, function, applyProperties, conditions);
    }

    public static void onAttacked(LivingDamageEvent event) {
        LivingEntity attacked = event.getEntity();
        LivingEntity attacker = (LivingEntity) event.getSource().getEntity();

        //Get on hit events of the attacked entity
        List<MPRAttackedEvent> events = getEvents(attacked, MPRAttackedEvent.class);
        for (MPRAttackedEvent attackEvent : events)
            attackEvent.hit(event, attacked, attacker, event.getSource(), event.getAmount(), event.getSource().getDirectEntity() == event.getSource().getEntity());
    }

    public static class Serializer implements JsonDeserializer<MPROnHitEvent>, JsonSerializer<MPROnHitEvent> {
        @Override
        public MPROnHitEvent deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            MPRRange damageAmount = GsonHelper.getAsObject(jObject, "damage_amount", null, context, MPRRange.class);
            MPRModifiableValue damageModifier = GsonHelper.getAsObject(jObject, "damage_modifier", null, context, MPRModifiableValue.class);
            MPRModifier.Operation damageModifierOperation = GsonHelper.getAsObject(jObject, "damage_modifier_operation", null, context, MPRModifier.Operation.class);
            if (damageModifier != null && damageModifierOperation == null)
                throw new JsonParseException("damage_modifier_operation is required when damage_modifier is set");
            MPRRange healthLeft = GsonHelper.getAsObject(jObject, "health_left", null, context, MPRRange.class);
            String id = GsonHelper.getAsString(jObject, "id");
            Target target = GsonHelper.getAsObject(jObject, "target", context, Target.class);
            CommandFunction.CacheableFunction function = deserializeFunction(jObject);
            List<MPRCondition> conditions = MPRCondition.deserializeConditions(jObject, context);
            List<MPRProperty> properties = MPRProperty.deserializeList(jObject, "apply_properties", context);

            return new MPRAttackedEvent(damageModifierOperation, damageAmount, damageModifier, healthLeft, GsonHelper.getAsBoolean(jObject, "flat_health_left", false), MPRHurtData.deserialize(jObject, context), ResourceLocation.parse(id), target, function, properties, conditions);
        }

        @Override
        public JsonElement serialize(MPROnHitEvent src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();

            return src.endSerialization(jObject, context, false);
        }
    }
}
