package net.insane96mcp.mobrandomness.json.utils;

import java.io.File;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mobrandomness.MobsPropertiesRandomness;
import net.insane96mcp.mobrandomness.exceptions.InvalidJsonException;

public class Chance {
	public float amount;
	@SerializedName("affected_by_difficulty")
	public boolean affectedByDifficulty;
	@SerializedName("is_local_difficulty")
	public boolean isLocalDifficulty;
	public float multiplier;
	
	@Override
	public String toString() {
		return String.format("Chance{chance: %f, affectedByDifficulty: %b, isLocalDifficulty: %b, multiplier: %f}", amount, affectedByDifficulty, isLocalDifficulty, multiplier);
	}
	
	public void Validate(final File file) throws InvalidJsonException{
		if (amount == 0.0f)
			throw new InvalidJsonException("Missing chance in " + this.toString(), file);
		
		if (multiplier == 0.0f) {
			MobsPropertiesRandomness.Debug("Missing multiplier, defaulting to 1.0 for " + this.toString());
			multiplier = 1.0f;
		}
		
		if (isLocalDifficulty)
			affectedByDifficulty = true;
	}
}
