package net.insane96mcp.mpr.json;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.insane96mcp.mpr.json.utils.Chance;
import net.insane96mcp.mpr.json.utils.RangeMinMax;
import net.insane96mcp.mpr.json.utils.Utils;
import net.insane96mcp.mpr.lib.Logger;
import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class PotionEffect implements IJsonObject{
	public String id;
	public RangeMinMax amplifier;

	public Chance chance;
	
	public boolean ambient;
	@SerializedName("hide_particles")
	public boolean hideParticles;
	
	public List<Integer> dimensions;
	
	@Override
	public String toString() {
		return String.format("PotionEffect{id: %s, amplifier: %s, chance: %s, ambient: %s, hideParticles: %s, dimensions: %s}", id, amplifier, chance, ambient, hideParticles, dimensions);
	}

	public void Validate(final File file) throws InvalidJsonException {
		//Potion Id
		if (id == null)
			throw new InvalidJsonException("Missing Potion Effect Id for " + this.toString(), file);
		else if (Potion.getPotionFromResourceLocation(id) == null)
			throw new InvalidJsonException("Potion effect with Id " + id + " does not exist", file);
		
		//Amplifier
		if (amplifier == null) {
			Logger.Debug("Missing Amplifier from " + this.toString() + ". Creating a new one with min and max set to 0");
			amplifier = new RangeMinMax(0, 0);
		}
		amplifier.Validate(file);
		
		//Chance
		if (chance != null)
			chance.Validate(file);
		
		//ambient and hide particles
		if (ambient && hideParticles)
			Logger.Info("Particles are hidden, but ambient is enabled. This might be an unintended behaviour for " + this.toString());
		
		if (dimensions == null)
			dimensions = new ArrayList<Integer>();
	}

	public static void Apply(EntityLiving entity, World world, Random random) {
		if (world.isRemote)
			return;
		
		for (Mob mob : Mob.mobs) {
			if (Utils.MatchesEntity(entity, world, random, mob)) {
				for (PotionEffect potionEffect : mob.potionEffects) {
					if (!potionEffect.chance.ChanceMatches(entity, world, random))
						continue;

					if (!Utils.doesDimensionMatch(entity, potionEffect.dimensions))
						continue;
					
					int minAmplifier = (int) potionEffect.amplifier.GetMin();
					int maxAmplifier = (int) potionEffect.amplifier.GetMax();
					
					Potion potion = Potion.getPotionFromResourceLocation(potionEffect.id);
					net.minecraft.potion.PotionEffect effect = new net.minecraft.potion.PotionEffect(potion, 1000000, MathHelper.getInt(random, minAmplifier, maxAmplifier), potionEffect.ambient, !potionEffect.hideParticles);
					entity.addPotionEffect(effect);
				}
			}
		}
	}
}
