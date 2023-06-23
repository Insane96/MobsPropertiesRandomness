package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.MathHelper;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public abstract class MPRModifiable implements IMPRObject {
	@SerializedName("difficulty_modifier")
	public MPRDifficultyModifier difficultyModifier;

	//Applied after difficulty modifier
	@SerializedName("world_spawn_distance_modifier")
	public MPRWorldSpawnDistanceModifier worldSpawnDistanceModifier;

	//Applied after world spawn distance modifier
	@SerializedName("depth_modifier")
	public MPRDepthModifier depthModifier;

	//Applied after depth modifier
	@SerializedName("time_existed_modifier")
	public MPRTimeExistedModifier timeExistedModifier;

	//Applied after time existed modifier
	@SerializedName("condition_modifiers")
	public List<MPRConditionModifier> conditionModifiers;

	//Rounds the final result to this decimal places
	public Integer round;

	public MPRModifiable(MPRDifficultyModifier difficultyModifier, MPRWorldSpawnDistanceModifier worldSpawnDistanceModifier, MPRDepthModifier depthModifier, MPRTimeExistedModifier timeExistedModifier, List<MPRConditionModifier> conditionModifiers, Integer round) {
		this.difficultyModifier = difficultyModifier;
		this.worldSpawnDistanceModifier = worldSpawnDistanceModifier;
		this.depthModifier = depthModifier;
		this.timeExistedModifier = timeExistedModifier;
		this.conditionModifiers = conditionModifiers;
		this.round = round;
	}

	public void validate() throws JsonValidationException {
		if (this.difficultyModifier != null)
			this.difficultyModifier.validate();

		if (this.worldSpawnDistanceModifier != null)
			this.worldSpawnDistanceModifier.validate();

		if (this.depthModifier != null)
			this.depthModifier.validate();

		if (this.timeExistedModifier != null)
			this.timeExistedModifier.validate();

		if (this.conditionModifiers != null) {
			for (MPRConditionModifier conditionModifier : this.conditionModifiers) {
				conditionModifier.validate();
			}
		}
	}

	public float applyModifiersAndRound(LivingEntity entity, float value) {
		if (this.difficultyModifier != null)
			value = this.difficultyModifier.applyModifier(entity, value);

		if (this.worldSpawnDistanceModifier != null)
			value = this.worldSpawnDistanceModifier.applyModifier(entity, value);

		if (this.depthModifier != null)
			value = this.depthModifier.applyModifier(entity, value);

		if (this.timeExistedModifier != null)
			value = this.timeExistedModifier.applyModifier(entity, value);

		if (this.conditionModifiers != null) {
			for (MPRConditionModifier conditionModifier : this.conditionModifiers) {
				value = conditionModifier.applyModifier(entity, value);
			}
		}

		return this.round(value);
	}

	public float round(float value) {
		if (this.round == null)
			return value;
		return MathHelper.round(value, this.round);
	}

	@Override
	public String toString() {
		return String.format("difficulty_modifier: %s, world_spawn_distance_modifier: %s, depth_modifier: %s, time_existed_modifier: %s, conditionModifiers: %s, round: %s", this.difficultyModifier, this.worldSpawnDistanceModifier, this.depthModifier, this.timeExistedModifier, this.conditionModifiers, this.round);
	}
}
