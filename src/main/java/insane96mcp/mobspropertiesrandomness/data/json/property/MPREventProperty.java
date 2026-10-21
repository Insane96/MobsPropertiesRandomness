package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.MPREvent;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPREventProperty.Serializer.class)
public class MPREventProperty extends MPRProperty {
    public MPREvent event;
    /// The event preset this event comes from, if any
    @Nullable
    public ResourceLocation presetId;

    public MPREventProperty(MPREvent event, @Nullable ResourceLocation presetId, List<MPRCondition> conditions) {
        super(conditions);
        this.event = event;
        this.presetId = presetId;
    }

    /**
     * Events should check for conditions only when trying to execute and not on apply
     */
    @Override
    public boolean tryApply(LivingEntity livingEntity) {
        return this.apply(livingEntity);
    }

    @Override
    public boolean tryApply(Target eventTarget, LivingEntity living, @Nullable LivingEntity other) {
        Target resolvedTarget = this.target != null ? this.target : eventTarget;
        LivingEntity applyTarget = resolvedTarget == Target.THIS ? living : other;
        if (applyTarget == null)
            return false;
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
            ResourceLocation presetId = null;
            MPREvent event;
            if (jObject.has("preset")) {
                if (jObject.has("event"))
                    throw new JsonParseException("An event property can't have both 'event' and 'preset'");
                presetId = MPR.locationFrom(GsonHelper.getAsString(jObject, "preset"));
                event = MPREvent.fromPreset(presetId, context);
            }
            else {
                event = MPREvent.deserialize(jObject, context);
                //Unknown event type (already warned), the property is skipped
                if (event == null)
                    return null;
            }
            return new MPREventProperty(event, presetId, MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPREventProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            if (src.presetId != null)
                jObject.addProperty("preset", src.presetId.toString());
            else
                jObject.add("event", context.serialize(src.event));
            return src.endSerialization(jObject, context);
        }
    }

}
