package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.properties.condition.MPRCondition;
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

@JsonAdapter(MPRPotionEffect.Serializer.class)
public class MPRPotionEffect {
	public MobEffect mobEffect;
	public MPRRange amplifier;
	public MPRRange duration;
	public boolean ambient;
	public boolean hideParticles;
	public List<MPRCondition> conditions;

	public void apply(LivingEntity entity) {
		if (!MPRCondition.conditionsApply(this.conditions, entity))
			return;
		int duration = this.duration.getIntBetween(entity);
		MobEffectInstance effectInstance = new MobEffectInstance(mobEffect, duration == -1 ? -1 : duration * 20, this.amplifier.getIntBetween(entity), this.ambient, !this.hideParticles, false);
		entity.addEffect(effectInstance);
	}

	public static class Serializer implements JsonDeserializer<MPRPotionEffect>, JsonSerializer<MPRPotionEffect> {
		@Override
		public MPRPotionEffect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();

			MPRPotionEffect mprPotionEffect = new MPRPotionEffect();
			String sEffect = GsonHelper.getAsString(jObject, "effect");
			ResourceLocation effect = ResourceLocation.tryParse(sEffect);
			if (effect == null)
				throw new JsonParseException("Invalid Potion Effect %s Id in PotionEffect Object".formatted(sEffect));
			mprPotionEffect.mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(effect);
			if (mprPotionEffect.mobEffect == null)
				throw new JsonParseException("Potion Effect %s in PotionEffect Object doesn't exist".formatted(sEffect));

			if (jObject.has("amplifier"))
				mprPotionEffect.amplifier = context.deserialize(jObject.get("amplifier"), MPRRange.class);
			else
				mprPotionEffect.amplifier = new MPRRange(0f);

			if (jObject.has("duration"))
				mprPotionEffect.duration = context.deserialize(jObject.get("duration"), MPRRange.class);
			else
				mprPotionEffect.duration = new MPRRange(-1f);

			mprPotionEffect.ambient = GsonHelper.getAsBoolean(jObject, "ambient", false);
			mprPotionEffect.hideParticles = GsonHelper.getAsBoolean(jObject, "hide_particles", false);

			if (mprPotionEffect.ambient && mprPotionEffect.hideParticles)
				Logger.warn("Particles are hidden, but ambient is enabled for %s. Ambient doesn't work if particles are hidden.".formatted(sEffect));

			mprPotionEffect.conditions = MPRCondition.deserializeList(jObject, "conditions", context);

			return mprPotionEffect;
		}

		@Override
		public JsonElement serialize(MPRPotionEffect src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jObject = new JsonObject();
			jObject.addProperty("effect", ForgeRegistries.MOB_EFFECTS.getKey(src.mobEffect).toString());
			jObject.add("amplifier", context.serialize(src.amplifier));
			jObject.add("duration", context.serialize(src.duration));
			jObject.addProperty("ambient", src.ambient);
			jObject.addProperty("hide_particles", src.hideParticles);
			if (!src.conditions.isEmpty())
				jObject.add("conditions", context.serialize(src.conditions));
			return jObject;
		}
	}
}
