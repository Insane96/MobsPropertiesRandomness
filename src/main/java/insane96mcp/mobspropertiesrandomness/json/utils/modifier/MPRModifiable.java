package insane96mcp.mobspropertiesrandomness.json.utils.modifier;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.modifier.difficulty.MPRDifficultyModifier;
import insane96mcp.mobspropertiesrandomness.utils.MathHelper;

import java.io.File;

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

	public void validate(final File file) throws InvalidJsonException {
		if (this.difficultyModifier != null)
			this.difficultyModifier.validate(file);

		if (this.posModifier != null)
			this.posModifier.validate(file);

		if (this.timeExistedModifier != null)
			this.timeExistedModifier.validate(file);
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
