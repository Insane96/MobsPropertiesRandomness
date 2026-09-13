package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.Target;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPRProperty.Serializer.class)
public class MPRProperty extends MPRConditionable {
    /**
     * Which entity this property is applied to when inside an event's {@code apply_properties} and both
     * {@code this}/{@code other} are available. {@code null} (the default) means "inherit the event's own
     * target", letting different properties in the same {@code apply_properties} list target different
     * entities independently of it. Meaningless everywhere else (a mob's own {@code properties}, a preset,
     * ...) since there's no {@code other} there: setting it to {@code "other"} there just makes the property
     * never apply, same as a condition targeting {@code "other"} with no counterpart available.
     */
    @Nullable
    public Target target;

    public MPRProperty(List<MPRCondition> conditions) {
        super(conditions);
    }

    /// Checks for conditions and applies the property
    public boolean tryApply(LivingEntity livingEntity) {
        return tryApply(Target.THIS, livingEntity, null);
    }

    /**
     * Checks for conditions and applies the property. Conditions are evaluated against {@code living}/
     * {@code other} (the event owner and its counterpart), and the property is applied to whichever of the
     * two this property's own {@link #target} resolves to, falling back to {@code eventTarget} (the owning
     * event's target) when this property doesn't specify one of its own.
     */
    public boolean tryApply(Target eventTarget, LivingEntity living, @Nullable LivingEntity other) {
        if (!MPRCondition.conditionsApply(this.conditions, living, other))
            return false;
        Target resolvedTarget = this.target != null ? this.target : eventTarget;
        LivingEntity applyTarget = resolvedTarget == Target.THIS ? living : other;
        if (applyTarget == null)
            return false;
        return apply(applyTarget);
    }

    /// Applies the property
    public boolean apply(LivingEntity living) {
        throw new UnsupportedOperationException("apply(LivingEntity) not implemented");
    }

    @Override
    public JsonObject endSerialization(JsonObject jObject, JsonSerializationContext context) {
        if (this.target != null)
            jObject.add("target", context.serialize(this.target));
        return super.endSerialization(jObject, context);
    }

    @Nullable
    public static MPRProperty deserialize(JsonElement element, JsonDeserializationContext context) {
        JsonObject jObjectProperty = element.getAsJsonObject();
        ResourceLocation propertyId = MPR.locationFrom(GsonHelper.getAsString(jObjectProperty, "property"));
        Type propertyType = PropertiesRegistry.get(propertyId);
        if (propertyType == null) {
            MPRLogger.warn("property %s does not exist. Skipping".formatted(propertyId));
            return null;
        }
        MPRProperty property = context.deserialize(jObjectProperty, propertyType);
        property.target = GsonHelper.getAsObject(jObjectProperty, "target", null, context, Target.class);
        return property;
    }

    public static List<MPRProperty> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        List<MPRProperty> properties = new ArrayList<>();
        if (!jObject.has(memberName))
            return properties;
        JsonArray aProperties = GsonHelper.getAsJsonArray(jObject, memberName);
        for (JsonElement jsonElement : aProperties) {
            MPRProperty property = deserialize(jsonElement, context);
            if (property == null)
                continue;
            properties.add(property);
        }
        return properties;
    }

    public static class Serializer implements JsonDeserializer<MPRProperty>, JsonSerializer<MPRProperty> {
        @Override
        public MPRProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new MPRProperty(MPRCondition.deserializeList(json.getAsJsonObject(), "conditions", context));
        }

        @Override
        public JsonElement serialize(MPRProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            return src.endSerialization(jObject, context);
        }
    }
}
