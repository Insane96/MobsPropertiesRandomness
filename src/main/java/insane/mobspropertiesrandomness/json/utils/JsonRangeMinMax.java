package insane.mobspropertiesrandomness.json.utils;

import insane.mobspropertiesrandomness.exceptions.InvalidJsonException;
import insane.mobspropertiesrandomness.json.IJsonObject;
import insane.mobspropertiesrandomness.setup.Logger;

public class JsonRangeMinMax implements IJsonObject {

	private Float min;

	public float GetMin() {
		return min;
	}

	private Float max;

	public float GetMax() {
		return max;
	}

	public JsonRangeMinMax(int min, int max) {
		this((float) min, (float) max);
	}

	public JsonRangeMinMax(float min, float max) {
		this.min = min;
		this.max = max;
		if (max < min)
			max = min;
	}

	@Override
	public String toString() {
		return String.format("RangeMinMax{min: %f, max: %f}", min, max);
	}

	public void validate() throws InvalidJsonException {
		if (min == null) {
			throw new InvalidJsonException("Missing min for %s", this.toString());
		}
		if (max == null) {
			Logger.Debug("Missing max for " + this.toString() + ". Max will now be equal to min");
			max = min;
		}
		if (max < min) {
			Logger.Debug("Min is greater than max " + this.toString() + ". Max will now be equal to min");
			max = min;
		}
	}
}