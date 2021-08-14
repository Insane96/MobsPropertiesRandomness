package insane96mcp.mobspropertiesrandomness.json;

import insane96mcp.insanelib.utils.RandomHelper;
import insane96mcp.mobspropertiesrandomness.MobsPropertiesRandomness;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRAttribute;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRRange;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.UUID;

public class MPRMobAttribute extends MPRAttribute implements IMPRObject, IMPRAppliable {
	@Override
	public void validate(File file) throws InvalidJsonException {
		super.validate(file);
	}

	@Override
	public void apply(MobEntity entity, World world) {
		if (world.isRemote)
			return;

		if (worldWhitelist != null && worldWhitelist.isWhitelisted(entity))
			return;

		float min = this.amount.getMin();
		float max = this.amount.getMax();

		if (difficultyModifier != null) {
			MPRRange minMax = difficultyModifier.applyModifier(world.getDifficulty(), world.getDifficultyForLocation(entity.getPosition()).getAdditionalDifficulty(), min, max);
			min = minMax.getMin();
			max = minMax.getMax();
		}

		Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(this.attributeId));
		ModifiableAttributeInstance attributeInstance = entity.getAttribute(attribute);
		if (attributeInstance == null) {
			Logger.warn("Attribute " + this.attributeId + " not found for the entity, skipping the attribute");
			return;
		}

		float amount = RandomHelper.getFloat(world.rand, min, max);

		AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), MobsPropertiesRandomness.RESOURCE_PREFIX + this.attributeId, amount, operation);
		attributeInstance.applyPersistentModifier(modifier);

		//Health Fix
		if (this.attributeId.contains("generic.max_health"))
			entity.setHealth((float) attributeInstance.getValue());
		else if (this.attributeId.contains("generic.follow_range"))
			fixFollowRange(entity);
	}

	private void fixFollowRange(MobEntity entity) {
		for (PrioritizedGoal pGoal : entity.targetSelector.goals) {
			if (pGoal.getGoal() instanceof NearestAttackableTargetGoal) {
				NearestAttackableTargetGoal nearestAttackableTargetGoal = (NearestAttackableTargetGoal) pGoal.getGoal();
				nearestAttackableTargetGoal.targetEntitySelector.setDistance(entity.getAttributeValue(Attributes.FOLLOW_RANGE));
			}
		}
	}

	@Override
	public String toString() {
		return String.format("MobAttribute{uuid: %s, attribute_id: %s, amount: %s, operation: %s, difficulty_modifier: %s, world_whitelist: %s}", uuid, attributeId, amount, operation, difficultyModifier, worldWhitelist);
	}
}
