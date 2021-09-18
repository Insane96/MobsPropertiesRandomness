package insane96mcp.mobspropertiesrandomness.json.utils;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.modifier.MPRModifiable;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;

import java.io.File;

public class MPRRange extends MPRModifiable implements IMPRObject {
	private Float min;
	private Float max;

	public MPRRange(int min, int max) {
		this((float) min, (float) max);
	}

	public MPRRange(float min, float max) {
		super(null, null);
		this.min = min;
		this.max = Math.max(min, max);
	}

	public void validate(final File file) throws InvalidJsonException {
		if (min == null)
			throw new InvalidJsonException("Missing min. " + this, file);

		if (max == null) {
			Logger.info("Missing max for " + this + ". Max will be equal to min.");
			max = min;
		}

		if (max < min)
			throw new InvalidJsonException("Min cannot be greater than max. " + this, file);

		super.validate(file);
	}

	public float getMin(MobEntity entity, World world) {
		float min = this.min;

		if (this.difficultyModifier != null && !this.difficultyModifier.affectsMaxOnly)
			min = this.difficultyModifier.applyModifier(world.getDifficulty(), world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty(), min);

		if (this.posModifier != null)
			min = this.posModifier.applyModifier(world, entity.getPositionVec(), min);

		return min;
	}

	public float getMax(MobEntity entity, World world) {
		float max = this.max;

		if (this.difficultyModifier != null)
			max = this.difficultyModifier.applyModifier(world.getDifficulty(), world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty(), max);

		if (this.posModifier != null)
			max = this.posModifier.applyModifier(world, entity.getPositionVec(), max);

		return max;
	}

	@Override
	public String toString() {
		return String.format("Range{min: %f, max: %f, %s}", min, max, super.toString());
	}
}
