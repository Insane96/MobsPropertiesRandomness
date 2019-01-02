package net.insane96mcp.mpr.json.utils;

import java.io.File;

import net.insane96mcp.mpr.lib.Logger;

public class RangeMinMax {
	public float min;
	public float max;
	
	public RangeMinMax() {
		this(0, 0);
	}
	
	public RangeMinMax(float min, float max) {
		this.min = min;
		this.max = max;
	}
	
	public RangeMinMax(int min, int max) {
		this.min = min;
		this.max = max;
		if (max < min) {
			Logger.Debug("Min is greater than max in Constructor, max now will be equal to min");
			max = min;
		}
	}
	
	@Override
	public String toString() {
		return String.format("RangeMinMax{min: %f, max: %f}", min, max);
	}
	
	public void Validate(final File file){
		if (max < min) {
			Logger.Debug("Min is greater than max (or max has been omitted), max now will be equal to min");
			max = min;
		}
	}
}	