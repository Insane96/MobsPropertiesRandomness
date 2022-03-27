package insane96mcp.mobspropertiesrandomness.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.util.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.json.util.MPRRange;
import insane96mcp.mobspropertiesrandomness.json.util.MPRWorldWhitelist;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class MPRPotionEffect implements IMPRObject, IMPRAppliable {
	public String id;
	public MPRRange amplifier;

	public MPRModifiableValue chance;

	public boolean ambient;
	@SerializedName("hide_particles")
	public boolean hideParticles;

	public Integer duration;

	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

	public void validate() throws JsonValidationException {
		//Potion Id
		if (id == null)
			throw new JsonValidationException("Missing Potion Effect Id in PotionEffect Object. " + this);
		else if (!ForgeRegistries.MOB_EFFECTS.containsKey(new ResourceLocation(id)))
			throw new JsonValidationException("Invalid Potion Effect Id in PotionEffect Object. " + this);

		//Amplifier
		if (amplifier == null) {
			Logger.info("Missing Amplifier in PotionEffect object. " + this + ". Will default to 0 (I)");
			amplifier = new MPRRange(0);
		}
		amplifier.validate();

		//Chance
		if (chance != null)
			chance.validate();

		if (this.duration == null)
			this.duration = Integer.MAX_VALUE / 20;

		//ambient and hide particles
		if (ambient && hideParticles)
			Logger.info("Particles are hidden, but ambient is enabled. Ambient doesn't work if particles are hidden. " + this);

		if (worldWhitelist != null)
			worldWhitelist.validate();
	}

	public void apply(LivingEntity entity, Level world) {
		if (world.isClientSide)
			return;

		if (this.chance != null && world.random.nextFloat() >= this.chance.getValue(entity, world))
			return;

		if (this.worldWhitelist != null && this.worldWhitelist.isWhitelisted(entity))
			return;

		MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(this.id));
		MobEffectInstance effectInstance = new MobEffectInstance(effect, this.duration * 20, this.amplifier.getIntBetween(entity, world), this.ambient, !this.hideParticles, false);
		entity.addEffect(effectInstance);
	}

	@Override
	public String toString() {
		return String.format("PotionEffect{id: %s, amplifier: %s, chance: %s, duration: %s, ambient: %s, hide_particles: %s, world_whitelist: %s}", id, amplifier, chance, duration, ambient, hideParticles, worldWhitelist);
	}
}
