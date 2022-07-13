package insane96mcp.mobspropertiesrandomness.json.properties.attribute;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.util.MPRWorldWhitelist;
import insane96mcp.mobspropertiesrandomness.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.json.util.modifiable.MPRRange;
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
	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

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

		if (this.worldWhitelist != null)
			this.worldWhitelist.validate();
	}

	public boolean shouldApply(LivingEntity entity, Level world) {
		if (world.isClientSide)
			return false;

		if (this.chance != null && world.random.nextFloat() >= this.chance.getValue(entity, world))
			return false;

		if (worldWhitelist != null && !worldWhitelist.isWhitelisted(entity))
			return false;

		return true;
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
		return String.format("Attribute{uuid: %s, id: %s, modifier_name: %s, amount: %s, operation: %s, world_whitelist: %s}", uuid, id, modifierName, amount, operation, worldWhitelist);
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

		private Operation(AttributeModifier.Operation operation) {
			this.operation = operation;
		}
	}
}