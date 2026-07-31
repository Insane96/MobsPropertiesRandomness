package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
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
            jObject.add("enchantments", SerializerUtils.serializeRegistryHolderList(jObject, src.mobEffects, context, Registries.MOB_EFFECT));
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
