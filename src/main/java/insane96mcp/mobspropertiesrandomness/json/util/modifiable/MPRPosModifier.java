package insane96mcp.mobspropertiesrandomness.json.util.modifiable;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MPRPosModifier extends MPRModifier implements IMPRObject {

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
	public void validate() throws JsonValidationException {
		if (this.distanceFromSpawnBonus != null && this.distanceFromSpawnStep == null || this.distanceFromSpawnBonus == null && this.distanceFromSpawnStep != null)
			throw new JsonValidationException("distance_from_spawn_bonus and distance_from_spawn_step are required for each other. " + this);

		if (this.depthBonus != null && this.depthStep == null || this.depthBonus == null && this.depthStep != null)
			throw new JsonValidationException("depth_bonus and depth_step are required for each other. " + this);

		super.validate();
	}

	public float applyModifier(Level world, Vec3 pos, float value) {
		//Distance from Spawn
		float totalBonus = 0;
		if (this.distanceFromSpawnBonus != null) {
			Vec3 spawnPos = new Vec3(world.getLevelData().getXSpawn(), world.getLevelData().getYSpawn(), world.getLevelData().getZSpawn());
			float distance = (float) spawnPos.distanceTo(pos);
			totalBonus += (distance / distanceFromSpawnStep) * distanceFromSpawnBonus;
		}

		//Depth
		if (this.depthBonus != null) {
			totalBonus += (float) ((Math.max(depthStartingLevel - pos.y, 0) / depthStep) * depthBonus);
		}

		return value * (1 + totalBonus);
	}

	@Override
	public String toString() {
		return String.format("PosModifier{distance_from_spawn_bonus: %s, distance_from_spawn_step: %s, depth_bonus: %s, depth_step: %s, depth_starting_level: %s, affects_max_only: %s}", distanceFromSpawnBonus, distanceFromSpawnStep, depthBonus, depthStep, depthStartingLevel, this.doesAffectMaxOnly());
	}
}
