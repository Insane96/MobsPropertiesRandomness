package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPREnchantments.Serializer.class)
public class MPREnchantments {
    public List<MPREnchantment> enchantments;

    public MPREnchantments(List<MPREnchantment> enchantments) {
        this.enchantments = enchantments;
    }

    public void apply(LivingEntity entity, ItemStack itemStack) {
        for (MPREnchantment enchantment : this.enchantments) {
            enchantment.applyToStack(entity, itemStack);
        }
    }

    public static class Serializer implements JsonSerializer<MPREnchantments>, JsonDeserializer<MPREnchantments> {
        @Override
        public MPREnchantments deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            if (array == null)
                return new MPREnchantments(List.of());
            List<MPREnchantment> enchantments = SerializerUtils.deserializeList(array, context, MPREnchantment.class);
            return new MPREnchantments(enchantments);
        }

        @Override
        public JsonElement serialize(MPREnchantments src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray jArray = new JsonArray();
            if (src.enchantments != null) {
                for (MPREnchantment enchantment : src.enchantments) {
                    jArray.add(context.serialize(enchantment));
                }
            }
            return jArray;
        }
    }
}
