package insane96mcp.mpr.json.utils;

import java.io.File;

import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.lib.Logger;

public class RangeMinMax implements IJsonObject{
	
	private Float min;
	
	public float GetMin() {
		return min;
	}
	
	private Float max;
	
	public float GetMax() {
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
	
	public void Validate(final File file) throws InvalidJsonException{
		if (min == null) {
			throw new InvalidJsonException("Missing min for " + this.toString(), file);
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