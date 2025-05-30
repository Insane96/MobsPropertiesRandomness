package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.MPRProperties;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.util.ModifiableWeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRItem.Serializer.class)
public class MPRItem extends MPRConditionable {
    @Nullable
    public Item item;
    public ModifiableWeightedRandom modifiableWeightedRandom;
    public MPRItemProperties properties;

    public MPRItem(@Nullable Item item, ModifiableWeightedRandom modifiableWeightedRandom, MPRItemProperties properties, List<MPRCondition> conditions) {
        super(conditions);
        this.item = item;
        this.modifiableWeightedRandom = modifiableWeightedRandom;
        this.properties = properties;
    }

    public void apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
        if (this.item != null)
            stack = new ItemStack(this.item);
        if (this.properties != null)
            this.properties.apply(living, stack, slot);
    }

    public static class Serializer implements JsonSerializer<MPRItem>, JsonDeserializer<MPRItem> {
        @Override
        public MPRItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRItem(
                    SerializerUtils.deserializeRegistryObject(jObject.get("item"), Registries.ITEM),
                    GsonHelper.getAsObject(jObject, "weight", new ModifiableWeightedRandom(new MPRModifiableValue(1f)), context, ModifiableWeightedRandom.class),
                    context.deserialize(jObject, MPRItemProperties.class),
                    MPRProperties.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRItem src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.properties).getAsJsonObject();
            jObject.add("item", SerializerUtils.serializeRegistryObject(src.item, Registries.ITEM));
            jObject.add("weight", context.serialize(src.modifiableWeightedRandom));
            return jObject;
        }
    }
}
