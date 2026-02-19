package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.mobspropertiesrandomness.data.json.MPRAttributeModifier;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

@JsonAdapter(MPRAttributeModifierItemFunction.Serializer.class)
public class MPRAttributeModifierItemFunction extends MPRItemFunction {
    @Nullable
    EquipmentSlotGroup equipmentSlotGroup;
    MPRAttributeModifier attributeModifier;

    public MPRAttributeModifierItemFunction(@Nullable EquipmentSlotGroup equipmentSlotGroup, MPRAttributeModifier attributeModifier, List<MPRCondition> conditions) {
        super(conditions);
        this.equipmentSlotGroup = equipmentSlotGroup;
        this.attributeModifier = attributeModifier;
    }

    @Override
    public boolean apply(LivingEntity entity, ItemStack itemStack, EquipmentSlot equipmentSlot) {
        if (this.attributeModifier.attribute == null)
            return false;
        AttributeModifier modifier = this.attributeModifier.getModifier(entity);
        MCUtils.addAttributeModifierToItemStack(itemStack, this.attributeModifier.attribute, modifier, Objects.requireNonNullElseGet(this.equipmentSlotGroup, () -> EquipmentSlotGroup.bySlot(equipmentSlot)));
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRAttributeModifierItemFunction>, JsonSerializer<MPRAttributeModifierItemFunction> {
        @Override
        public MPRAttributeModifierItemFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRAttributeModifierItemFunction(
                    GsonHelper.getAsObject(jObject, "slots", null, context, EquipmentSlotGroup.class),
                    context.deserialize(jObject, MPRAttributeModifier.class),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRAttributeModifierItemFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.attributeModifier).getAsJsonObject();
            if (src.equipmentSlotGroup != null)
                jObject.addProperty("slots", src.equipmentSlotGroup.getSerializedName());
            return src.endSerialization(jObject, context);
        }
    }
}
