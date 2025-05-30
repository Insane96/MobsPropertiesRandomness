package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRItemProperties.Serializer.class)
public class MPRItemProperties {
    public List<MPREnchantment> enchantments;
    @Nullable
    public MPRRange count;
    @Nullable
    public MPRModifiableValue dropChance;
    public List<MPRItemAttributeModifier> attributeModifier;

    public MPRItemProperties(List<MPREnchantment> enchantments, @Nullable MPRRange count, @Nullable MPRModifiableValue dropChance, List<MPRItemAttributeModifier> attributeModifier) {
        this.enchantments = enchantments;
        this.count = count;
        this.dropChance = dropChance;
        this.attributeModifier = attributeModifier;
    }

    public void apply(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot) {
        for (MPREnchantment enchantment : this.enchantments) {
            enchantment.applyToStack(entity, itemStack);
        }

        if (this.count != null)
            itemStack.setCount(this.count.getIntBetween(entity));
        if (this.dropChance != null && entity instanceof Mob mob)
            mob.setDropChance(slot, this.dropChance.getValue(entity));
        for (MPRItemAttributeModifier attributeModifier : this.attributeModifier) {
            attributeModifier.apply(entity, itemStack, slot);
        }
    }

    public static class Serializer implements JsonSerializer<MPRItemProperties>, JsonDeserializer<MPRItemProperties> {
        @Override
        public MPRItemProperties deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRItemProperties(
                    SerializerUtils.deserializeList(jObject.getAsJsonArray("enchant"), context, MPREnchantment.class),
                    GsonHelper.getAsObject(jObject, "count", null, context, MPRRange.class),
                    GsonHelper.getAsObject(jObject, "drop_chance", null, context, MPRModifiableValue.class),
                    SerializerUtils.deserializeList(jObject.getAsJsonArray("attribute_modifiers"), context, MPRItemAttributeModifier.class)
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
            jObject.add("count", context.serialize(src.count));
            jObject.add("drop_chance", context.serialize(src.dropChance));
            jObject.add("attribute_modifiers", SerializerUtils.serializeList(src.attributeModifier, context));
            return jObject;
        }
    }
}
