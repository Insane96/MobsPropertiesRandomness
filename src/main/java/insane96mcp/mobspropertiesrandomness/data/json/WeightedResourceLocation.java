package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.weightedrandom.IWeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;


@JsonAdapter(WeightedResourceLocation.Serializer.class)
public class WeightedResourceLocation extends MPRConditionable implements IWeightedRandom {
    public final MPRModifiableValue modifiableWeight;
    protected int _weight;
    protected final ResourceLocation location;

    public WeightedResourceLocation(MPRModifiableValue modifiableWeight, ResourceLocation location, List<MPRCondition> conditions) {
        super(conditions);
        this.modifiableWeight = modifiableWeight;
        this.location = location;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    @Nullable
    public WeightedResourceLocation computeAndGet(LivingEntity entity) {
        if (!MPRCondition.conditionsApply(this.conditions, entity))
            return null;
        this._weight = (int) this.modifiableWeight.getValue(entity);

        return this;
    }

    @Override
    public int getWeight() {
        return this._weight;
    }

    public static class Serializer implements JsonSerializer<WeightedResourceLocation>, JsonDeserializer<WeightedResourceLocation> {
        private final String locationKey;

        public Serializer() {
            this("location");
        }

        public Serializer(String locationKey) {
            this.locationKey = locationKey;
        }

        @Override
        public WeightedResourceLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new WeightedResourceLocation(
                    GsonHelper.getAsObject(jObject, "weight", new MPRModifiableValue(1f), context, MPRModifiableValue.class),
                    ResourceLocation.parse(GsonHelper.getAsString(jObject, locationKey)),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(WeightedResourceLocation src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("weight", context.serialize(src.modifiableWeight));
            jObject.add(locationKey, context.serialize(src.location));
            return src.endSerialization(jObject, context);
        }
    }
}
