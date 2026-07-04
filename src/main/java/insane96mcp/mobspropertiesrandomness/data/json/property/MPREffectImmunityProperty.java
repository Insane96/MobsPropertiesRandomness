package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPREffectImmunityProperty.Serializer.class)
public class MPREffectImmunityProperty extends MPRProperty {
    private static final ResourceLocation EFFECT_IMMUNITY = MPR.id("effect_immunity");

    //TODO Why isn't this a list of MobEffects?
    public List<ResourceLocation> mobEffects;
    public boolean remove;

    public MPREffectImmunityProperty(List<ResourceLocation> mobEffects, boolean remove, List<MPRCondition> conditions) {
        super(conditions);
        this.mobEffects = mobEffects;
        this.remove = remove;
    }

    @Override
    public boolean apply(LivingEntity living) {
        if (!this.remove) {
            ListTag listTag = new ListTag();
            for (ResourceLocation mobEffect : this.mobEffects) {
                listTag.add(StringTag.valueOf(mobEffect.toString()));
            }
            ModNBTData.put(living, EFFECT_IMMUNITY, listTag);
        }
        else {
            ModNBTData.remove(living, EFFECT_IMMUNITY);
        }
        return true;
    }

    public static boolean shouldPreventEffect(LivingEntity living, Holder<MobEffect> effect) {
        if (living.level().isClientSide
                || !ModNBTData.contains(living, EFFECT_IMMUNITY))
            return false;

        ListTag listTag = ModNBTData.getList(living, EFFECT_IMMUNITY, CompoundTag.TAG_STRING);
        for (int i = 0; i < listTag.size(); ++i) {
            String s = listTag.getString(i);
            if (BuiltInRegistries.MOB_EFFECT.getKey(effect.value()).toString().equals(s))
                return true;
        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<MPREffectImmunityProperty>, JsonSerializer<MPREffectImmunityProperty> {
        @Override
        public MPREffectImmunityProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPREffectImmunityProperty(SerializerUtils.deserializeLocationList(jObject, "effects", context), GsonHelper.getAsBoolean(jObject, "remove", false), MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPREffectImmunityProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            SerializerUtils.serializeLocationList(jObject, "effects", context, src.mobEffects);
            if (src.remove)
                jObject.addProperty("remove", true);
            return src.endSerialization(jObject, context);
        }
    }
}
