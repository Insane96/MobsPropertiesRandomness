package net.insane96mcp.mobrandomness.json;

import java.io.File;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mobrandomness.MobsPropertiesRandomness;

public class PotionEffect {
	public String id;
	public RangeMinMax amplifier;

	public float chance;
	@SerializedName("chance_with_difficulty")
	public ChanceWithDifficulty chanceWithDifficulty;
	
	public boolean ambient;
	@SerializedName("hide_particles")
	public boolean hideParticles;
	
	@Override
	public String toString() {
		return String.format("PotionEffect{id: %s, amplifier: %s, chance: %f, chanceWithDifficulty: %s}", id, amplifier, chance, chanceWithDifficulty);
	}

	public void Validate(final File file) throws InvalidJsonException{
		//Potion Id
		if (id == null)
			throw new InvalidJsonException("Missing Potion Effect Id for " + this.toString(), file);
		
		//Amplifier
		if (amplifier == null) {
			MobsPropertiesRandomness.Debug("Missing Amplifier from " + this.toString() + ". Creating a new one with min and max set to 0");
			amplifier = new RangeMinMax();
		}
		amplifier.Validate(file);
		
		//Chance
		if (chance == 0.0f && chanceWithDifficulty == null)
			throw new InvalidJsonException("Missing Chance (or chance = 0) for " + this.toString(), file);
		
		if (chance > 0.0f && chanceWithDifficulty != null) {
			MobsPropertiesRandomness.Debug("chance and chance_with_difficulty are both present, chance is set to 0 and will be ignored for " + this.toString());
			chance = 0.0f;
		}
		if (chanceWithDifficulty != null)
			chanceWithDifficulty.Validate(file);
		
		//ambient and show particles
		if (ambient && hideParticles)
			MobsPropertiesRandomness.Debug("Particles are hidden, but ambient is enabled. This might be an unintended behaviour for " + this.toString());
	}
}
