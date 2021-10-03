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

	//Rounds the result to this value
	public Integer round;

	public MPRModifiable(MPRDifficultyModifier difficultyModifier, MPRPosModifier posModifier, Integer round) {
		this.difficultyModifier = difficultyModifier;
		this.posModifier = posModifier;
		this.round = round;
	}

	public void validate(final File file) throws InvalidJsonException {
		if (difficultyModifier != null)
			difficultyModifier.validate(file);

		if (posModifier != null)
			posModifier.validate(file);

	}

	public float round(float value) {
		if (this.round == null)
			return value;
		return MathHelper.round(value, this.round);
	}

	@Override
	public String toString() {
		return String.format("difficulty_modifier: %s, pos_modifier: %s, round: %s", difficultyModifier, posModifier, round);
	}
}
