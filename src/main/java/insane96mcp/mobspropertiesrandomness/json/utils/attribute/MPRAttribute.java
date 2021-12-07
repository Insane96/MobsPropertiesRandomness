package insane96mcp.mobspropertiesrandomness.json.utils.attribute;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRRange;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRWorldWhitelist;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.world.World;

import java.io.File;
import java.util.UUID;

public abstract class MPRAttribute implements IMPRObject {
	public String uuid;
	public String id;
	@SerializedName("modifier_name")
	public String modifierName;
	public MPRRange amount;
	public AttributeModifier.Operation operation;
	public MPRModifiableValue chance;
	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (this.uuid == null)
			this.uuid = UUID.randomUUID().toString();

		if (this.id == null)
			throw new InvalidJsonException("Missing Id. " + this, file);

		if (this.modifierName == null)
			throw new InvalidJsonException("Missing Modifier Name. " + this, file);

		if (this.amount == null)
			throw new InvalidJsonException("Missing Amount. " + this, file);
		this.amount.validate(file);

		if (this.operation == null)
			throw new InvalidJsonException("Missing Operation. " + this, file);

		if (this.chance != null)
			this.chance.validate(file);

		if (this.worldWhitelist != null)
			this.worldWhitelist.validate(file);
	}

	public boolean shouldApply(MobEntity entity, World world) {
		if (world.isClientSide)
			return false;

		if (this.chance != null && world.random.nextFloat() >= this.chance.getValue(entity, world))
			return false;

		if (worldWhitelist != null && worldWhitelist.isWhitelisted(entity))
			return false;

		return true;
	}

	protected void fixHealth(MobEntity entity) {
		if (this.id.contains("generic.max_health")) {
			ModifiableAttributeInstance attributeInstance = entity.getAttribute(Attributes.MAX_HEALTH);
			if (attributeInstance != null)
				entity.setHealth((float) attributeInstance.getValue());
			entity.setHealth((float) entity.getAttributeValue(Attributes.MAX_HEALTH));
		}
	}

	@Override
	public String toString() {
		return String.format("Attribute{uuid: %s, id: %s, modifier_name: %s, amount: %s, operation: %s, world_whitelist: %s}", uuid, id, modifierName, amount, operation, worldWhitelist);
	}
}