package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPREffectImmunityProperty.Serializer.class)
public class MPREffectImmunityProperty extends MPRProperty {
    public List<ResourceLocation> mobEffects;

    public MPREffectImmunityProperty(List<ResourceLocation> mobEffects, List<MPRCondition> conditions) {
        super(conditions);
        this.mobEffects = mobEffects;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        ListTag listTag = new ListTag();
        for (ResourceLocation mobEffect : this.mobEffects) {
            listTag.add(StringTag.valueOf(mobEffect.toString()));
        }
        living.getPersistentData().put(MPR.RESOURCE_PREFIX + "effect_immunity", listTag);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPREffectImmunityProperty>, JsonSerializer<MPREffectImmunityProperty> {
        @Override
        public MPREffectImmunityProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPREffectImmunityProperty(SerializerUtils.deserializeLocationList(jObject, "effects", context), MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPREffectImmunityProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            SerializerUtils.serializeLocationList(jObject, "effects", context, src.mobEffects);
            return src.endSerialization(jObject, context);
        }
    }
}
