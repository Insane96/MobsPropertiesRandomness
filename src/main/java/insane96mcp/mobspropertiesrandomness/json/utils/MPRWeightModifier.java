package insane96mcp.mobspropertiesrandomness.json.utils;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;

import java.io.File;

public class MPRWeightModifier implements IMPRObject {

	public int easy;
	public int normal;
	public int hard;

	public MPRWeightModifier() {
		easy = 0;
		normal = 0;
		hard = 0;
	}

	@Override
	public void validate(File file) throws InvalidJsonException {
	}

	@Override
	public String toString() {
		return String.format("WeightModifier{easy: %d, normal: %d, hard: %d}", easy, normal, hard);
	}
}
