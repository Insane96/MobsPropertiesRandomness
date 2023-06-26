package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;

public class MPRDifficultyModifier extends MPRModifier implements IMPRObject {
	public Float easy;
	public Float normal;
	public Float hard;

	@Override
	public void validate() throws JsonValidationException {
		if (this.getOperation() == null)
			throw new JsonValidationException("Missing Operation for Difficulty object. " + this);

		if (this.getOperation() == Operation.ADD) {
			if (this.easy == null)
				this.easy = 0f;
			if (this.normal == null)
				this.normal = 0f;
			if (this.hard == null)
				this.hard = 0f;
		}
		else if (this.getOperation() == Operation.MULTIPLY) {
			if (this.easy == null)
				this.easy = 1f;
			if (this.normal == null)
				this.normal = 1f;
			if (this.hard == null)
				this.hard = 1f;
		}

		super.validate();
	}

	@Override
	public float applyModifier(LivingEntity entity, float value) {
		Difficulty worldDifficulty = entity.level.getDifficulty();
		switch (worldDifficulty) {
			case EASY -> {
				if (this.getOperation() == Operation.ADD)
					value += this.easy;
				if (this.getOperation() == Operation.MULTIPLY)
					value *= this.easy;
			}
			case NORMAL -> {
				if (this.getOperation() == Operation.ADD)
					value += this.normal;
				if (this.getOperation() == Operation.MULTIPLY)
					value *= this.normal;
			}
			case HARD -> {
				if (this.getOperation() == Operation.ADD)
					value += this.hard;
				if (this.getOperation() == Operation.MULTIPLY)
					value *= this.hard;
			}
		}
		return value;
	}

	@Override
	public String toString() {
		return String.format("DifficultyModifier{operation: %s, easy: %f, normal: %f, hard: %f}", this.getOperation(), this.easy, this.normal, this.hard);
	}
}
