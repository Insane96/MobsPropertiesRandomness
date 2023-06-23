package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.world.entity.LivingEntity;

public abstract class MPRModifier implements IMPRObject {
	private Operation operation;

	@Override
	public void validate() throws JsonValidationException {
		if (this.operation == null)
			throw new JsonValidationException("operation missing from Modifier");
	}

	public abstract float applyModifier(LivingEntity entity, float value);

	public Operation getOperation() {
		return this.operation;
	}

	public enum Operation {
		@SerializedName("add")
		ADD,
		@SerializedName("multiply")
		MULTIPLY
	}
}
