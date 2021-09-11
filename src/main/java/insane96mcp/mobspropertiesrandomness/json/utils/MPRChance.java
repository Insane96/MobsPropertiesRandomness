package insane96mcp.mobspropertiesrandomness.json.utils;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.modifier.MPRModifiable;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;

import java.io.File;

public class MPRChance extends MPRModifiable implements IMPRObject {
	public Float amount;

	public void validate(final File file) throws InvalidJsonException {
		if (amount == null)
			throw new InvalidJsonException("Chance is missing amount", file);

		super.validate(file);
	}

	public boolean chanceMatches(MobEntity entity, World world) {
		float chance = this.amount;
		if (this.difficultyModifier != null)
			chance = this.difficultyModifier.applyModifier(world.getDifficulty(), world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty(), chance);
		if (this.posModifier != null)
			chance = this.posModifier.applyModifier(world, entity.getPositionVec(), chance);

		return world.rand.nextFloat() < chance;
	}

	@Override
	public String toString() {
		return String.format("Chance{amount: %f, %s}", amount, super.toString());
	}
}
