package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.world.entity.LivingEntity;

public class MPRDepthModifier extends MPRModifier implements IMPRObject {

	/*
	 Each 'step' blocks from 'starting_y' will increase the value to modify by 'amount_per_step'. If operation is "multiply" the result is treated as a percentage increase, the formula is 'value * (1 + (distance_below_starting_y / step) * amount_per_step)', else if the operation is "add" the result is added to the value, the formula is 'value + ((distance_below_starting_y / step) * amount_per_step)'
	 E.g. A mob with 50% chance (0.5) to spawn with a potion effect, with this modifier set as step = 10, bonus = 0.02 and operation = "multiply" when it spawns 15 blocks from world spawn it will have the value modified as 'chance * (1 + (distance_below_starting_y / step) * amount_per_step)' = '0.5 * (1 + (15 / 10))' = '0.5 * 1.5' (an increase of 50%) = '0.75 (75% chance)'
	 */
	@SerializedName("amount_per_step")
	public Float amountPerStep;
	public Float step;
	@SerializedName("starting_y")
	public Float startingY;

	@Override
	public void validate() throws JsonValidationException {
		if (this.amountPerStep == null)
			throw new JsonValidationException("amount_per_step is required for Depth Modifier");
		else if (this.step == null)
			throw new JsonValidationException("step is required for Depth Modifier");
		if (this.startingY == null)
			throw new JsonValidationException("starting_y is required for Depth Modifier");

		super.validate();
	}

	@Override
	public float applyModifier(LivingEntity entity, float value) {
		float totalBonus = (Math.max(this.startingY - entity.blockPosition().getY(), 0) / this.step) * this.amountPerStep;

		if (this.getOperation() == Operation.ADD) return value + totalBonus;
		else return value * (1 + totalBonus);
	}

	@Override
	public String toString() {
		return String.format("DepthModifier{%s, amount_per_step: %s, step: %s, starting_y: %s}", super.toString(), this.amountPerStep, this.step, this.startingY);
	}
}
