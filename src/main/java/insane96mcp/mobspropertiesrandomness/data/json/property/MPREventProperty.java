package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.MPREvent;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPREventProperty.Serializer.class)
public class MPREventProperty extends MPRProperty {
    public MPREvent event;

    public MPREventProperty(MPREvent event, List<MPRCondition> conditions) {
        super(conditions);
        this.event = event;
    }

    /**
     * Events should check for conditions only when trying to execute and not on apply
     */
    @Override
    public boolean tryApply(LivingEntity livingEntity) {
        return this.apply(livingEntity);
    }

    @Override
    public boolean tryApply(LivingEntity applyTarget, LivingEntity self, @Nullable LivingEntity other) {
        return this.apply(applyTarget);
    }

    @Override
    public boolean apply(LivingEntity living) {
        this.event.apply(living);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPREventProperty>, JsonSerializer<MPREventProperty> {
        @Override
        public MPREventProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            MPREvent event = MPREvent.deserialize(jObject, context);
            return new MPREventProperty(event, MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPREventProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("event", context.serialize(src.event));
            return src.endSerialization(jObject, context);
        }
    }

}
