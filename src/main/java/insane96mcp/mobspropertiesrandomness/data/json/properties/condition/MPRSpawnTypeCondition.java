package insane96mcp.mobspropertiesrandomness.data.json.properties.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.module.base.TagsFeature;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRSpawnTypeCondition.Serializer.class)
public class MPRSpawnTypeCondition extends MPRCondition {
    List<MobSpawnType> spawnTypes;

    public MPRSpawnTypeCondition(List<MobSpawnType> spawnTypes, boolean inverted) {
        super(inverted);
        this.spawnTypes = spawnTypes;
    }

    @Override
    public boolean conditionCheck(LivingEntity livingEntity) {
        for (MobSpawnType spawnType : this.spawnTypes) {
            if (TagsFeature.isSpawnType(spawnType, livingEntity))
                return true;
        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<MPRSpawnTypeCondition>, JsonSerializer<MPRSpawnTypeCondition> {
        @Override
        public MPRSpawnTypeCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            JsonArray aSpawnTypes = jObject.getAsJsonArray("spawn_types");
            Type listType = new TypeToken<List<MobSpawnType>>() {}.getType();
            List<MobSpawnType> values = context.deserialize(aSpawnTypes, listType);
            return new MPRSpawnTypeCondition(values, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRSpawnTypeCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("spawn_types", context.serialize(src.spawnTypes));
            return src.serializeInverted();
        }
    }
}
