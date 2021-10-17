package insane96mcp.mobspropertiesrandomness.json.utils.modifier;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.io.File;

public class MPRPosModifier implements IMPRObject {

	/*
	 Each 'distance_from_spawn_step' blocks from spawn will increase the value to modify by 'distance_from_spawn_bonus'%. The formula is 'bonus * (distance_from_spawn / step)'. E.g. with step = 100 and bonus = 0.02 when a mob spawns 150 blocks from spawn will have the value modified as '0.02 * (150 / 100)' = '0.02 * 1.5' = '+3%'
	 */
	@SerializedName("distance_from_spawn_bonus")
	public Float distanceFromSpawnBonus;
	@SerializedName("distance_from_spawn_step")
	public Float distanceFromSpawnStep;

	/*
	 Each 'depth_step' blocks below 'depth_starting_level' will increase the value to modify by 'depth_bonus'%. The formula is 'bonus * (MAX(starting_level - spawning_y, 0) / step)'. E.g. with step = 1, bonus = 0.01 and starting_level = 64 when a mob spawns at y = 24 you'll have '0.01 * (MIN(64 - 24, 0) / 1)' = '0.01 * 40' = '+40%'
	 */
	@SerializedName("depth_bonus")
	public Float depthBonus;
	@SerializedName("depth_step")
	public Float depthStep;
	@SerializedName("depth_starting_level")
	public Float depthStartingLevel;

	/*
	The two bonuses are summed up, so in the example the result would be a +43%
	 */

	public MPRPosModifier() {
		this.depthStartingLevel = 64f;
	}

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (this.distanceFromSpawnBonus != null && this.distanceFromSpawnStep == null || this.distanceFromSpawnBonus == null && this.distanceFromSpawnStep != null)
			throw new InvalidJsonException("distance_from_spawn_bonus and distance_from_spawn_step are required for eachother. " + this, file);

		if (this.depthBonus != null && this.depthStep == null || this.depthBonus == null && this.depthStep != null)
			throw new InvalidJsonException("depth_bonus and depth_step are required for eachother. " + this, file);
	}

	public float applyModifier(World world, Vector3d pos, float value) {
		//Distance from Spawn
		Vector3d spawnPos = new Vector3d(world.getLevelData().getXSpawn(), world.getLevelData().getYSpawn(), world.getLevelData().getZSpawn());
		float distance = (float) spawnPos.distanceTo(pos);
		float distancePercentage = (distance / distanceFromSpawnStep) * distanceFromSpawnBonus;

		//Depth
		float depthPercentage = (float) ((Math.max(depthStartingLevel - pos.y, 0) / depthStep) * depthBonus);

		float totalBonus = distancePercentage + depthPercentage;
		return value * (1 + totalBonus);
	}

	@Override
	public String toString() {
		return String.format("PosModifier{distance_from_spawn_bonus: %s, distance_from_spawn_step: %s, depth_bonus: %s, depth_step: %s, depth_starting_level: %s}", distanceFromSpawnBonus, distanceFromSpawnStep, depthBonus, depthStep, depthStartingLevel);
	}
}
