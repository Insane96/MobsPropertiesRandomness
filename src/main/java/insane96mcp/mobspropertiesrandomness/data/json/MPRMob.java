package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.data.IdTagMatcher;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRProperty;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRMob.Serializer.class)
public class MPRMob extends MPRProperties {
    public IdTagMatcher target;

    public int priority;

    public MPRMob(IdTagMatcher target, int priority, List<MPRProperty> properties, List<MPRCondition> conditions) {
        super(properties, conditions);
        this.target = target;
        this.priority = priority;
    }

    public void tryApply(LivingEntity entity) {
        if (!this.target.matchesEntity(entity)
                || !MPRCondition.conditionsApply(this.conditions, entity))
            return;
        for (MPRProperty property : this.properties) {
            property.tryApply(entity);
        }
    }

    public static class Serializer implements JsonDeserializer<MPRMob>, JsonSerializer<MPRMob> {
        @Override
        public MPRMob deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRMob(
                    context.deserialize(jObject.get("target"), IdTagMatcher.class),
                    GsonHelper.getAsInt(jObject, "priority", 0),
                    deserializeProperties(jObject, context),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRMob src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("target", context.serialize(src.target));
            jObject.addProperty("priority", src.priority);
            return src.endSerialization(jObject, context);
        }
    }
}
