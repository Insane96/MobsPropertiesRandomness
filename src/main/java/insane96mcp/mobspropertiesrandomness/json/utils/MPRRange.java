package insane96mcp.mobspropertiesrandomness.json.utils;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;

import java.io.File;

public class MPRRange implements IMPRObject {
	private Float min;

	public float getMin() {
		return min;
	}

	private Float max;

	public float getMax() {
		return max;
	}

	public MPRRange(int min, int max) {
		this((float) min, (float) max);
	}

	public MPRRange(float min, float max) {
		this.min = min;
		this.max = Math.max(min, max);
	}

	public void validate(final File file) throws InvalidJsonException {
		if (min == null)
			throw new InvalidJsonException("Missing min. " + this, file);

		if (max == null) {
			Logger.info("Missing max for " + this + ". Max will be equal to min.");
			max = min;
		}

		if (max < min)
			throw new InvalidJsonException("Min cannot be greater than max. " + this, file);
	}

	@Override
	public String toString() {
		return String.format("Range{min: %f, max: %f}", min, max);
	}
}
