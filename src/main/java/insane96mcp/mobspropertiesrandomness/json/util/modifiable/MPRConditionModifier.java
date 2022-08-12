package insane96mcp.mobspropertiesrandomness.json.util.modifiable;

import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.properties.condition.MPRConditions;
import net.minecraft.world.entity.LivingEntity;

public class MPRConditionModifier extends MPRModifier implements IMPRObject {
	public MPRConditions condition;
	public Operation operation;
	public Float amount;

	@Override
	public void validate() throws JsonValidationException {
		if (condition == null)
			throw new JsonValidationException("Missing 'condition' for Condition Modifier. " + this);
		if (operation == null)
			throw new JsonValidationException("Missing 'operation' for Condition Modifier. " + this);
		if (amount == null)
			throw new JsonValidationException("Missing 'amount' for Condition Modifier. " + this);

		condition.validate();

		super.validate();
	}

	public float applyModifier(LivingEntity livingEntity, float value) {
		if (this.condition.conditionsApply(livingEntity)) {
			if (operation == Operation.ADD)
				return value + amount;
			else
				return value * amount;
		}
		return value;
	}

	@Override
	public String toString() {
		return String.format("ConditionModifier{condition: %s, operation: %s, amount: %s, affects_max_only: %b}", condition, operation, amount, this.affectsMaxOnly());
	}
}
