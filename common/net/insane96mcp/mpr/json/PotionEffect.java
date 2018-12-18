package net.insane96mcp.mpr.json;

import java.io.File;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mpr.MobsPropertiesRandomness;
import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.insane96mcp.mpr.json.utils.Chance;
import net.insane96mcp.mpr.json.utils.RangeMinMax;
import net.minecraft.potion.Potion;

public class PotionEffect {
	public String id;
	public RangeMinMax amplifier;

	public Chance chance;
	
	public boolean ambient;
	@SerializedName("hide_particles")
	public boolean hideParticles;
	
	@Override
	public String toString() {
		return String.format("PotionEffect{id: %s, amplifier: %s, chance: %s}", id, amplifier, chance);
	}

	public void Validate(final File file) throws InvalidJsonException{
		//Potion Id
		if (id == null)
			throw new InvalidJsonException("Missing Potion Effect Id for " + this.toString(), file);
		else if (Potion.getPotionFromResourceLocation(id) == null)
			MobsPropertiesRandomness.Warning("Failed to find Potion with " + id);
		
		//Amplifier
		if (amplifier == null) {
			MobsPropertiesRandomness.Debug("Missing Amplifier from " + this.toString() + ". Creating a new one with min and max set to 0");
			amplifier = new RangeMinMax();
		}
		amplifier.Validate(file);
		
		//Chance
		if (chance != null)
			chance.Validate(file);
		
		//ambient and show particles
		if (ambient && hideParticles)
			MobsPropertiesRandomness.Debug("Particles are hidden, but ambient is enabled. This might be an unintended behaviour for " + this.toString());
	}
}
