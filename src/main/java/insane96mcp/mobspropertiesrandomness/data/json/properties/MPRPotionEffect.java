package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.MPRWorldWhitelist;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class MPRPotionEffect implements IMPRObject {
	public String id;
	public MPRRange amplifier;

	public MPRModifiableValue chance;

	public boolean ambient;
	@SerializedName("hide_particles")
	public boolean hideParticles;

	public MPRRange duration;

	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

	public void validate() throws JsonValidationException {
		//Potion Id
		if (this.id == null)
			throw new JsonValidationException("Missing Potion Effect Id in PotionEffect Object. " + this);
		else if (!ForgeRegistries.MOB_EFFECTS.containsKey(new ResourceLocation(this.id)))
			throw new JsonValidationException("Invalid Potion Effect Id in PotionEffect Object. " + this);

		//Amplifier
		if (this.amplifier == null) {
			Logger.info("Missing Amplifier in PotionEffect object. " + this + ". Will default to 0 (I)");
			this.amplifier = new MPRRange(0);
		}
		this.amplifier.validate();

		//Chance
		if (this.chance != null)
			this.chance.validate();

		if (this.duration == null)
			this.duration = new MPRRange(100000000);
		else
			this.duration.validate();

		//ambient and hide particles
		if (this.ambient && this.hideParticles)
			Logger.info("Particles are hidden, but ambient is enabled. Ambient doesn't work if particles are hidden. " + this);

		if (this.worldWhitelist != null)
			this.worldWhitelist.validate();
	}

	public void apply(LivingEntity entity, Level level) {
		if (level.isClientSide)
			return;

		if (this.chance != null && level.random.nextFloat() >= this.chance.getValue(entity, level))
			return;

		if (this.worldWhitelist != null && !this.worldWhitelist.isWhitelisted(entity))
			return;

		MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(this.id));
		//noinspection ConstantConditions
		MobEffectInstance effectInstance = new MobEffectInstance(effect, this.duration.getInt(entity, level) * 20, this.amplifier.getInt(entity, level), this.ambient, !this.hideParticles, false);
		entity.addEffect(effectInstance);
	}

	@Override
	public String toString() {
		return String.format("PotionEffect{id: %s, amplifier: %s, chance: %s, duration: %s, ambient: %s, hide_particles: %s, world_whitelist: %s}", this.id, this.amplifier, this.chance, this.duration, this.ambient, this.hideParticles, this.worldWhitelist);
	}
}
