package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.data.ObjTag;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRMob.Serializer.class)
public class MPRMob extends MPRProperties {
    public final ObjTag<EntityType<?>> target;

    public final int priority;
    public final EventPriority eventPriority;

    public MPRMob(ObjTag<EntityType<?>> target, int priority, List<MPRProperty> properties, EventPriority eventPriority, List<MPRCondition> conditions) {
        super(properties, conditions);
        this.target = target;
        this.priority = priority;
        this.eventPriority = eventPriority;
    }

    @Override
    public void tryApply(LivingEntity living) {
        if (!this.target.matches(living.getType()))
            return;
        super.tryApply(living);
    }

    public static class Serializer implements JsonDeserializer<MPRMob>, JsonSerializer<MPRMob> {
        @Override
        public MPRMob deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            if (!jObject.has("target"))
                throw new JsonParseException("Missing target");
            return new MPRMob(
                    ObjTag.deserialize(jObject.get("target"), Registries.ENTITY_TYPE),
                    GsonHelper.getAsInt(jObject, "priority", 0),
                    deserializeProperties(jObject, context),
                    GsonHelper.getAsObject(jObject, "event_priority", EventPriority.LOW, context, EventPriority.class),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRMob src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("target", src.target.serialize());
            jObject.addProperty("priority", src.priority);
            if (src.eventPriority != EventPriority.LOW)
                jObject.add("event_priority", context.serialize(src.eventPriority));
            return src.endSerialization(jObject, context);
        }
    }
}
