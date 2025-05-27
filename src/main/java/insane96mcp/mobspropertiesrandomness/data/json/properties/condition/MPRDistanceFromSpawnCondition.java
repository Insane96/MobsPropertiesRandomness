package insane96mcp.mobspropertiesrandomness.data.json.properties.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Type;

@JsonAdapter(MPRDistanceFromSpawnCondition.Serializer.class)
public class MPRDistanceFromSpawnCondition extends MPRCondition {
    protected MPRRange distance;

    public MPRDistanceFromSpawnCondition(MPRRange distance, boolean inverted) {
        super(inverted);
        this.distance = distance;
    }

    @Override
    protected boolean conditionCheck(LivingEntity livingEntity) {
        Vec3 worldSpawn = new Vec3(livingEntity.level().getLevelData().getXSpawn(), livingEntity.level().getLevelData().getYSpawn(), livingEntity.level().getLevelData().getZSpawn());
        return this.distance.isBetween(livingEntity, (float) Math.sqrt(livingEntity.distanceToSqr(worldSpawn)));
    }

    public static class Serializer implements JsonDeserializer<MPRDistanceFromSpawnCondition>, JsonSerializer<MPRDistanceFromSpawnCondition> {
        @Override
        public MPRDistanceFromSpawnCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRDistanceFromSpawnCondition(
                    context.deserialize(jObject.get("distance"), MPRRange.class),
                    MPRCondition.deserializeInverted(jObject)
            );
        }

        @Override
        public JsonElement serialize(MPRDistanceFromSpawnCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("distance", context.serialize(src.distance));
            return src.serializeInverted();
        }
    }
}
