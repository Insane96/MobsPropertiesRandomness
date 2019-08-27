package insane96mcp.mpr.json.utils;

import java.io.File;

import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.IJsonObject;

public class JsonWeightDifficulty implements IJsonObject{

	public int easy;
	public int normal;
	public int hard;
	
	public JsonWeightDifficulty() {
		easy = 0;
		normal = 0;
		hard = 0;
	}
	
	@Override
	public String toString() {
		return String.format("WeightDifficulty{easy: %d, normal: %d, hard: %d}", easy, normal, hard);
	}
	
	@Override
	public void Validate(File file) throws InvalidJsonException { }
}
