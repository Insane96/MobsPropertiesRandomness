package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.mobspropertiesrandomness.data.json.MPRAttributeModifier;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRAttributeModifierItemFunction.Serializer.class)
public class MPRAttributeModifierItemFunction extends MPRItemFunction {
    List<EquipmentSlot> equipmentSlots;
    MPRAttributeModifier attributeModifier;

    public MPRAttributeModifierItemFunction(List<EquipmentSlot> equipmentSlots, MPRAttributeModifier attributeModifier, List<MPRCondition> conditions) {
        super(conditions);
        this.equipmentSlots = equipmentSlots;
        this.attributeModifier = attributeModifier;
    }

    @Override
    public boolean apply(LivingEntity entity, ItemStack itemStack, EquipmentSlot equipmentSlotType) {
        AttributeModifier modifier = this.attributeModifier.getModifier(entity);
        if (this.equipmentSlots.isEmpty())
            MCUtils.addAttributeModifierToItemStack(itemStack, this.attributeModifier.attribute, modifier, equipmentSlotType);
        else {
            for (EquipmentSlot slot : this.equipmentSlots)
                MCUtils.addAttributeModifierToItemStack(itemStack, this.attributeModifier.attribute, modifier, slot);
        }
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRAttributeModifierItemFunction>, JsonSerializer<MPRAttributeModifierItemFunction> {
        @Override
        public MPRAttributeModifierItemFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRAttributeModifierItemFunction(
                    SerializerUtils.deserializeList(jObject, "slots", context, EquipmentSlot.class, false),
                    context.deserialize(jObject, MPRAttributeModifier.class),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRAttributeModifierItemFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.attributeModifier).getAsJsonObject();
            jObject.add("slots", SerializerUtils.serializeList(src.equipmentSlots, context));
            return src.endSerialization(jObject, context);
        }
    }
}
