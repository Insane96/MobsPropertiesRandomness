package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;

import java.io.File;
import java.util.UUID;

public class MPRItemAttribute implements IMPRObject {
	//TODO Create a generic MPRAttribute and then item and mob attribute
	public UUID id;
	@SerializedName("attribute_name")
	public String attributeName;
	public AttributeModifier.Operation operation;
	public MPRRange amount;
	public EquipmentSlotType slot;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (attributeName == null) {
			throw new InvalidJsonException("Missing attribute name for " + this, file);
		}

		if (operation == null)
			throw new InvalidJsonException("Missing modifier operation for " + this, file);

		if (amount != null)
			amount.validate(file);
		else
			throw new InvalidJsonException("Missing Amount for " + this, file);

		//TODO Make this mandatory?
		if (id == null)
			id = UUID.randomUUID();

		if (slot == null)
			Logger.debug("Missing Equipment Slot for " + this + ". Will now default to the item equipment slot");
	}

	@Override
	public String toString() {
		return String.format("ItemAttribute{attribute_name: %s, operation: %s, amount: %s, id: %s, slot: %s}", attributeName, operation, amount, id, slot);
	}
}
