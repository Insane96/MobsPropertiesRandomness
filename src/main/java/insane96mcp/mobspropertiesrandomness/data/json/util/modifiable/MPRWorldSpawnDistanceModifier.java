package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class MPRWorldSpawnDistanceModifier extends MPRModifier implements IMPRObject {

	/*
	 Each 'step' blocks from spawn will increase the value to modify by 'amount_per_step'.
	 If operation is "multiply" the result is treated as a percentage increase, the formula is 'value * (1 + ((distance + shift) / step) * amount_per_step)'.
	 Else if the operation is "add" the result is added to the value, the formula is 'value + (((distance + shift) / step) * amount_per_step)'
	 E.g. A mob with 50% chance (0.5) to spawn with a potion effect, with this modifier set as step = 100, bonus = 0.02 and operation = "multiply" when it spawns 150 blocks from world spawn it will have the value modified as 'chance * (1 + ((distance + shift) / step) * amount_per_step)' = '0.5 * (1 + ((150 + 0) / 100))' = '0.5 * 1.5' (an increase of 50%) = '0.75 (75% chance)'
	 */
	@SerializedName("amount_per_step")
	public MPRModifiableValue amountPerStep;
	public MPRModifiableValue step;
	/**
	 * Shifts the distance from spawn by this value
	 */
	public MPRModifiableValue shift;

	public MPRWorldSpawnDistanceModifier() {
		this.shift = new MPRModifiableValue(0f);
	}

	@Override
	public void validate() throws JsonValidationException {
		if (this.amountPerStep == null || this.step == null)
			throw new JsonValidationException("amount_per_step or step are missing for World Spawn Distance Modifier. " + this);

		super.validate();
	}

	public float applyModifier(LivingEntity entity, float value) {
		Vec3 spawnPos = new Vec3(entity.level.getLevelData().getXSpawn(), entity.level.getLevelData().getYSpawn(), entity.level.getLevelData().getZSpawn());
		float distance = (float) spawnPos.distanceTo(entity.position()) + this.shift.getValue(entity);
		float totalBonus = (distance / this.step.getValue(entity)) * this.amountPerStep.getValue(entity);

		if (this.getOperation() == Operation.ADD) return value + totalBonus;
		else return value * (1 + totalBonus);
	}

	@Override
	public String toString() {
		return String.format("PosModifier{%s, amount_per_step: %s, step: %s, shift: %s}", super.toString(), this.amountPerStep, this.step, this.shift);
	}
}
