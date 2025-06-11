package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@JsonAdapter(MPRLightLevelCondition.Serializer.class)
public class MPRLightLevelCondition extends MPRCondition {
    private final MPRRange lightLevel;
    @Nullable
    private final LightLayer type;

    public MPRLightLevelCondition(MPRRange lightLevel, @Nullable LightLayer type, boolean inverted) {
        super(inverted);
        this.lightLevel = lightLevel;
        this.type = type;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        if (this.type == null)
            return this.lightLevel.isBetween(living, living.level().getMaxLocalRawBrightness(living.blockPosition()));
        return this.lightLevel.isBetween(living, living.level().getBrightness(this.type, living.blockPosition()));
    }

    public static class Serializer implements JsonDeserializer<MPRLightLevelCondition>, JsonSerializer<MPRLightLevelCondition> {
        @Override
        public MPRLightLevelCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRLightLevelCondition(
                    GsonHelper.getAsObject(jObject, "light_level", context, MPRRange.class),
                    GsonHelper.getAsObject(jObject, "type", null, context, LightLayer.class),
                    MPRCondition.deserializeInverted(jObject)
            );
        }

        @Override
        public JsonElement serialize(MPRLightLevelCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("light_level", context.serialize(src.lightLevel));
            if (src.type != null)
                jObject.add("type", context.serialize(src.type));
            return src.endSerialization(jObject);
        }
    }
}
