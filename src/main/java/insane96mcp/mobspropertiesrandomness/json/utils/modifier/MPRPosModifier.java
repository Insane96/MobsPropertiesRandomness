package insane96mcp.mobspropertiesrandomness.json.utils.modifier;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRRange;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.io.File;

public class MPRPosModifier implements IMPRObject {

	/*
	 Each 'distance_from_spawn_step' blocks from spawn will increase the value to modify by 'distance_from_spawn_bonus'%. The formula is 'bonus * (distance_from_spawn / step)'. E.g. with step = 100 and bonus = 0.02 when a mob spawns 150 blocks from spawn will have the value modified as '0.02 * (150 / 100)' = '0.02 * 1.5' = '+3%'
	 */
	@SerializedName("distance_from_spawn_bonus")
	public float distanceFromSpawnBonus;
	@SerializedName("distance_from_spawn_step")
	public float distanceFromSpawnStep;

	/*
	 Each 'depth_step' blocks below 'depth_starting_level' will increase the value to modify by 'depth_bonus'%. The formula is 'bonus * (MAX(starting_level - spawning_y, 0) / step)'. E.g. with step = 1, bonus = 0.01 and starting_level = 64 when a mob spawns at y = 24 you'll have '0.01 * (MIN(64 - 24, 0) / 1)' = '0.01 * 40' = '+40%'
	 */
	@SerializedName("depth_bonus")
	public float depthBonus;
	@SerializedName("depth_step")
	public float depthStep;
	@SerializedName("depth_starting_level")
	public float depthStartingLevel;

	/*
	The two bonuses are summed up, so in the example the result would be a +43%
	 */

	public MPRPosModifier() {
		this.distanceFromSpawnBonus = 0f;
		this.distanceFromSpawnStep = 100f;
		this.depthBonus = 0f;
		this.depthStep = 1f;
		this.depthStartingLevel = 64f;
	}

	@Override
	public void validate(File file) throws InvalidJsonException {

	}

	public MPRRange applyModifier(World world, Vector3d pos, float min, float max) {
		max = applyModifier(world, pos, max);
		min = applyModifier(world, pos, min);

		return new MPRRange(min, max);
	}

	public float applyModifier(World world, Vector3d pos, float value) {

		//Distance from Spawn
		Vector3d spawnPos = new Vector3d(world.getWorldInfo().getSpawnX(), world.getWorldInfo().getSpawnY(), world.getWorldInfo().getSpawnZ());
		float distance = (float) spawnPos.distanceTo(pos);
		float distancePercentage = (distance / distanceFromSpawnStep) * distanceFromSpawnBonus;

		//Depth
		float depthPercentage = (float) ((Math.max(depthStartingLevel - pos.getY(), 0) / depthStep) * depthBonus);

		float totalBonus = distancePercentage + depthPercentage;
		return value * (1 + totalBonus);
	}
}
