package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.mobspropertiesrandomness.data.json.MPRAttributeModifier;
import insane96mcp.mobspropertiesrandomness.data.json.MPRProperties;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRItemAttributeModifier.Serializer.class)
public class MPRItemAttributeModifier extends MPRConditionable {
    List<EquipmentSlot> equipmentSlots;
    MPRAttributeModifier attributeModifier;

    public MPRItemAttributeModifier(List<EquipmentSlot> equipmentSlots, MPRAttributeModifier attributeModifier, List<MPRCondition> conditions) {
        super(conditions);
        this.equipmentSlots = equipmentSlots;
        this.attributeModifier = attributeModifier;
    }

    public void apply(LivingEntity entity, ItemStack itemStack, EquipmentSlot equipmentSlotType) {
        if (!MPRCondition.conditionsApply(this.conditions, entity))
            return;
        AttributeModifier modifier = this.attributeModifier.getModifier(entity);
        if (this.equipmentSlots.isEmpty()) {
            MCUtils.addAttributeModifierToItemStack(itemStack, this.attributeModifier.attribute, modifier, equipmentSlotType);
            return;
        }

        for (EquipmentSlot slot : this.equipmentSlots) {
            MCUtils.addAttributeModifierToItemStack(itemStack, this.attributeModifier.attribute, modifier, slot);
        }
    }

    public static class Serializer implements JsonDeserializer<MPRItemAttributeModifier>, JsonSerializer<MPRItemAttributeModifier> {
        @Override
        public MPRItemAttributeModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRItemAttributeModifier(
                    SerializerUtils.deserializeList(jObject, "slots", context, EquipmentSlot.class, false),
                    context.deserialize(jObject, MPRAttributeModifier.class),
                    MPRProperties.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRItemAttributeModifier src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.attributeModifier).getAsJsonObject();
            jObject.add("slots", SerializerUtils.serializeList(src.equipmentSlots, context));
            return src.endSerialization(jObject, context);
        }
    }
}
