package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

@JsonAdapter(MPRAttributeModifierProperty.Serializer.class)
public class MPRAttributeModifierProperty extends MPRProperty {
    public UUID uuid;
    public Attribute attribute;
    public String modifierName;
    public MPRRange amount;
    public Operation operation;

    public MPRAttributeModifierProperty(Attribute attribute, UUID uuid, String modifierName, MPRRange amount, Operation operation, List<MPRCondition> conditions) {
        super(conditions);
        this.uuid = uuid;
        this.attribute = attribute;
        this.modifierName = modifierName;
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        Attribute attribute = this.attribute;
        AttributeInstance attributeInstance = living.getAttribute(attribute);
        if (attributeInstance == null) {
            Logger.warn("Attribute %s not found for the entity, skipping the attribute", ForgeRegistries.ATTRIBUTES.getKey(attribute));
            return false;
        }

        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), this.modifierName, this.amount.getFloatBetween(living), this.operation.get());
        attributeInstance.addPermanentModifier(modifier);

        this.fixHealth(living);
        return true;
    }

    protected void fixHealth(LivingEntity entity) {
        if (this.attribute == Attributes.MAX_HEALTH) {
            AttributeInstance attributeInstance = entity.getAttribute(Attributes.MAX_HEALTH);
            if (attributeInstance != null)
                entity.setHealth((float) attributeInstance.getValue());
            entity.setHealth((float) entity.getAttributeValue(Attributes.MAX_HEALTH));
        }
    }

    public static class Serializer implements JsonDeserializer<MPRAttributeModifierProperty>, JsonSerializer<MPRAttributeModifierProperty> {
        @Override
        public MPRAttributeModifierProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            Attribute attribute = SerializerUtils.deserializeRegistryObject(jObject.get("attribute"), Registries.ATTRIBUTE);

            UUID uuid = jObject.has("uuid") ? UUID.fromString(GsonHelper.getAsString(jObject, "uuid")) : UUID.randomUUID();
            String modifierName = GsonHelper.getAsString(jObject, "modifier_name", null);
            MPRRange amount = context.deserialize(jObject.get("amount"), MPRRange.class);
            Operation operation = context.deserialize(jObject.get("operation"), Operation.class);

            return new MPRAttributeModifierProperty(attribute, uuid, modifierName, amount, operation, deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRAttributeModifierProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("attribute", SerializerUtils.serializeRegistryObject(src.attribute, Registries.ATTRIBUTE));
            jObject.addProperty("uuid", src.uuid.toString());
            jObject.addProperty("modifier_name", src.modifierName);
            jObject.add("amount", context.serialize(src.amount));
            jObject.add("operation", context.serialize(src.operation));
            return src.endSerialization(jObject, context);
        }
    }

    public enum Operation {
        @SerializedName("addition")
        ADDITION(AttributeModifier.Operation.ADDITION),
        @SerializedName("multiply_base")
        MULTIPLY_BASE(AttributeModifier.Operation.MULTIPLY_BASE),
        @SerializedName("multiply_total")
        MULTIPLY_TOTAL(AttributeModifier.Operation.MULTIPLY_TOTAL);

        final AttributeModifier.Operation operation;
        public AttributeModifier.Operation get() {
            return this.operation;
        }

        Operation(AttributeModifier.Operation operation) {
            this.operation = operation;
        }
    }
}
