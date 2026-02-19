package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@JsonAdapter(MPRAttributeModifier.Serializer.class)
public class MPRAttributeModifier {
    /**
     * null if the attribute is invalid
     */
    @Nullable
    public Holder<Attribute> attribute;
    public ResourceLocation id;
    public MPRRange amount;
    public AttributeModifier.Operation operation;

    public MPRAttributeModifier(@Nullable Holder<Attribute> attribute, ResourceLocation id, MPRRange amount, AttributeModifier.Operation operation) {
        this.attribute = attribute;
        this.id = id;
        this.amount = amount;
        this.operation = operation;
    }

    public AttributeModifier getModifier(LivingEntity living) {
        return new AttributeModifier(this.id, this.amount.getDoubleBetween(living), this.operation);
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
            Holder<Attribute> attribute = SerializerUtils.deserializeRegistryObjectAsHolder(jObject, "attribute", Registries.ATTRIBUTE);
            if (attribute == null)
                MPRLogger.warn("Invalid attribute: %s. Will be ignored.", jObject.get("attribute").getAsString());

            String sId = GsonHelper.getAsString(jObject, "id");
            ResourceLocation id = ResourceLocation.tryParse(sId);
            if (id == null)
                throw new JsonParseException("Invalid id: %s".formatted(sId));
            MPRRange amount = GsonHelper.getAsObject(jObject, "amount", context, MPRRange.class);
            AttributeModifier.Operation operation = GsonHelper.getAsObject(jObject, "operation", context, AttributeModifier.Operation.class);

            return new MPRAttributeModifier(attribute, id, amount, operation);
        }

        @Override
        public JsonElement serialize(MPRAttributeModifier src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            if (src.attribute == null)
                throw new JsonParseException("Attribute is null");
            jObject.add("attribute", SerializerUtils.serializeRegistryObject(src.attribute, Registries.ATTRIBUTE));
            jObject.addProperty("id", src.id.toString());
            jObject.add("amount", context.serialize(src.amount));
            jObject.add("operation", context.serialize(src.operation));
            return jObject;
        }
    }
}
