package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRPotionEffectProperty.Serializer.class)
public class MPRPotionEffectProperty extends MPRProperty {
    public MobEffect mobEffect;
    public Stackable amplifier;
    public Stackable duration;
    public boolean ambient;
    public boolean hideParticles;

    public MPRPotionEffectProperty(MobEffect mobEffect, Stackable amplifier, Stackable duration, boolean ambient, boolean hideParticles, List<MPRCondition> conditions) {
        super(conditions);
        this.mobEffect = mobEffect;
        this.amplifier = amplifier;
        this.duration = duration;
        this.ambient = ambient;
        this.hideParticles = hideParticles;
    }

    @Override
    public boolean apply(LivingEntity living) {
        if (this.mobEffect == null)
            return false;
        int currentDuration = 0, currentLevel = 0;
        if (living.getEffect(this.mobEffect) != null) {
            currentDuration = living.getEffect(this.mobEffect).getDuration();
            currentLevel = living.getEffect(this.mobEffect).getAmplifier() + 1;
        }
        double duration = this.duration.getStackedValue(living, currentDuration);
        MobEffectInstance effectInstance = new MobEffectInstance(mobEffect, (int) (duration == -1 ? -1 : duration * 20d), this.amplifier.getStackedIntValue(living, currentLevel) - 1, this.ambient, !this.hideParticles, false);
        living.addEffect(effectInstance);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRPotionEffectProperty>, JsonSerializer<MPRPotionEffectProperty> {
        @Override
        public MPRPotionEffectProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();

            MobEffect mobEffect = SerializerUtils.deserializeRegistryObject(jObject, "effect", Registries.MOB_EFFECT);
            if (mobEffect == null)
                Logger.warn("Invalid effect: %s. Will be ignored.", jObject.get("effect").getAsString());

            Stackable amplifier = GsonHelper.getAsObject(jObject, "amplifier", Stackable.ONE, context, Stackable.class);
            //if (amplifier.value.min.value <= 0 && amplifier.value.max.value <= 0)
            //    Logger.warn("amplifier should be > 0");

            Stackable duration = GsonHelper.getAsObject(jObject, "duration", new Stackable(new MPRRange(new MPRModifiableValue(-1d))), context, Stackable.class);

            boolean ambient = GsonHelper.getAsBoolean(jObject, "ambient", false);
            boolean hideParticles = GsonHelper.getAsBoolean(jObject, "hide_particles", false);

            if (ambient && hideParticles)
                Logger.warn("Particles are hidden, but ambient is enabled for %s. Ambient doesn't work if particles are hidden.".formatted(mobEffect));

            return new MPRPotionEffectProperty(mobEffect, amplifier, duration, ambient, hideParticles, MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRPotionEffectProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("effect", SerializerUtils.serializeRegistryObject(src.mobEffect, Registries.MOB_EFFECT));
            jObject.add("amplifier", context.serialize(src.amplifier));
            jObject.add("duration", context.serialize(src.duration));
            jObject.addProperty("ambient", src.ambient);
            jObject.addProperty("hide_particles", src.hideParticles);
            return src.endSerialization(jObject, context);
        }
    }

    @JsonAdapter(Stackable.Serializer.class)
    public static class Stackable {
        public static final Stackable ZERO = new Stackable(MPRRange.ZERO);
        public static final Stackable ONE = new Stackable(MPRRange.ONE);

        public MPRRange value;
        public boolean stack;
        public MPRRange cap;

        public Stackable(MPRRange value) {
            this(value, false, MPRRange.UNLIMITED);
        }

        public Stackable(MPRRange value, boolean stack, MPRRange cap) {
            this.value = value;
            this.stack = stack;
            this.cap = cap;
        }

        public double getStackedValue(LivingEntity living, double originalValue) {
            double value = this.value.getDoubleBetween(living);
            if (!this.stack)
                return value;
            if (this.cap != MPRRange.UNLIMITED)
                return Mth.clamp(value, this.cap.getMin(living), this.cap.getMax(living));
            return value + originalValue;
        }

        public int getStackedIntValue(LivingEntity living, int originalValue) {
            int value = this.value.getIntBetween(living);
            if (!this.stack)
                return value;
            if (this.cap != MPRRange.UNLIMITED)
                return Mth.clamp(value, (int) this.cap.getMin(living), (int) this.cap.getMax(living));
            return value + originalValue;
        }

        public static class Serializer implements JsonDeserializer<Stackable>, JsonSerializer<Stackable> {
            @Override
            public Stackable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json.isJsonPrimitive())
                    return new Stackable(new MPRRange(json.getAsDouble()));
				JsonObject jObject = json.getAsJsonObject();
                MPRRange value;
				if (jObject.has("value"))
					value = GsonHelper.getAsObject(jObject, "value", context, MPRRange.class);
				else
					value = GsonHelper.convertToObject(json, json.toString(), context, MPRRange.class);

                boolean stack = GsonHelper.getAsBoolean(jObject, "stack", false);
                MPRRange cap = GsonHelper.getAsObject(jObject, "cap", MPRRange.UNLIMITED, context, MPRRange.class);

                return new Stackable(value, stack, cap);
            }

            @Override
            public JsonElement serialize(Stackable src, Type typeOfSrc, JsonSerializationContext context) {
                if (!src.stack && src.cap == MPRRange.UNLIMITED && src.value.min == src.value.max)
                    return new JsonPrimitive(src.value.min.value);
                JsonObject jObject = new JsonObject();
                jObject.add("value", context.serialize(src.value));
                if (src.stack)
                    jObject.addProperty("stack", true);
                if (src.cap != MPRRange.UNLIMITED)
                    jObject.add("cap", context.serialize(src.cap));
                return jObject;
            }
        }
    }
}
