package insane96mcp.mobspropertiesrandomness.json.util.modifier.difficulty;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;

import java.io.File;

public class MPRLocalDifficulty implements IMPRObject {
	public Float multiplier;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (multiplier == null || multiplier <= 0)
			throw new InvalidJsonException("Multiplier is missing or is <= 0. " + this, file);
	}

	public float applyModifier(float worldLocalDiff, float value) {
		return value * (worldLocalDiff * multiplier);
	}

	@Override
	public String toString() {
		return String.format("LocalDifficulty{multiplier: %f}", multiplier);
	}
}
