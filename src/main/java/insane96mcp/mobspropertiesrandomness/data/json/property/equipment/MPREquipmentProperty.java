package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.weightedrandom.WeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.WeightedLootTable;
import insane96mcp.mobspropertiesrandomness.data.json.WeightedResourceLocation;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRProperty;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPREquipmentProperty.Serializer.class)
public class MPREquipmentProperty extends MPRProperty {
    public EquipmentSlot slot;
    public List<MPRItem> items;
    public List<WeightedLootTable> lootTables;
    public List<MPRItemFunction> functions;

    public MPREquipmentProperty(EquipmentSlot slot, List<MPRItem> items, List<WeightedLootTable> lootTables, List<MPRItemFunction> functions, List<MPRCondition> conditions) {
        super(conditions);
        this.slot = slot;
        this.items = items;
        this.functions = functions;
        this.lootTables = lootTables;
    }

    @Override
    public boolean apply(LivingEntity living) {
        if (!MPRCondition.conditionsApply(this.conditions, living))
            return false;
        MPRItem randomItem = this.getRandomItem(living);
        ItemStack stack;
        if (randomItem == null) {
            WeightedResourceLocation randomLootTable = this.getRandomLootTable(living);
            if (randomLootTable == null)
                return false;
            MinecraftServer server = living.getServer();
            if (server == null)
                return false;
            LootParams.Builder lootparams$builder = new LootParams.Builder((ServerLevel) living.level())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, living.damageSources().magic())
                    .withParameter(LootContextParams.THIS_ENTITY, living)
                    .withParameter(LootContextParams.ORIGIN, living.position());
            LootTable lootTable = server.getLootData().getLootTable(randomLootTable.getLocation());
            ObjectArrayList<ItemStack> randomItems = lootTable.getRandomItems(lootparams$builder.create(LootContextParamSets.ENTITY));
            if (randomItems.isEmpty())
                return false;
            stack = randomItems.stream().findFirst().get();
        }
        else
            stack = randomItem.getStack(living, this.slot);
        for (MPRItemFunction function : this.functions)
            function.tryApply(living, stack, slot);
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

    private List<WeightedResourceLocation> getLootTables(LivingEntity entity){
        ArrayList<WeightedResourceLocation> finalLootTables = new ArrayList<>();
        for (WeightedResourceLocation lootTable : this.lootTables) {
            WeightedResourceLocation weightedResourceLocation = lootTable.computeAndGet(entity);
            if (weightedResourceLocation != null)
                finalLootTables.add(weightedResourceLocation);
        }
        return finalLootTables;
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

    @Nullable
    public WeightedResourceLocation getRandomLootTable(LivingEntity entity) {
        List<WeightedResourceLocation> lootTables = getLootTables(entity);
        if (lootTables.isEmpty())
            return null;
        return WeightedRandom.getRandomItem(entity.level().random, lootTables);
    }

    public static class Serializer implements JsonDeserializer<MPREquipmentProperty>, JsonSerializer<MPREquipmentProperty> {
        @Override
        public MPREquipmentProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPREquipmentProperty(
                    GsonHelper.getAsObject(jObject, "slot", context, EquipmentSlot.class),
                    SerializerUtils.deserializeList(jObject, "items", context, MPRItem.class, false),
                    SerializerUtils.deserializeList(jObject, "loot_tables", context, WeightedLootTable.class, false),
                    MPRItemFunction.deserializeList(jObject, "functions", context),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPREquipmentProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.functions).getAsJsonObject();
            jObject.add("slot", context.serialize(src.slot));
            jObject.add("items", SerializerUtils.serializeList(src.items, context));
            jObject.add("loot_tables", SerializerUtils.serializeList(src.lootTables, context));
            return src.endSerialization(jObject, context);
        }
    }
}
