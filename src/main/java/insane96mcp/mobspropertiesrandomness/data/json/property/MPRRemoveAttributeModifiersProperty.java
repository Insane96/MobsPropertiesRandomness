package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRRemoveAttributeModifiersProperty.Serializer.class)
public class MPRRemoveAttributeModifiersProperty extends MPRProperty {
    @Nullable
    public Holder<Attribute> attribute;
    public List<ResourceLocation> modifierIds;

    public MPRRemoveAttributeModifiersProperty(@Nullable Holder<Attribute> attribute, List<ResourceLocation> modifierIds, List<MPRCondition> conditions) {
        super(conditions);
        this.attribute = attribute;
        this.modifierIds = modifierIds;
    }

    @Override
    public boolean apply(LivingEntity living) {
        if (this.attribute == null)
            return false;
        AttributeInstance attributeInstance = living.getAttribute(this.attribute);
        if (attributeInstance == null)
            return false;
        for (ResourceLocation modifierId : this.modifierIds) {
            attributeInstance.removeModifier(modifierId);
        }
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRRemoveAttributeModifiersProperty>, JsonSerializer<MPRRemoveAttributeModifiersProperty> {
        @Override
        public MPRRemoveAttributeModifiersProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            Holder<Attribute> attribute = SerializerUtils.deserializeRegistryObjectAsHolder(jObject, "attribute", Registries.ATTRIBUTE);
            if (attribute == null)
                MPRLogger.warn("Invalid attribute: %s. Will be ignored.", jObject.get("attribute").getAsString());
            return new MPRRemoveAttributeModifiersProperty(attribute, SerializerUtils.deserializeLocationList(jObject, "modifier_ids", context),
                    MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRRemoveAttributeModifiersProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            if (src.attribute == null)
                throw new JsonParseException("Attribute is null");
            jObject.add("attribute", SerializerUtils.serializeRegistryObject(src.attribute, Registries.ATTRIBUTE));
            SerializerUtils.serializeLocationList(jObject, "modifier_ids", context, src.modifierIds);
            return src.endSerialization(jObject, context);
        }
    }
}
