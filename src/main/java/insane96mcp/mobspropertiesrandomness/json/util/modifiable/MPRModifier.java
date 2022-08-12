package insane96mcp.mobspropertiesrandomness.json.util.modifiable;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;

public abstract class MPRModifier implements IMPRObject {
	@SerializedName("affects_max_only")
	private Boolean affectsMaxOnly;

	@Override
	public void validate() throws JsonValidationException {
		if (this.affectsMaxOnly == null)
			this.affectsMaxOnly = false;
	}

	public boolean affectsMaxOnly() {
		return this.affectsMaxOnly;
	}

	public enum Operation {
		@SerializedName("add")
		ADD,
		@SerializedName("multiply")
		MULTIPLY
	}
}
