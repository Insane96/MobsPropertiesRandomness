package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRPotionEffectProperty.Serializer.class)
public class MPRPotionEffectProperty extends MPRProperty {
    public MobEffect mobEffect;
    public MPRRange amplifier;
    public MPRRange duration;
    public boolean ambient;
    public boolean hideParticles;

    public MPRPotionEffectProperty(MobEffect mobEffect, MPRRange amplifier, MPRRange duration, boolean ambient, boolean hideParticles, List<MPRCondition> conditions) {
        super(conditions);
        this.mobEffect = mobEffect;
        this.amplifier = amplifier;
        this.duration = duration;
        this.ambient = ambient;
        this.hideParticles = hideParticles;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        int duration = this.duration.getIntBetween(living);
        MobEffectInstance effectInstance = new MobEffectInstance(mobEffect, duration == -1 ? -1 : duration * 20, this.amplifier.getIntBetween(living), this.ambient, !this.hideParticles, false);
        living.addEffect(effectInstance);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRPotionEffectProperty>, JsonSerializer<MPRPotionEffectProperty> {
        @Override
        public MPRPotionEffectProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();

            String sEffect = GsonHelper.getAsString(jObject, "effect");
            ResourceLocation effect = ResourceLocation.tryParse(sEffect);
            if (effect == null)
                throw new JsonParseException("Invalid Potion Effect %s Id in PotionEffect Property".formatted(sEffect));
            MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(effect);
            if (mobEffect == null)
                throw new JsonParseException("Potion Effect %s in PotionEffect Property doesn't exist".formatted(sEffect));

            MPRRange amplifier;
            if (jObject.has("amplifier"))
                amplifier = context.deserialize(jObject.get("amplifier"), MPRRange.class);
            else
                amplifier = new MPRRange(0f);

            MPRRange duration;
            if (jObject.has("duration"))
                duration = context.deserialize(jObject.get("duration"), MPRRange.class);
            else
                duration = new MPRRange(-1f);

            boolean ambient = GsonHelper.getAsBoolean(jObject, "ambient", false);
            boolean hideParticles = GsonHelper.getAsBoolean(jObject, "hide_particles", false);

            if (ambient && hideParticles)
                Logger.warn("Particles are hidden, but ambient is enabled for %s. Ambient doesn't work if particles are hidden.".formatted(sEffect));

            return new MPRPotionEffectProperty(mobEffect, amplifier, duration, ambient, hideParticles, deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRPotionEffectProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.addProperty("effect", ForgeRegistries.MOB_EFFECTS.getKey(src.mobEffect).toString());
            jObject.add("amplifier", context.serialize(src.amplifier));
            jObject.add("duration", context.serialize(src.duration));
            jObject.addProperty("ambient", src.ambient);
            jObject.addProperty("hide_particles", src.hideParticles);
            return src.endSerialization(jObject, context);
        }
    }
}
