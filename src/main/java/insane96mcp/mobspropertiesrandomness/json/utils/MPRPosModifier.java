package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;

import java.io.File;

public class MPRPosModifier implements IMPRObject {

	/*
	 Each 'distance_from_spawn_step' blocks from spawn will increase the value to modify by 'distance_from_spawn_bonus'%. The formula is 'bonus * (distance_from_spawn / step)'. E.g. with step = 100 and bonus = 0.02 when a mob spawns 150 blocks from spawn will have the value modified as '0.02 * (150 / 100)' = '0.02 * 1.5' = '+3%'
	 */
	@SerializedName("distance_from_spawn_bonus")
	public double distanceFromSpawnBonus;
	@SerializedName("distance_from_spawn_step")
	public double distanceFromSpawnStep;

	/*
	 Each 'depth_step' blocks below 'depth_starting_level' will increase the value to modify by 'depth_bonus'%. The formula is 'bonus * (MAX(starting_level - spawning_y, 0) / step)'. E.g. with step = 1, bonus = 0.01 and starting_level = 64 when a mob spawns at y = 24 you'll have '0.01 * (MIN(64 - 24, 0) / 1)' = '0.01 * 40' = '+40%'
	 */
	@SerializedName("depth_bonus")
	public double depthBonus;
	@SerializedName("depth_step")
	public double depthStep;
	@SerializedName("depth_starting_level")
	public double depthStartingLevel;

	public MPRPosModifier() {
		this.distanceFromSpawnBonus = 0d;
		this.distanceFromSpawnStep = 100d;
		this.depthBonus = 0d;
		this.depthStep = 1d;
		this.depthStartingLevel = 64d;
	}

	@Override
	public void validate(File file) throws InvalidJsonException {

	}
}
