package insane96mcp.mobspropertiesrandomness.json.util.modifier.difficulty;

import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;

public class MPRLocalDifficulty implements IMPRObject {
	public Float multiplier;

	@Override
	public void validate() throws JsonValidationException {
		if (multiplier == null || multiplier <= 0)
			throw new JsonValidationException("Multiplier is missing or is <= 0. " + this);
	}

	public float applyModifier(float worldLocalDiff, float value) {
		return value * (worldLocalDiff * multiplier);
	}

	@Override
	public String toString() {
		return String.format("LocalDifficulty{multiplier: %f}", multiplier);
	}
}
