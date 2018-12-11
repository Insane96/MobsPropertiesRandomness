package net.insane96mcp.mobrandomness.json;

import java.io.File;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mobrandomness.MobsPropertiesRandomness;
import net.insane96mcp.mobrandomness.exceptions.InvalidJsonException;

public class ChanceWithDifficulty {
	public float chance;
	@SerializedName("is_local_difficulty")
	public boolean isLocalDifficulty;
	public float multiplier;
	
	@Override
	public String toString() {
		return String.format("ChanceWithDifficulty{chance: %f, isLocalDifficulty: %b, multiplier: %f}", chance, isLocalDifficulty, multiplier);
	}
	
	public void Validate(final File file) throws InvalidJsonException{
		if (chance == 0.0f)
			throw new InvalidJsonException("Missing chance in " + this.toString(), file);
		
		if (multiplier == 0.0f) {
			MobsPropertiesRandomness.Debug("Missing multiplier, defaulting to 1.0 for " + this.toString());
			multiplier = 1.0f;
		}
	}
}
