package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPRDimensionCondition.Serializer.class)
public class MPRDimensionCondition extends MPRCondition {
    List<ResourceKey<Level>> dimensions;

    public MPRDimensionCondition(List<ResourceKey<Level>> dimensions, boolean inverted) {
        super(inverted);
        this.dimensions = dimensions;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        for (ResourceKey<Level> dimension : this.dimensions) {
            if (living.level().dimension().equals(dimension)) {
                return true;
            }
        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<MPRDimensionCondition>, JsonSerializer<MPRDimensionCondition> {
        @Override
        public MPRDimensionCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            JsonArray aDimensions = jObject.getAsJsonArray("dimensions");
            if (aDimensions == null)
                throw new JsonParseException("Missing dimensions array");
            List<ResourceKey<Level>> dimensions = new ArrayList<>();
            for (JsonElement jsonElement : aDimensions) {
                ResourceKey<Level> rk = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(jsonElement.getAsString()));
                dimensions.add(rk);
            }
            return new MPRDimensionCondition(dimensions, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRDimensionCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            JsonArray aDimensions = new JsonArray();
            for (ResourceKey<Level> dimension : src.dimensions) {
                aDimensions.add(dimension.location().toString());
            }
            jObject.add("dimensions", aDimensions);
            return src.endSerialization(jObject);
        }
    }
}
