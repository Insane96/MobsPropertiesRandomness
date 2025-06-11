package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(WeightedLootTable.Serializer.class)
public class WeightedLootTable extends WeightedResourceLocation {

    public WeightedLootTable(MPRModifiableValue modifiableWeight, ResourceLocation location, List<MPRCondition> conditions) {
        super(modifiableWeight, location, conditions);
    }

    @Nullable
    public WeightedLootTable computeAndGet(LivingEntity entity) {
        if (!MPRCondition.conditionsApply(this.conditions, entity))
            return null;
        this._weight = (int) this.modifiableWeight.getValue(entity);
        if (this._weight <= 0)
            return null;

        return this;
    }

    public static class Serializer implements JsonSerializer<WeightedLootTable>, JsonDeserializer<WeightedLootTable> {
        @Override
        public WeightedLootTable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new WeightedLootTable(
                    GsonHelper.getAsObject(jObject, "weight", new MPRModifiableValue(1d), context, MPRModifiableValue.class),
                    ResourceLocation.parse(GsonHelper.getAsString(jObject, "loot_table")),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(WeightedLootTable src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("weight", context.serialize(src.modifiableWeight));
            jObject.add("loot_table", context.serialize(src.location));
            return src.endSerialization(jObject, context);
        }
    }
}
