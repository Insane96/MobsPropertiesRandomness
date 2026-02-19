package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.MPRAttributeModifier;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRAttributeModifierProperty.Serializer.class)
public class MPRAttributeModifierProperty extends MPRProperty {
    public MPRAttributeModifier attributeModifier;

    public MPRAttributeModifierProperty(MPRAttributeModifier attributeModifier, List<MPRCondition> conditions) {
        super(conditions);
        this.attributeModifier = attributeModifier;
    }

    @Override
    public boolean apply(LivingEntity living) {
        if (this.attributeModifier.attribute == null)
            return false;
        AttributeInstance attributeInstance = living.getAttribute(this.attributeModifier.attribute);
        if (attributeInstance == null) {
            MPRLogger.warn("Attribute %s not found for the entity, skipping the attribute", BuiltInRegistries.ATTRIBUTE.getKey(this.attributeModifier.attribute.value()));
            return false;
        }

        attributeInstance.addPermanentModifier(this.attributeModifier.getModifier(living));
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRAttributeModifierProperty>, JsonSerializer<MPRAttributeModifierProperty> {
        @Override
        public MPRAttributeModifierProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRAttributeModifierProperty(
                    context.deserialize(jObject, MPRAttributeModifier.class),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRAttributeModifierProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.attributeModifier).getAsJsonObject();
            return src.endSerialization(jObject, context);
        }
    }
}
