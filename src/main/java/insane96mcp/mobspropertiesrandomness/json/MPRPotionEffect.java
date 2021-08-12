package insane96mcp.mobspropertiesrandomness.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.utils.RandomHelper;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRChance;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRRange;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import insane96mcp.mobspropertiesrandomness.utils.MPRUtils;
import net.minecraft.entity.MobEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MPRPotionEffect implements IMPRObject, IMPRAppliable {
	public String id;
	public MPRRange amplifier;

	public MPRChance chance;

	public boolean ambient;
	@SerializedName("hide_particles")
	public boolean hideParticles;

	private List<String> dimensions;
	public transient List<ResourceLocation> dimensionsList = new ArrayList<>();

	private List<String> biomes;
	public transient List<ResourceLocation> biomesList = new ArrayList<>();

	public void validate(final File file) throws InvalidJsonException {
		//Potion Id
		if (id == null)
			throw new InvalidJsonException("Missing Potion Effect Id. " + this, file);
		else if (!ForgeRegistries.POTIONS.containsKey(new ResourceLocation(id)))
			throw new InvalidJsonException("Invalid Potion Effect Id. " + this, file);

		//Amplifier
		if (amplifier == null) {
			Logger.info("Missing Amplifier. " + this + ". Will default to 0");
			amplifier = new MPRRange(0, 0);
		}
		amplifier.validate(file);

		//Chance
		if (chance != null)
			chance.validate(file);

		//ambient and hide particles
		if (ambient && hideParticles)
			Logger.info("Particles are hidden, but ambient is enabled. This might be an unintended setting for " + this);

		dimensionsList.clear();
		if (dimensions != null) {
			for (String dimension : dimensions) {
				ResourceLocation dimensionRL = new ResourceLocation(dimension);
				dimensionsList.add(dimensionRL);
			}
		}

		biomesList.clear();
		if (biomes != null) {
			for (String biome : biomes) {
				ResourceLocation biomeLoc = new ResourceLocation(biome);
				biomesList.add(biomeLoc);
			}
		}
	}

	public void apply(MobEntity entity, World world) {
		if (world.isRemote)
			return;

		if (this.chance != null && !this.chance.chanceMatches(entity, world))
			return;

		if (!MPRUtils.doesDimensionMatch(entity, this.dimensionsList))
			return;

		if (!MPRUtils.doesBiomeMatch(entity, this.biomesList))
			return;

		int minAmplifier = (int) this.amplifier.getMin();
		int maxAmplifier = (int) this.amplifier.getMax();

		Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(this.id));
		EffectInstance effectInstance = new EffectInstance(effect, 1000000, RandomHelper.getInt(world.rand, minAmplifier, maxAmplifier), this.ambient, !this.hideParticles, false);
		entity.addPotionEffect(effectInstance);
	}

	@Override
	public String toString() {
		return String.format("PotionEffect{id: %s, amplifier: %s, chance: %s, ambient: %s, hide_particles: %s, dimensions: %s, biomes: %s}", id, amplifier, chance, ambient, hideParticles, dimensions, biomes);
	}
}
