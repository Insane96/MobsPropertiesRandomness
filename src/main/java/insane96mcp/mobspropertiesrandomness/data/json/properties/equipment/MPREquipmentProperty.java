package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRProperty;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPREquipmentProperty.Serializer.class)
public class MPREquipmentProperty extends MPRProperty {
    EquipmentSlot slot;
    Item item;
    MPREnchantments enchantments;

    public MPREquipmentProperty(EquipmentSlot slot, Item item, MPREnchantments enchantments, List<MPRCondition> conditions) {
        super(conditions);
        this.slot = slot;
        this.item = item;
        this.enchantments = enchantments;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        ItemStack stack = new ItemStack(this.item, 1);
        enchantments.apply(living, stack);
        living.setItemSlot(this.slot, stack);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPREquipmentProperty>, JsonSerializer<MPREquipmentProperty> {
        @Override
        public MPREquipmentProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            EquipmentSlot slot = context.deserialize(jObject.get("slot"), EquipmentSlot.class);
            Item item = SerializerUtils.deserializeRegistryObject(jObject.get("item"), Registries.ITEM);
            MPREnchantments enchantments = context.deserialize(jObject.get("enchant"), MPREnchantments.class);

            return new MPREquipmentProperty(slot, item, enchantments, deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPREquipmentProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("slot", context.serialize(src.slot));
            jObject.add("item", context.serialize(src.item));
            jObject.add("enchant", context.serialize(src.enchantments));
            return src.endSerialization(jObject, context);
        }
    }
}
