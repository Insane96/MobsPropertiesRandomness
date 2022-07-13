package insane96mcp.mobspropertiesrandomness.json.util.modifiable;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.MathHelper;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;

public abstract class MPRModifiable implements IMPRObject {
	@SerializedName("difficulty_modifier")
	public MPRDifficultyModifier difficultyModifier;

	//Applied after difficulty modifier
	@SerializedName("pos_modifier")
	public MPRPosModifier posModifier;

	//Applied after pos modifier
	@SerializedName("time_existed_modifier")
	public MPRTimeExistedModifier timeExistedModifier;

	//Rounds the final result to this value
	public Integer round;

	public MPRModifiable(MPRDifficultyModifier difficultyModifier, MPRPosModifier posModifier, MPRTimeExistedModifier timeExistedModifier, Integer round) {
		this.difficultyModifier = difficultyModifier;
		this.posModifier = posModifier;
		this.timeExistedModifier = timeExistedModifier;
		this.round = round;
	}

	public void validate() throws JsonValidationException {
		if (this.difficultyModifier != null)
			this.difficultyModifier.validate();

		if (this.posModifier != null)
			this.posModifier.validate();

		if (this.timeExistedModifier != null)
			this.timeExistedModifier.validate();
	}

	public float round(float value) {
		if (this.round == null)
			return value;
		return MathHelper.round(value, this.round);
	}

	@Override
	public String toString() {
		return String.format("difficulty_modifier: %s, pos_modifier: %s, time_existed_modifier: %s, round: %s", difficultyModifier, posModifier, timeExistedModifier, round);
	}
}
