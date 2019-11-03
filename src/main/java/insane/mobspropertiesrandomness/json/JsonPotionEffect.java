package insane.mobspropertiesrandomness.json;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import insane.mobspropertiesrandomness.exceptions.InvalidJsonException;
import insane.mobspropertiesrandomness.json.utils.JsonRangeMinMax;
import insane.mobspropertiesrandomness.json.utils.JsonUtils;
import insane.mobspropertiesrandomness.setup.Logger;
import insane.mobspropertiesrandomness.utils.RandUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JsonPotionEffect implements IJsonObject {
	public String id;
	public JsonRangeMinMax amplifier;

	//TODO Chances
	//public JsonChance chance;

	public boolean ambient;
	@SerializedName("hide_particles")
	public boolean hideParticles;

	public List<Integer> dimensions;
	private List<String> biomes;
	public transient List<Biome> biomesList;

	@Override
	public void validate() throws InvalidJsonException {
		//Potion Id
		if (id == null)
			throw new InvalidJsonException("Missing Potion Effect Id for %s", this.toString());
		else if (!ForgeRegistries.POTIONS.containsKey(new ResourceLocation(id)))
			throw new InvalidJsonException("Potion effect with Id '%s' does not exist", id);

		//Amplifier
		if (amplifier == null) {
			Logger.Debug("Missing Amplifier from " + this.toString() + ". Creating a new one with min and max set to 0");
			amplifier = new JsonRangeMinMax(0, 0);
		}
		amplifier.validate();

		//Chance
		/*if (chance != null)
			chance.Validate(file);
*/
		//ambient and hide particles
		if (ambient && hideParticles)
			Logger.Info("Particles are hidden, but ambient is enabled. This might be an unintended behaviour for " + this.toString());

		if (dimensions == null)
			dimensions = new ArrayList<Integer>();

		biomesList = new ArrayList<Biome>();
		if (biomes == null) {
			biomes = new ArrayList<String>();
		} else {
			for (String biome : biomes) {
				ResourceLocation biomeLoc = new ResourceLocation(biome);
				Biome b = ForgeRegistries.BIOMES.getValue(biomeLoc);
				biomesList.add(b);
			}
		}
	}

	@Override
	public String toString() {
		return String.format("PotionEffect{id: %s, amplifier: %s, chance: %s, ambient: %s, hideParticles: %s, dimensions: %s, biomes: %s}", id, amplifier, id, ambient, hideParticles, dimensions, biomes);
	}

	public static void apply(LivingEntity entity, World world, Random random) {
		if (world.isRemote)
			return;

		for (JsonMob mob : JsonMob.mobs) {
			if (JsonUtils.matchesEntity(entity, world, random, mob)) {
				for (JsonPotionEffect potionEffect : mob.potionEffects) {
					/*if (!potionEffect.chance.ChanceMatches(entity, world, random))
						continue;
*/
					if (!JsonUtils.doesDimensionMatch(entity, potionEffect.dimensions))
						continue;

					if (!JsonUtils.doesBiomeMatch(entity, potionEffect.biomesList))
						continue;

					int minAmplifier = (int) potionEffect.amplifier.GetMin();
					int maxAmplifier = (int) potionEffect.amplifier.GetMax();

					Effect potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(potionEffect.id));
					EffectInstance effect = new EffectInstance(potion, 1000000, RandUtils.getInt(random, minAmplifier, maxAmplifier), potionEffect.ambient, !potionEffect.hideParticles);
					entity.addPotionEffect(effect);
				}
			}
		}
	}
}
