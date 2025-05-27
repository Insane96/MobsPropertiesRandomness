package insane96mcp.mobspropertiesrandomness.data.json.properties.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRBiomeCondition.Serializer.class)
public class MPRBiomeCondition extends MPRCondition {
    List<IdTagMatcher> biomes;

    public MPRBiomeCondition(List<IdTagMatcher> biomes, boolean inverted) {
        super(inverted);
        this.biomes = biomes;
    }

    @Override
    protected boolean conditionCheck(LivingEntity livingEntity) {
        for (IdTagMatcher dimension : this.biomes) {
            if (dimension.matchesBiome(livingEntity.level().getBiome(livingEntity.blockPosition()))) {
                return true;
            }
        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<MPRBiomeCondition>, JsonSerializer<MPRBiomeCondition> {
        @Override
        public MPRBiomeCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            JsonArray aBiomes = jObject.getAsJsonArray("biomes");
            if (aBiomes == null)
                throw new JsonParseException("Missing biomes array");
            List<IdTagMatcher> values = context.deserialize(aBiomes, IdTagMatcher.LIST_TYPE);
            return new MPRBiomeCondition(values, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRBiomeCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("biomes", context.serialize(src.biomes));
            return src.serializeInverted();
        }
    }
}
