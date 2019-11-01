package insane96mcp.mpr.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mpr.exceptions.InvalidJsonException;
import insane96mcp.mpr.json.IJsonObject;
import insane96mcp.mpr.utils.Logger;
import net.minecraft.inventory.EquipmentSlotType;

import java.io.File;
import java.util.UUID;

public class ItemAttribute implements IJsonObject{
	
	public String modifier;
	@SerializedName("attribute_name")
	public String attributeName;
	public EnumOperation operation;
	public RangeMinMax amount;
	public UUID id;
	public EquipmentSlotType slot;
	
	@Override
	public String toString() {
		return String.format("ItemAttribute{modifier: %s, attributeName: %s, operation: %s, amount: %s, id: %s, slot: %s}", modifier, attributeName, operation, amount, id, slot);
	}
	
	public void validate(final File file) throws InvalidJsonException {
		if (modifier == null) {
			throw new InvalidJsonException("Missing modifier name for " + this, file);
		}
		if (attributeName == null) {
			throw new InvalidJsonException("Missing attribute name for " + this, file);
		}
		
		if (operation == null)
			throw new InvalidJsonException("Missing modifier operation for " + this, file);
		
		if (amount != null)
			amount.validate(file);
		else 
			throw new InvalidJsonException("Missing Amount for " + this, file);
		
		if (id == null)
			id = UUID.randomUUID();
		
		if (slot == null)
			Logger.debug("Missing Equipment Slot for " + this + ". Will now default to the item equipment slot");
	}
	
	public enum EnumOperation {
		@SerializedName("addition") ADDITION,
		@SerializedName("multiply_base") MULTIPLY_BASE, 
		@SerializedName("multiply_total") MULTIPLY_TOTAL
	}
}
