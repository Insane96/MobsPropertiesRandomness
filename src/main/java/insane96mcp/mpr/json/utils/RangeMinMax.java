package insane96mcp.mpr.json.utils;

import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.utils.Logger;

import java.io.File;

public class RangeMinMax implements IJsonObject{
	
	private Float min;
	
	public float getMin() {
		return min;
	}
	
	private Float max;
	
	public float getMax() {
		return max;
	}
	
	public RangeMinMax(int min, int max) {
		this((float) min, (float) max);
	}
	
	public RangeMinMax(float min, float max) {
		this.min = min;
		this.max = max;
		if (max < min)
			max = min;
	}
	
	@Override
	public String toString() {
		return String.format("RangeMinMax{min: %f, max: %f}", min, max);
	}
	
	public void validate(final File file) throws InvalidJsonException{
		if (min == null) {
			throw new InvalidJsonException("Missing min for " + this.toString(), file);
		}
		if (max == null) {
			Logger.debug("Missing max for " + this.toString() + ". Max will now be equal to min");
			max = min;
		}
		if (max < min) {
			Logger.debug("Min is greater than max " + this.toString() + ". Max will now be equal to min");
			max = min;
		}
	}
}	