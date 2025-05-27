package insane96mcp.mobspropertiesrandomness.data.json.properties.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRDeepnessCondition.Serializer.class)
public class MPRDeepnessCondition extends MPRCondition {
    protected MPRRange deepness;
    protected boolean seaLevelRelative;

    public MPRDeepnessCondition(MPRRange deepness, boolean seaLevelRelative, boolean inverted) {
        super(inverted);
        this.deepness = deepness;
        this.seaLevelRelative = seaLevelRelative;
    }

    @Override
    protected boolean conditionCheck(LivingEntity livingEntity) {
        int livingY = livingEntity.getBlockY();
        if (this.seaLevelRelative)
            livingY -= livingEntity.level().getSeaLevel();
        return this.deepness.isBetween(livingEntity, livingY);
    }

    public static class Serializer implements JsonDeserializer<MPRDeepnessCondition>, JsonSerializer<MPRDeepnessCondition> {
        @Override
        public MPRDeepnessCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRDeepnessCondition(
                    context.deserialize(jObject.get("y"), MPRRange.class),
                    GsonHelper.getAsBoolean(jObject, "sea_level_relative", false),
                    MPRCondition.deserializeInverted(jObject)
            );
        }

        @Override
        public JsonElement serialize(MPRDeepnessCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("y", context.serialize(src.deepness));
            if (src.seaLevelRelative)
                jObject.addProperty("sea_level_relative", true);
            return src.serializeInverted();
        }
    }
}
