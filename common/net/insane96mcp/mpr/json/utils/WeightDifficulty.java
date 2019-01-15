package net.insane96mcp.mpr.json.utils;

public class WeightDifficulty {

	public int easy;
	public int normal;
	public int hard;
	
	public WeightDifficulty() {
		easy = 0;
		normal = 0;
		hard = 0;
	}
	
	@Override
	public String toString() {
		return String.format("WeightDifficulty{easy: %d, normal: %d, hard: %d}", easy, normal, hard);
	}
}
