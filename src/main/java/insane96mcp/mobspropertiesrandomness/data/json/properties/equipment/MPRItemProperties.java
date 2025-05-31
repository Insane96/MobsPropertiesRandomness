package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

@Deprecated
@JsonAdapter(MPRItemProperties.Serializer.class)
public class MPRItemProperties {
    public List<MPREnchantment> enchantments;

    public MPRItemProperties(List<MPREnchantment> enchantments) {
        this.enchantments = enchantments;
    }

    public void apply(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot) {
        for (MPREnchantment enchantment : this.enchantments) {
            enchantment.applyToStack(entity, itemStack);
        }
    }

    public static class Serializer implements JsonSerializer<MPRItemProperties>, JsonDeserializer<MPRItemProperties> {
        @Override
        public MPRItemProperties deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRItemProperties(
                    SerializerUtils.deserializeList(jObject.getAsJsonArray("enchant"), context, MPREnchantment.class)
            );
        }

        @Override
        public JsonElement serialize(MPRItemProperties src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            JsonArray aEnchantments = new JsonArray();
            if (src.enchantments != null) {
                for (MPREnchantment enchantment : src.enchantments) {
                    aEnchantments.add(context.serialize(enchantment));
                }
            }
            jObject.add("enchant", aEnchantments);
            return jObject;
        }
    }
}
