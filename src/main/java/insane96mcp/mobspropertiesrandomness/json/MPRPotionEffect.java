package insane96mcp.mobspropertiesrandomness.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRRange;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRWorldWhitelist;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;

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

	public void validate(final File file) throws InvalidJsonException {
		//Potion Id
		if (id == null)
			throw new InvalidJsonException("Missing Potion Effect Id in PotionEffect Object. " + this, file);
		else if (!ForgeRegistries.POTIONS.containsKey(new ResourceLocation(id)))
			throw new InvalidJsonException("Invalid Potion Effect Id in PotionEffect Object. " + this, file);

		//Amplifier
		if (amplifier == null) {
			Logger.info("Missing Amplifier in PotionEffect object. " + this + ". Will default to 0 (I)");
			amplifier = new MPRRange(0);
		}
		amplifier.validate(file);

		//Chance
		if (chance != null)
			chance.validate(file);

		if (this.duration == null)
			this.duration = Integer.MAX_VALUE;

		//ambient and hide particles
		if (ambient && hideParticles)
			Logger.info("Particles are hidden, but ambient is enabled. Ambient doesn't work if particles are hidden. " + this);

		if (worldWhitelist != null)
			worldWhitelist.validate(file);
	}

	public void apply(LivingEntity entity, World world) {
		if (world.isClientSide)
			return;

		if (this.chance != null && world.random.nextFloat() >= this.chance.getValue(entity, world))
			return;

		if (this.worldWhitelist != null && this.worldWhitelist.isWhitelisted(entity))
			return;

		Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(this.id));
		EffectInstance effectInstance = new EffectInstance(effect, this.duration * 20, this.amplifier.getIntBetween(entity, world), this.ambient, !this.hideParticles, false);
		entity.addEffect(effectInstance);
	}

	@Override
	public String toString() {
		return String.format("PotionEffect{id: %s, amplifier: %s, chance: %s, duration: %s, ambient: %s, hide_particles: %s, world_whitelist: %s}", id, amplifier, chance, duration, ambient, hideParticles, worldWhitelist);
	}
}
