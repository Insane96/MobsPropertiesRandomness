package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.weightedrandom.IWeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRItem.Serializer.class)
public class MPRItem extends MPRConditionable implements IWeightedRandom {
    public Item item;
    private final MPRModifiableValue modifiableWeight;
    private int _weight;
    public MPRItemProperties properties;

    private boolean valid = true;

    public MPRItem(Item item, MPRModifiableValue modifiableWeight, MPRItemProperties properties, List<MPRCondition> conditions) {
        super(conditions);
        this.item = item;
        this.modifiableWeight = modifiableWeight;
        this.properties = properties;
    }

    public ItemStack getStack(LivingEntity living, EquipmentSlot slot) {
        ItemStack stack = new ItemStack(this.item);
        if (this.properties != null)
            this.properties.apply(living, stack, slot);
        return stack;
    }

    @Nullable
    public MPRItem computeAndGet(LivingEntity entity) {
        if (!this.valid
                || !MPRCondition.conditionsApply(this.conditions, entity))
            return null;
        this._weight = (int) this.modifiableWeight.getValue(entity);

        return this;
    }

    @Override
    public int getWeight() {
        return this._weight;
    }

    public static class Serializer implements JsonSerializer<MPRItem>, JsonDeserializer<MPRItem> {
        @Override
        public MPRItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            String sItem = GsonHelper.getAsString(jObject, "item");
            Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(sItem));
            MPRItem mprItem = new MPRItem(
                    item,
                    GsonHelper.getAsObject(jObject, "weight", new MPRModifiableValue(1f), context, MPRModifiableValue.class),
                    context.deserialize(jObject, MPRItemProperties.class),
                    MPRCondition.deserializeConditions(jObject, context)
            );
            if (item == Items.AIR && !sItem.equals("minecraft:air"))
                mprItem.valid = false;
            return mprItem;
        }

        @Override
        public JsonElement serialize(MPRItem src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.properties).getAsJsonObject();
            jObject.add("item", SerializerUtils.serializeRegistryObject(src.item, Registries.ITEM));
            jObject.add("weight", context.serialize(src.modifiableWeight));
            return jObject;
        }
    }
}
