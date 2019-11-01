package insane96mcp.mpr.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.utils.Logger;

import java.io.File;

public class JsonDifficulty implements IJsonObject{
	@SerializedName("affects_max_only")
	public boolean affectsMaxOnly;
	@SerializedName("is_local_difficulty")
	public boolean isLocalDifficulty;
	public float multiplier;
	
	public JsonDifficulty() {
		affectsMaxOnly = false;
		isLocalDifficulty = false;
		multiplier = 1.0f;
	}
	
	@Override
	public String toString() {
		return String.format("JsonDifficulty{affectsMaxOnly: %b, isLocalDifficulty: %b, multiplier: %f}", affectsMaxOnly, isLocalDifficulty, multiplier);
	}
	
	public void validate(final File file) {
		if (multiplier == 0.0f) {
			Logger.debug("Missing multiplier, defaulting to 1.0 for " + this.toString());
			multiplier = 1.0f;
		}
	}
}
