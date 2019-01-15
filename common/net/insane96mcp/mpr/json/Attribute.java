package net.insane96mcp.mpr.json;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.insane96mcp.mpr.json.utils.Difficulty;
import net.insane96mcp.mpr.json.utils.RangeMinMax;
import net.insane96mcp.mpr.lib.Logger;

public class Attribute implements IJsonObject{
	public String id;
	public RangeMinMax modifier;
	@SerializedName("is_flat")
	public boolean isFlat;
	@SerializedName("affected_by_difficulty")
	public boolean affectedByDifficulty;
	public Difficulty difficulty;
	public List<Integer> dimensions;
	
	@Override
	public String toString() {
		return String.format("Attribute{id: %s, modifier: %s, isFlat: %b, affectedByDifficulty: %b, difficulty: %s}", id, modifier, isFlat, affectedByDifficulty, difficulty);
	}
	
	public void Validate(final File file) throws InvalidJsonException {
		//Attribute Id
		if (id == null)
			throw new InvalidJsonException("Missing Attribute Id for " + this.toString(), file);
		
		//Modifier
		if (modifier == null) 
			throw new InvalidJsonException("Missing Modifier (Min/Max) Id for " + this.toString(), file);
		
		modifier.Validate(file);
		
		//difficulty
		if (!affectedByDifficulty) {
			if (difficulty == null) {
				Logger.Debug("Difficulty Object is missing, affected_by_difficulty will be false for " + this.toString());
			}
			else {
				Logger.Debug("Difficulty Object is present, affected_by_difficulty will be true for " + this.toString());
				affectedByDifficulty = true;
				difficulty.Validate(file);
			}
		}
		else
			if (difficulty == null) 
				difficulty = new Difficulty();
		
		if (dimensions == null)
			dimensions = new ArrayList<Integer>();
	}
}
