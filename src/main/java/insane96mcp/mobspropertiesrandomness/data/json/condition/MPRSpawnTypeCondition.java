package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.module.base.TagsFeature;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
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
    protected boolean conditionCheck(LivingEntity living) {
        for (MobSpawnType spawnType : this.spawnTypes) {
            if (TagsFeature.isSpawnType(spawnType, living))
                return true;
        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<MPRSpawnTypeCondition>, JsonSerializer<MPRSpawnTypeCondition> {
        @Override
        public MPRSpawnTypeCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            List<MobSpawnType> values = SerializerUtils.deserializeList(jObject, "spawn_types", context, MobSpawnType.class);
            if (values.isEmpty())
                throw new JsonParseException("No spawn_types specified for Spawn Type Condition");
            return new MPRSpawnTypeCondition(values, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRSpawnTypeCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("spawn_types", context.serialize(src.spawnTypes));
            return src.endSerialization(jObject);
        }
    }
}
