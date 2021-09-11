package insane96mcp.mobspropertiesrandomness.json.utils.modifier;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.modifier.difficulty.MPRDifficultyModifier;

import java.io.File;

public abstract class MPRModifiable implements IMPRObject {
	@SerializedName("difficulty_modifier")
	public MPRDifficultyModifier difficultyModifier;

	//Applied after difficulty modifier
	@SerializedName("pos_modifier")
	public MPRPosModifier posModifier;

	public void validate(final File file) throws InvalidJsonException {
		if (difficultyModifier != null)
			difficultyModifier.validate(file);

		if (posModifier != null)
			posModifier.validate(file);
	}

	@Override
	public String toString() {
		return String.format("Modifiable{difficulty_modifier: %s, pos_modifier: %s}", difficultyModifier, posModifier);
	}
}
