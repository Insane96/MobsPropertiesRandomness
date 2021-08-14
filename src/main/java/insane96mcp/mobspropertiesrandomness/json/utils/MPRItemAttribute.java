package insane96mcp.mobspropertiesrandomness.json.utils;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.inventory.EquipmentSlotType;

import java.io.File;

public class MPRItemAttribute extends MPRAttribute implements IMPRObject {
	public EquipmentSlotType slot;

	@Override
	public void validate(File file) throws InvalidJsonException {
		super.validate(file);
	}

	@Override
	public String toString() {
		return String.format("ItemAttribute{uuid: %s, attribute_id: %s, modifier_name: %s, amount: %s, operation: %s, difficulty_modifier: %s, world_whitelist: %s, slot: %s}", uuid, attributeId, modifierName, amount, operation, difficultyModifier, worldWhitelist, slot);
	}
}
