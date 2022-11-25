package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject_old;

public abstract class MPRModifier implements IMPRObject_old {
	@SerializedName("affects_max_only")
	private Boolean affectsMaxOnly;

	public MPRModifier() {
		this.affectsMaxOnly = false;
	}

	@Override
	public void validate() throws JsonValidationException {

	}

	public boolean doesAffectMaxOnly() {
		return this.affectsMaxOnly;
	}

	public enum Operation {
		@SerializedName("add")
		ADD,
		@SerializedName("multiply")
		MULTIPLY
	}
}
