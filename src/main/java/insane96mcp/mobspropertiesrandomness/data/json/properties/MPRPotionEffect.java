package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.properties.condition.MPRConditions;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class MPRPotionEffect implements IMPRObject {
	public String id;
	public MPRRange amplifier;

	public MPRModifiableValue chance;

	public boolean ambient;
	@SerializedName("hide_particles")
	public boolean hideParticles;

	public MPRRange duration;

	public MPRConditions conditions;

	public void validate() throws JsonValidationException {
		//Potion Id
		if (this.id == null)
			throw new JsonValidationException("Missing Potion Effect Id in PotionEffect Object. " + this);
		else if (!ForgeRegistries.MOB_EFFECTS.containsKey(new ResourceLocation(this.id)))
			throw new JsonValidationException("Invalid Potion Effect Id in PotionEffect Object. " + this);

		//Amplifier
		if (this.amplifier == null) {
			Logger.debug("Missing Amplifier in PotionEffect object. " + this + ". Will default to 0 (I)");
			this.amplifier = new MPRRange(0f);
		}
		this.amplifier.validate();

		//Chance
		if (this.chance != null)
			this.chance.validate();

		if (this.duration == null)
			this.duration = new MPRRange(-1f);
		else
			this.duration.validate();

		//ambient and hide particles
		if (this.ambient && this.hideParticles)
			Logger.debug("Particles are hidden, but ambient is enabled. Ambient doesn't work if particles are hidden. " + this);

		if (this.conditions != null)
			this.conditions.validate();
	}

	public void apply(LivingEntity entity) {
		if (this.chance != null && entity.level().random.nextFloat() >= this.chance.getValue(entity))
			return;

		if (this.conditions != null && !this.conditions.conditionsApply(entity))
			return;

		MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(this.id));
		//noinspection ConstantConditions
		int duration = this.duration.getIntBetween(entity);
		//noinspection DataFlowIssue
		MobEffectInstance effectInstance = new MobEffectInstance(effect, duration == -1 ? -1 : duration * 20, this.amplifier.getIntBetween(entity), this.ambient, !this.hideParticles, false);
		entity.addEffect(effectInstance);
	}

	@Override
	public String toString() {
		return String.format("PotionEffect{id: %s, amplifier: %s, chance: %s, duration: %s, ambient: %s, hide_particles: %s, conditions: %s}", this.id, this.amplifier, this.chance, this.duration, this.ambient, this.hideParticles, this.conditions);
	}
}
