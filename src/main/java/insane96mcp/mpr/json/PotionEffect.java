package insane96mcp.mpr.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.utils.Chance;
import insane96mcp.mpr.json.utils.JsonUtils;
import insane96mcp.mpr.json.utils.RangeMinMax;
import insane96mcp.mpr.utils.Logger;
import net.minecraft.entity.MobEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PotionEffect implements IJsonObject{
	public String id;
	public RangeMinMax amplifier;

	public Chance chance;
	
	public boolean ambient;
	@SerializedName("hide_particles")
	public boolean hideParticles;
	
	public List<Integer> dimensions;
	private List<String> biomes;
	public transient List<Biome> biomesList;
	
	@Override
	public String toString() {
		return String.format("PotionEffect{id: %s, amplifier: %s, chance: %s, ambient: %s, hideParticles: %s, dimensions: %s, biomes: %s}", id, amplifier, chance, ambient, hideParticles, dimensions, biomes);
	}

	public void validate(final File file) throws InvalidJsonException {
		//Potion Id
		if (id == null)
			throw new InvalidJsonException("Missing Potion Effect Id for " + this.toString(), file);
		else if (!ForgeRegistries.POTIONS.containsKey(new ResourceLocation(id)))
			throw new InvalidJsonException("Potion effect with Id " + id + " does not exist", file);
		
		//Amplifier
		if (amplifier == null) {
			Logger.debug("Missing Amplifier from " + this.toString() + ". Creating a new one with min and max set to 0");
			amplifier = new RangeMinMax(0, 0);
		}
		amplifier.validate(file);
		
		//Chance
		if (chance != null)
			chance.validate(file);
		
		//ambient and hide particles
		if (ambient && hideParticles)
			Logger.info("Particles are hidden, but ambient is enabled. This might be an unintended behaviour for " + this.toString());
		
		if (dimensions == null)
			dimensions = new ArrayList<Integer>();
		
		biomesList = new ArrayList<Biome>();
		if (biomes == null) {
			biomes = new ArrayList<String>();
		}
		else {
			for (String biome : biomes) {
				ResourceLocation biomeLoc = new ResourceLocation(biome);
				Biome b = ForgeRegistries.BIOMES.getValue(biomeLoc);
				biomesList.add(b);
			}
		}
	}

	public static void apply(MobEntity entity, World world, Random random) {
		if (world.isRemote)
			return;
		
		for (Mob mob : Mob.mobs) {
			if (JsonUtils.matchesEntity(entity, world, random, mob)) {
				for (PotionEffect potionEffect : mob.potionEffects) {
					if (!potionEffect.chance.chanceMatches(entity, world, random))
						continue;

					if (!JsonUtils.doesDimensionMatch(entity, potionEffect.dimensions))
						continue;

					if (!JsonUtils.doesBiomeMatch(entity, potionEffect.biomesList))
						continue;

					int minAmplifier = (int) potionEffect.amplifier.getMin();
					int maxAmplifier = (int) potionEffect.amplifier.getMax();

					Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionEffect.id));

					EffectInstance effectInstance = new EffectInstance(effect, 1000000, insane96mcp.mpr.utils.Utils.getRandomInt(random, minAmplifier, maxAmplifier), potionEffect.ambient, !potionEffect.hideParticles);
					entity.addPotionEffect(effectInstance);
				}
			}
		}
	}
}
