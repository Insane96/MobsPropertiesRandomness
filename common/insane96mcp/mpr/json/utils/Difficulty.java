package insane96mcp.mpr.json.utils;

import java.io.File;

import com.google.gson.annotations.SerializedName;

import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.lib.Logger;

public class Difficulty implements IJsonObject{
	@SerializedName("affects_max_only")
	public boolean affectsMaxOnly;
	@SerializedName("is_local_difficulty")
	public boolean isLocalDifficulty;
	public float multiplier;
	
	public Difficulty() {
		affectsMaxOnly = false;
		isLocalDifficulty = false;
		multiplier = 1.0f;
	}
	
	@Override
	public String toString() {
		return String.format("Difficulty{affectsMaxOnly: %b, isLocalDifficulty: %b, multiplier: %f}", affectsMaxOnly, isLocalDifficulty, multiplier);
	}
	
	public void Validate(final File file) {
		if (multiplier == 0.0f) {
			Logger.Debug("Missing multiplier, defaulting to 1.0 for " + this.toString());
			multiplier = 1.0f;
		}
	}
}
