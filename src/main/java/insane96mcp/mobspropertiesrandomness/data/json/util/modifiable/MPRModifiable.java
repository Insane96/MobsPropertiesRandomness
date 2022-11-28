package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.MathHelper;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;

import java.util.List;

public abstract class MPRModifiable implements IMPRObject {
	@SerializedName("difficulty_modifier")
	public MPRDifficultyModifier difficultyModifier;

	//Applied after difficulty modifier
	@SerializedName("pos_modifier")
	public MPRPosModifier posModifier;

	//Applied after pos modifier
	@SerializedName("time_existed_modifier")
	public MPRTimeExistedModifier timeExistedModifier;

	//Applied after time existed modifier
	@SerializedName("conditions_modifier")
	public List<MPRConditionModifier> conditionModifiers;

	//Rounds the final result to this value
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

	public float round(float value) {
		if (this.round == null)
			return value;
		return MathHelper.round(value, this.round);
	}

	@Override
	public String toString() {
		return String.format("difficulty_modifier: %s, pos_modifier: %s, time_existed_modifier: %s, conditionModifiers: %s, round: %s", difficultyModifier, posModifier, timeExistedModifier, conditionModifiers, round);
	}
}
