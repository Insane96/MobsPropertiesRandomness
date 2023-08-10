package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.properties.condition.MPRConditions;
import net.minecraft.world.entity.LivingEntity;

public class MPRConditionModifier extends MPRModifier implements IMPRObject {
	public MPRConditions condition;
	public MPRModifiableValue amount;

	@Override
	public void validate() throws JsonValidationException {
		if (this.condition == null)
			throw new JsonValidationException("Missing 'condition' for Condition Modifier. " + this);
		if (this.amount == null)
			throw new JsonValidationException("Missing 'amount' for Condition Modifier. " + this);

		this.condition.validate();

		super.validate();
	}

	public float applyModifier(LivingEntity livingEntity, float value) {
		if (this.condition.conditionsApply(livingEntity)) {
			if (this.getOperation() == Operation.ADD)
				return value + this.amount.getValue(livingEntity);
			else
				return value * this.amount.getValue(livingEntity);
		}
		return value;
	}

	@Override
	public String toString() {
		return String.format("ConditionModifier{condition: %s, operation: %s, amount: %s}", this.condition, this.getOperation(), this.amount);
	}
}
