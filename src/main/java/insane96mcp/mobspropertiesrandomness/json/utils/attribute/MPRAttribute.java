package insane96mcp.mobspropertiesrandomness.json.utils.attribute;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRRange;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRWorldWhitelist;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.io.File;
import java.util.UUID;

public abstract class MPRAttribute implements IMPRObject {
	public String uuid;

	@SerializedName("attribute_id")
	public String attributeId;

	@SerializedName("modifier_name")
	public String modifierName;

	public MPRRange amount;

	public AttributeModifier.Operation operation;

	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (uuid == null)
			uuid = UUID.randomUUID().toString();

		if (attributeId == null)
			throw new InvalidJsonException("Missing Attribute Id. " + this, file);

		if (modifierName == null)
			throw new InvalidJsonException("Missing Modifier Name. " + this, file);

		if (amount == null)
			throw new InvalidJsonException("Missing Amount (Min/Max). " + this, file);
		amount.validate(file);

		if (operation == null)
			throw new InvalidJsonException("Missing Operation. " + this, file);

		if (worldWhitelist != null)
			worldWhitelist.validate(file);
	}

	@Override
	public String toString() {
		return String.format("Attribute{uuid: %s, attribute_id: %s, modifier_name: %s, amount: %s, operation: %s, world_whitelist: %s}", uuid, attributeId, modifierName, amount, operation, worldWhitelist);
	}
}