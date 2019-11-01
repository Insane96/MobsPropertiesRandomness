package insane96mcp.mpr.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.init.ModConfig;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.utils.Logger;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.io.File;
import java.util.Random;

public class Chance implements IJsonObject {
	public float amount;
	@SerializedName("affected_by_difficulty")
	public boolean affectedByDifficulty;
	@SerializedName("is_local_difficulty")
	public boolean isLocalDifficulty;
	public float multiplier;
	
	@Override
	public String toString() {
		return String.format("Chance{chance: %f, affectedByDifficulty: %b, isLocalDifficulty: %b, multiplier: %f}", amount, affectedByDifficulty, isLocalDifficulty, multiplier);
	}
	
	public void validate(final File file) throws InvalidJsonException{
		if (amount == 0.0f)
			throw new InvalidJsonException("Missing chance in " + this.toString(), file);
		
		if (multiplier == 0.0f) {
			Logger.debug("Missing multiplier, defaulting to 1.0 for " + this.toString());
			multiplier = 1.0f;
		}
		
		if (isLocalDifficulty)
			affectedByDifficulty = true;
	}
	
	public boolean chanceMatches(MobEntity entity, World world, Random random) {
		float chance = this.amount;
		if (this.affectedByDifficulty) {
			if (this.isLocalDifficulty) {
				chance *= world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty() * this.multiplier;
			}
			else {
				Difficulty difficulty = world.getDifficulty();
				if (difficulty.equals(Difficulty.EASY))
					chance *= ModConfig.Difficulty.easyMultiplier.get();
				else if (difficulty.equals(Difficulty.NORMAL))
					chance *= ModConfig.Difficulty.normalMultiplier.get();
				else if (difficulty.equals(Difficulty.HARD))
					chance *= ModConfig.Difficulty.hardMultiplier.get();
				
				chance *= this.multiplier;
			}
		}

		if (random.nextFloat() < chance / 100f)
			return true;
		
		return false;
	}
}
