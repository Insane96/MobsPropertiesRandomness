package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.preset.MPRPresetsProperty;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Predicate;

@JsonAdapter(MPRProperties.Serializer.class)
public class MPRProperties extends MPRConditionable {
    protected List<MPRProperty> properties;

    public MPRProperties(List<MPRProperty> properties, List<MPRCondition> conditions) {
        super(conditions);
        this.properties = properties;
    }

    /**
     * If conditions apply, applies all properties
     */
    public void tryApply(LivingEntity living) {
        if (!MPRCondition.conditionsApply(this.conditions, living))
            return;

        apply(living);
    }

    /**
     * Applies all properties checking each property condition
     */
    public void apply(LivingEntity living) {
        applyProperties(property -> property.tryApply(living));
    }

    /**
     * Applies all properties ignoring conditions
     */
    public void forceApply(LivingEntity living) {
        applyProperties(property -> property.apply(living));
    }

    /**
     * Applies the properties respecting {@code presets} properties' mode:<br>
     * - exclusive: the presets are applied first, and the other properties are applied only if no preset was applied<br>
     * - before: the presets are applied before the other properties<br>
     * - after: the presets are applied after the other properties
     */
    private void applyProperties(Predicate<MPRProperty> applier) {
        boolean exclusiveApplied = false;
        for (MPRProperty property : this.properties) {
            if (property instanceof MPRPresetsProperty presets && presets.mode != MPRPresetsProperty.Mode.AFTER
                    && applier.test(property) && presets.mode == MPRPresetsProperty.Mode.EXCLUSIVE)
                exclusiveApplied = true;
        }
        if (!exclusiveApplied) {
            for (MPRProperty property : this.properties) {
                if (!(property instanceof MPRPresetsProperty))
                    applier.test(property);
            }
        }
        for (MPRProperty property : this.properties) {
            if (property instanceof MPRPresetsProperty presets && presets.mode == MPRPresetsProperty.Mode.AFTER)
                applier.test(property);
        }
    }

    public List<MPRProperty> getProperties() {
        return this.properties;
    }

    public static List<MPRProperty> deserializeProperties(JsonObject jObject, JsonDeserializationContext context) { return MPRProperty.deserializeList(jObject, "properties", context); }

    public JsonObject endSerialization(JsonObject jObject, JsonSerializationContext context) {
        jObject.add("properties", context.serialize(this.properties));
        super.endSerialization(jObject, context);
        return jObject;
    }

    public static class Serializer implements JsonDeserializer<MPRProperties>, JsonSerializer<MPRProperties> {
        @Override
        public MPRProperties deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRProperties(deserializeProperties(jObject, context), MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRProperties src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            return src.endSerialization(jObject, context);
        }
    }
}
