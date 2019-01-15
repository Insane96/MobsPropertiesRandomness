package net.insane96mcp.mpr.json.utils;

import java.io.File;
import java.util.Random;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.insane96mcp.mpr.json.IJsonObject;
import net.insane96mcp.mpr.lib.Logger;
import net.insane96mcp.mpr.lib.Properties;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

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
	
	public void Validate(final File file) throws InvalidJsonException{
		if (amount == 0.0f)
			throw new InvalidJsonException("Missing chance in " + this.toString(), file);
		
		if (multiplier == 0.0f) {
			Logger.Debug("Missing multiplier, defaulting to 1.0 for " + this.toString());
			multiplier = 1.0f;
		}
		
		if (isLocalDifficulty)
			affectedByDifficulty = true;
	}
	
	public boolean ChanceMatches(EntityLiving entity, World world, Random random) {
		float chance = this.amount;
		if (this.affectedByDifficulty) {
			if (this.isLocalDifficulty) {
				chance *= world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty() * this.multiplier;
			}
			else {
				EnumDifficulty difficulty = world.getDifficulty();
				if (difficulty.equals(EnumDifficulty.EASY))
					chance *= Properties.config.difficulty.easyMultiplier;
				else if (difficulty.equals(EnumDifficulty.NORMAL))
					chance *= Properties.config.difficulty.normalMultiplier;
				else if (difficulty.equals(EnumDifficulty.HARD))
					chance *= Properties.config.difficulty.hardMultiplier;
				
				chance *= this.multiplier;
			}
		}

		if (random.nextFloat() < chance / 100f)
			return true;
		
		return false;
	}
}
