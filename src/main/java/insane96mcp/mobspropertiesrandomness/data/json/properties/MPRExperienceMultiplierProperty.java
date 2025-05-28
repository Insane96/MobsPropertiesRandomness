package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.module.base.TagsFeature;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRExperienceMultiplierProperty.Serializer.class)
public class MPRExperienceMultiplierProperty extends MPRProperty {
    public MPRRange experienceMultiplier;

    public MPRExperienceMultiplierProperty(MPRRange experienceMultiplier, List<MPRCondition> conditions) {
        super(conditions);
        this.experienceMultiplier = experienceMultiplier;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        living.getPersistentData().putDouble(TagsFeature.EXPERIENCE_MULTIPLIER, this.experienceMultiplier.getValue(living));
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRExperienceMultiplierProperty>, JsonSerializer<MPRExperienceMultiplierProperty> {
        @Override
        public MPRExperienceMultiplierProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRExperienceMultiplierProperty(context.deserialize(jObject.get("multiplier"), MPRRange.class), deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRExperienceMultiplierProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("multiplier", context.serialize(src.experienceMultiplier));
            return src.endSerialization(jObject, context);
        }
    }
}
