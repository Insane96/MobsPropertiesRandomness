package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPREffectCondition.Serializer.class)
public class MPREffectCondition extends MPRCondition {
	public static final MPRRange DEFAULT_AMPLIFIER_RANGE = new MPRRange(0d, 255d);

    protected MobEffect effect;
	protected MPRRange amplifier;

    public MPREffectCondition(MobEffect effect, MPRRange amplifier, boolean inverted) {
        super(inverted);
        this.effect = effect;
		this.amplifier = amplifier;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        if (!living.hasEffect(this.effect))
			return false;
		//noinspection DataFlowIssue
		return this.amplifier.isBetween(living, living.getEffect(this.effect).getAmplifier());
    }

    public static class Serializer implements JsonDeserializer<MPREffectCondition>, JsonSerializer<MPREffectCondition> {
        @Override
        public MPREffectCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPREffectCondition(SerializerUtils.deserializeRegistryObject(jObject, "effect", Registries.MOB_EFFECT), GsonHelper.getAsObject(jObject, "amplifier", DEFAULT_AMPLIFIER_RANGE, context, MPRRange.class), MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPREffectCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
			SerializerUtils.serializeRegistryObject(src.effect, Registries.MOB_EFFECT);
			if (src.amplifier != DEFAULT_AMPLIFIER_RANGE)
				jObject.add("amplifier", context.serialize(src.amplifier));
            return src.endSerialization(jObject);
        }
    }
}
