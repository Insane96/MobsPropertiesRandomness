package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRPotionEffectProperty.Serializer.class)
public class MPRPotionEffectProperty extends MPRProperty {
    public MobEffect mobEffect;
    public MPRRange amplifier;
    public MPRRange duration;
    public boolean stackDuration;
    public boolean ambient;
    public boolean hideParticles;

    public MPRPotionEffectProperty(MobEffect mobEffect, MPRRange amplifier, MPRRange duration, boolean stackDuration, boolean ambient, boolean hideParticles, List<MPRCondition> conditions) {
        super(conditions);
        this.mobEffect = mobEffect;
        this.amplifier = amplifier;
        this.duration = duration;
        this.stackDuration = stackDuration;
        this.ambient = ambient;
        this.hideParticles = hideParticles;
    }

    @Override
    public boolean apply(LivingEntity living) {
        double duration = this.duration.getDoubleBetween(living);
        if (living.getEffect(this.mobEffect) != null && this.stackDuration && duration != -1)
            //noinspection DataFlowIssue
            duration += living.getEffect(this.mobEffect).getDuration() / 20d;
        MobEffectInstance effectInstance = new MobEffectInstance(mobEffect, (int) (duration == -1 ? -1 :duration * 20d), this.amplifier.getIntBetween(living), this.ambient, !this.hideParticles, false);
        living.addEffect(effectInstance);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRPotionEffectProperty>, JsonSerializer<MPRPotionEffectProperty> {
        @Override
        public MPRPotionEffectProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();

            MobEffect mobEffect = SerializerUtils.deserializeRegistryObject(jObject, "effect", Registries.MOB_EFFECT);

            MPRRange amplifier;
            if (jObject.has("amplifier"))
                amplifier = context.deserialize(jObject.get("amplifier"), MPRRange.class);
            else
                amplifier = MPRRange.ZERO;

            MPRRange duration = GsonHelper.getAsObject(jObject, "duration", new MPRRange(-1d), context, MPRRange.class);

            boolean ambient = GsonHelper.getAsBoolean(jObject, "ambient", false);
            boolean hideParticles = GsonHelper.getAsBoolean(jObject, "hide_particles", false);

            if (ambient && hideParticles)
                Logger.warn("Particles are hidden, but ambient is enabled for %s. Ambient doesn't work if particles are hidden.".formatted(mobEffect));

            return new MPRPotionEffectProperty(mobEffect, amplifier, duration, GsonHelper.getAsBoolean(jObject, "stack_duration", false), ambient, hideParticles, MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRPotionEffectProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("effect", SerializerUtils.serializeRegistryObject(src.mobEffect, Registries.MOB_EFFECT));
            jObject.add("amplifier", context.serialize(src.amplifier));
            jObject.add("duration", context.serialize(src.duration));
            if (src.stackDuration)
                jObject.addProperty("stack_duration", true);
            jObject.addProperty("ambient", src.ambient);
            jObject.addProperty("hide_particles", src.hideParticles);
            return src.endSerialization(jObject, context);
        }
    }
}
