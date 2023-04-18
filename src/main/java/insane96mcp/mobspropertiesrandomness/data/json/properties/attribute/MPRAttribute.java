package insane96mcp.mobspropertiesrandomness.data.json.properties.attribute;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.properties.condition.MPRConditions;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import java.util.UUID;

public abstract class MPRAttribute implements IMPRObject {
	public String uuid;
	public String id;
	@SerializedName("modifier_name")
	public String modifierName;
	public MPRRange amount;
	public Operation operation;
	public MPRModifiableValue chance;
	public MPRConditions conditions;

	@Override
	public void validate() throws JsonValidationException {
		if (this.uuid == null)
			this.uuid = UUID.randomUUID().toString();

		if (this.id == null)
			throw new JsonValidationException("Missing Id. " + this);

		if (this.modifierName == null)
			throw new JsonValidationException("Missing Modifier Name. " + this);

		if (this.amount == null)
			throw new JsonValidationException("Missing Amount. " + this);
		this.amount.validate();

		if (this.operation == null)
			throw new JsonValidationException("Missing Operation. " + this);

		if (this.chance != null)
			this.chance.validate();

		if (this.conditions != null)
			this.conditions.validate();
	}

	public boolean shouldApply(LivingEntity entity, Level world) {
		if (world.isClientSide)
			return false;

		if (this.chance != null && world.random.nextFloat() >= this.chance.getValue(entity, world))
			return false;

		return this.conditions == null || this.conditions.conditionsApply(entity);
	}

	protected void fixHealth(LivingEntity entity) {
		if (this.id.contains("generic.max_health")) {
			AttributeInstance attributeInstance = entity.getAttribute(Attributes.MAX_HEALTH);
			if (attributeInstance != null)
				entity.setHealth((float) attributeInstance.getValue());
			entity.setHealth((float) entity.getAttributeValue(Attributes.MAX_HEALTH));
		}
	}

	@Override
	public String toString() {
		return String.format("Attribute{uuid: %s, id: %s, modifier_name: %s, amount: %s, operation: %s, conditions: %s}", this.uuid, this.id, this.modifierName, this.amount, this.operation, this.conditions);
	}

	public enum Operation {
		@SerializedName("addition")
		ADDITION(AttributeModifier.Operation.ADDITION),
		@SerializedName("multiply_base")
		MULTIPLY_BASE(AttributeModifier.Operation.MULTIPLY_BASE),
		@SerializedName("multiply_total")
		MULTIPLY_TOTAL(AttributeModifier.Operation.MULTIPLY_TOTAL);

		AttributeModifier.Operation operation;
		public AttributeModifier.Operation get() {
			return this.operation;
		}

		Operation(AttributeModifier.Operation operation) {
			this.operation = operation;
		}
	}
}