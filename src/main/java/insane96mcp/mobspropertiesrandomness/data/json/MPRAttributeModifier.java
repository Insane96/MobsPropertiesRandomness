package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
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

import java.lang.reflect.Type;
import java.util.UUID;

@JsonAdapter(MPRAttributeModifier.Serializer.class)
public class MPRAttributeModifier {
    public UUID uuid;
    public Attribute attribute;
    public String modifierName;
    public MPRRange amount;
    public AttributeModifier.Operation operation;

    public MPRAttributeModifier(Attribute attribute, UUID uuid, String modifierName, MPRRange amount, AttributeModifier.Operation operation) {
        this.uuid = uuid;
        this.attribute = attribute;
        this.modifierName = modifierName;
        this.amount = amount;
        this.operation = operation;
    }

    public AttributeModifier getModifier(LivingEntity living) {
        return new AttributeModifier(UUID.randomUUID(), this.modifierName, this.amount.getDoubleBetween(living), this.operation);
    }

    public static void fixHealth(LivingEntity entity) {
        AttributeInstance attributeInstance = entity.getAttribute(Attributes.MAX_HEALTH);
        if (attributeInstance != null)
            entity.setHealth((float) attributeInstance.getValue());
        entity.setHealth((float) entity.getAttributeValue(Attributes.MAX_HEALTH));
    }

    public static class Serializer implements JsonDeserializer<MPRAttributeModifier>, JsonSerializer<MPRAttributeModifier> {
        @Override
        public MPRAttributeModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            Attribute attribute = SerializerUtils.deserializeRegistryObject(jObject, "attribute", Registries.ATTRIBUTE);
            if (attribute == null)
                Logger.warn("Invalid attribute: %s. Will be ignored.", jObject.get("attribute").getAsString());

            UUID uuid = jObject.has("uuid") ? UUID.fromString(GsonHelper.getAsString(jObject, "uuid")) : UUID.randomUUID();
            String modifierName = GsonHelper.getAsString(jObject, "modifier_name");
            MPRRange amount = GsonHelper.getAsObject(jObject, "amount", context, MPRRange.class);
            AttributeModifier.Operation operation = GsonHelper.getAsObject(jObject, "operation", context, AttributeModifier.Operation.class);

            return new MPRAttributeModifier(attribute, uuid, modifierName, amount, operation);
        }

        @Override
        public JsonElement serialize(MPRAttributeModifier src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("attribute", SerializerUtils.serializeRegistryObject(src.attribute, Registries.ATTRIBUTE));
            jObject.addProperty("uuid", src.uuid.toString());
            jObject.addProperty("modifier_name", src.modifierName);
            jObject.add("amount", context.serialize(src.amount));
            jObject.add("operation", context.serialize(src.operation));
            return jObject;
        }
    }
}
