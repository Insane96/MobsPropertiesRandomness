package insane96mcp.mobspropertiesrandomness.json.utils.modifier.difficulty;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;

import java.io.File;

public class MPRLocalDifficulty implements IMPRObject {
	public float multiplier;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (multiplier <= 0.0f) {
			throw new InvalidJsonException("Multiplier must be greater than 0. " + this, file);
		}
	}

	public float applyModifier(float worldLocalDiff, float value) {
		return value * (worldLocalDiff * multiplier);
	}

	@Override
	public String toString() {
		return String.format("LocalDifficulty{multiplier: %f}", multiplier);
	}
}
