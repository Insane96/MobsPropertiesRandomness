package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.weightedrandom.WeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.MPRProperties;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRProperty;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPREquipmentProperty.Serializer.class)
public class MPREquipmentProperty extends MPRProperty {
    EquipmentSlot slot;
    List<MPRItem> items;
    @Nullable
    MPRItemProperties properties;

    public MPREquipmentProperty(EquipmentSlot slot, List<MPRItem> items, @Nullable MPRItemProperties properties, List<MPRCondition> conditions) {
        super(conditions);
        this.slot = slot;
        this.items = items;
        this.properties = properties;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        if (!MPRCondition.conditionsApply(this.conditions, living))
            return false;
        MPRItem randomItem = this.getRandomItem(living);
        if (randomItem == null)
            return false;
        ItemStack stack = randomItem.getStack(living, this.slot);
        if (this.properties != null)
            this.properties.apply(living, stack, slot);
        living.setItemSlot(this.slot, stack);
        return true;
    }

    private List<MPRItem> getItems(LivingEntity entity){
        ArrayList<MPRItem> finalItems = new ArrayList<>();
        for (MPRItem item : this.items) {
            MPRItem mprItem = item.computeAndGet(entity);
            if (mprItem != null)
                finalItems.add(mprItem);
        }
        return finalItems;
    }

    /**
     * Returns a random item from the pool based of weights, dimensions whitelist and biomes whitelist
     * @return an Item or null if no items were available
     */
    @Nullable
    public MPRItem getRandomItem(LivingEntity entity) {
        List<MPRItem> items = getItems(entity);
        if (items.isEmpty())
            return null;
        return WeightedRandom.getRandomItem(entity.level().random, items);
    }

    public static class Serializer implements JsonDeserializer<MPREquipmentProperty>, JsonSerializer<MPREquipmentProperty> {
        @Override
        public MPREquipmentProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            EquipmentSlot slot = GsonHelper.getAsObject(jObject, "slot", context, EquipmentSlot.class);
            List<MPRItem> items = SerializerUtils.deserializeList(jObject, "items", context, MPRItem.class);
            MPRItemProperties enchantments = context.deserialize(jObject, MPRItemProperties.class);

            return new MPREquipmentProperty(slot, items, enchantments, MPRProperties.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPREquipmentProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.properties).getAsJsonObject();
            jObject.add("slot", context.serialize(src.slot));
            jObject.add("items", SerializerUtils.serializeList(src.items, context));
            return src.endSerialization(jObject, context);
        }
    }
}
