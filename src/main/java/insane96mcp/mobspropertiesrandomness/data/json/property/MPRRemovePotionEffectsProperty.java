package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRRemovePotionEffectsProperty.Serializer.class)
public class MPRRemovePotionEffectsProperty extends MPRProperty {
    public List<Holder<MobEffect>> mobEffects;

    public MPRRemovePotionEffectsProperty(List<Holder<MobEffect>> mobEffects, List<MPRCondition> conditions) {
        super(conditions);
        this.mobEffects = mobEffects;
    }

    @Override
    public boolean apply(LivingEntity living) {
        if (this.mobEffects.isEmpty()) {
            living.removeAllEffects();
        }
        else {
            for (Holder<MobEffect> mobEffect : this.mobEffects) {
                living.removeEffect(mobEffect);
            }
        }
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRRemovePotionEffectsProperty>, JsonSerializer<MPRRemovePotionEffectsProperty> {
        @Override
        public MPRRemovePotionEffectsProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();

            return new MPRRemovePotionEffectsProperty(SerializerUtils.deserializeRegistryObjectListAsHolders(jObject, "effects", context, Registries.MOB_EFFECT, false),
                    MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRRemovePotionEffectsProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("effects", SerializerUtils.serializeRegistryHolderList(jObject, src.mobEffects, context, Registries.MOB_EFFECT));
            return src.endSerialization(jObject, context);
        }
    }
}
