package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.ModNBTData;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPREffectImmunityProperty.Serializer.class)
public class MPREffectImmunityProperty extends MPRProperty {
    private static final ResourceLocation EFFECT_IMMUNITY = MPR.location("effect_immunity");

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
        ModNBTData.put(living, EFFECT_IMMUNITY, listTag);
        return true;
    }

    public static boolean shouldPreventEffect(LivingEntity living, MobEffect effect) {
        if (living.level().isClientSide
                || !ModNBTData.contains(living, EFFECT_IMMUNITY))
            return false;

        ListTag listTag = ModNBTData.getList(living, EFFECT_IMMUNITY, CompoundTag.TAG_STRING);
        for (int i = 0; i < listTag.size(); ++i) {
            String s = listTag.getString(i);
            if (ForgeRegistries.MOB_EFFECTS.getKey(effect).toString().equals(s)) {
                return true;
            }
        }
        return false;
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
