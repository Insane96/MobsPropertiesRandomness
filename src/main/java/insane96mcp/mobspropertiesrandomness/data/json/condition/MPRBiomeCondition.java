package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.data.ObjTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRBiomeCondition.Serializer.class)
public class MPRBiomeCondition extends MPRCondition {
    List<ObjTag<Biome>> biomes;

    public MPRBiomeCondition(List<ObjTag<Biome>> biomes, boolean inverted) {
        super(inverted);
        this.biomes = biomes;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        for (ObjTag<Biome> biome : this.biomes) {
            if (biome.matches(living.level().getBiome(living.blockPosition()).value()))
                return true;
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
            List<ObjTag<Biome>> values = ObjTag.deserializeList(aBiomes, Registries.BIOME);
            return new MPRBiomeCondition(values, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRBiomeCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("biomes", ObjTag.serializeList(src.biomes));
            return src.endSerialization(jObject);
        }
    }
}
