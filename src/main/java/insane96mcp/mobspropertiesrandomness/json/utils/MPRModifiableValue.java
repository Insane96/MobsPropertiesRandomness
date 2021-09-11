package insane96mcp.mobspropertiesrandomness.json.utils;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.modifier.MPRModifiable;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;

import java.io.File;

public class MPRModifiableValue extends MPRModifiable implements IMPRObject {
	private Float value;

	public void validate(final File file) throws InvalidJsonException {
		if (this.value == null)
			throw new InvalidJsonException("Missing value. " + this, file);

		super.validate(file);
	}

	public float getValue(MobEntity entity, World world) {
		float value = this.value;

		if (this.difficultyModifier != null)
			value = this.difficultyModifier.applyModifier(world.getDifficulty(), world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty(), value);

		if (this.posModifier != null)
			value = this.posModifier.applyModifier(world, entity.getPositionVec(), value);

		return value;
	}

	@Override
	public String toString() {
		return String.format("ModifiableValue{value: %f, %s}", this.value, super.toString());
	}
}