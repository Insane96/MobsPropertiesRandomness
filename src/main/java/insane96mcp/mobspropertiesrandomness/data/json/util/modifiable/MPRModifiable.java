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
	@SerializedName("pos_modifier")
	public MPRPosModifier posModifier;

	//Applied after pos modifier
	@SerializedName("time_existed_modifier")
	public MPRTimeExistedModifier timeExistedModifier;

	//Applied after time existed modifier
	@SerializedName("condition_modifiers")
	public List<MPRConditionModifier> conditionModifiers;

	//Rounds the final result to this decimal places
	public Integer round;

	public MPRModifiable(MPRDifficultyModifier difficultyModifier, MPRPosModifier posModifier, MPRTimeExistedModifier timeExistedModifier, List<MPRConditionModifier> conditionModifiers, Integer round) {
		this.difficultyModifier = difficultyModifier;
		this.posModifier = posModifier;
		this.timeExistedModifier = timeExistedModifier;
		this.conditionModifiers = conditionModifiers;
		this.round = round;
	}

	public void validate() throws JsonValidationException {
		if (this.difficultyModifier != null)
			this.difficultyModifier.validate();

		if (this.worldSpawnDistanceModifier != null)
			this.worldSpawnDistanceModifier.validate();

		if (this.posModifier != null)
			this.posModifier.validate();

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
			value = this.difficultyModifier.applyModifier(entity.level.getDifficulty(), value);

		if (this.worldSpawnDistanceModifier != null)
			value = this.worldSpawnDistanceModifier.applyModifier(entity, value);

		if (this.posModifier != null)
			value = this.posModifier.applyModifier(entity.level, entity.position(), value);

		if (this.timeExistedModifier != null)
			value = this.timeExistedModifier.applyModifier(entity.level, entity, value);

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
		return String.format("difficulty_modifier: %s, pos_modifier: %s, time_existed_modifier: %s, conditionModifiers: %s, round: %s", this.difficultyModifier, this.posModifier, this.timeExistedModifier, this.conditionModifiers, this.round);
	}
}
