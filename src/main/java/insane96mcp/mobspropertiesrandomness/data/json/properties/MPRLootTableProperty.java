package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.weightedrandom.WeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.MPRProperties;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.ModifiableWeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.util.WeightedResourceLocation;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPRLootTableProperty.Serializer.class)
public class MPRLootTableProperty extends MPRProperty {
    public List<WeightedResourceLocation> lootTables;

    public MPRLootTableProperty(List<WeightedResourceLocation> lootTables, List<MPRCondition> conditions) {
        super(conditions);
        this.lootTables = lootTables;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        if (!(living instanceof Mob mob))
            return false;
        ArrayList<ModifiableWeightedRandom> weightedList = new ArrayList<>();
        for (WeightedResourceLocation weightedResourceLocation : this.lootTables) {
            ModifiableWeightedRandom computedWeighted = weightedResourceLocation.computeAndGet(living);
            if (computedWeighted != null)
                weightedList.add(computedWeighted);
        }
        if (weightedList.isEmpty())
            return false;
        mob.lootTable = ((WeightedResourceLocation) WeightedRandom.getRandomItem(living.getRandom(), weightedList)).getLocation();
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRLootTableProperty>, JsonSerializer<MPRLootTableProperty> {
        @Override
        public MPRLootTableProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            List<WeightedResourceLocation> weightedResourceLocations = SerializerUtils.deserializeList(jObject, "loot_tables", context, WeightedResourceLocation.class, true);
            if (weightedResourceLocations.isEmpty())
                throw new JsonParseException("No loot_tables specified for Loot Table Property");
            return new MPRLootTableProperty(weightedResourceLocations, MPRProperties.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRLootTableProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("loot_tables", context.serialize(src.lootTables));
            return src.endSerialization(jObject, context);
        }
    }

}
