package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;

import java.io.File;

public class MPRDifficulty implements IMPRObject {
	@SerializedName("affects_max_only")
	public boolean affectsMaxOnly;
	@SerializedName("is_local_difficulty")
	public boolean isLocalDifficulty;
	public float multiplier;

	public MPRDifficulty() {
		affectsMaxOnly = false;
		isLocalDifficulty = false;
		multiplier = 1.0f;
	}

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (multiplier == 0.0f) {
			Logger.warn("Multiplier equals 0, this could have unintended side effects. " + this);
		}
	}

	@Override
	public String toString() {
		return String.format("Difficulty{affects_max_only: %b, is_local_difficulty: %b, multiplier: %f}", affectsMaxOnly, isLocalDifficulty, multiplier);
	}
}
