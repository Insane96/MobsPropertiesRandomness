package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.MPRProperties;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRProperty;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPREquipmentProperty.Serializer.class)
public class MPREquipmentProperty extends MPRProperty {
    EquipmentSlot slot;
    Item item;
    @Nullable
    MPRItemProperties properties;

    public MPREquipmentProperty(EquipmentSlot slot, Item item, @Nullable MPRItemProperties properties, List<MPRCondition> conditions) {
        super(conditions);
        this.slot = slot;
        this.item = item;
        this.properties = properties;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        if (!MPRCondition.conditionsApply(this.conditions, living))
            return false;
        ItemStack stack = new ItemStack(this.item, 1);
        if (this.properties != null)
            this.properties.apply(living, stack, slot);
        living.setItemSlot(this.slot, stack);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPREquipmentProperty>, JsonSerializer<MPREquipmentProperty> {
        @Override
        public MPREquipmentProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            EquipmentSlot slot = context.deserialize(jObject.get("slot"), EquipmentSlot.class);
            Item item = SerializerUtils.deserializeRegistryObject(jObject.get("item"), Registries.ITEM);
            MPRItemProperties enchantments = context.deserialize(jObject, MPRItemProperties.class);

            return new MPREquipmentProperty(slot, item, enchantments, MPRProperties.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPREquipmentProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.properties).getAsJsonObject();
            jObject.add("slot", context.serialize(src.slot));
            jObject.add("item", context.serialize(src.item));
            return src.endSerialization(jObject, context);
        }
    }
}
