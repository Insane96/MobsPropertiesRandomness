package net.insane96mcp.mpr.json.utils;

import java.io.File;
import java.util.UUID;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.insane96mcp.mpr.lib.Logger;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ItemAttribute {
	
	public String modifier;
	@SerializedName("attribute_name")
	public String attributeName;
	public EnumOperation operation;
	public RangeMinMax amount;
	public UUID id;
	public EntityEquipmentSlot slot;
	
	@Override
	public String toString() {
		return String.format("ItemAttribute{modifier: %s, attributeName: %s, operation: %s, amount: %s, id: %s, slot: %s}", modifier, attributeName, operation, amount, id, slot);
	}
	
	public void Validate(final File file) throws InvalidJsonException {
		if (modifier == null) {
			throw new InvalidJsonException("Missing modifier name for " + this, file);
		}
		if (attributeName == null) {
			throw new InvalidJsonException("Missing attribute name for " + this, file);
		}
		
		if (operation == null)
			throw new InvalidJsonException("Missing modifier operation for " + this, file);
		
		if (amount != null)
			amount.Validate(file);
		else 
			throw new InvalidJsonException("Missing Amount for " + this, file);
		
		if (id == null)
			id = UUID.randomUUID();
		
		if (slot == null)
			Logger.Debug("Missing Equipment Slot for " + this + ". Will now default to the item equipment slot");
	}
	
	public enum EnumOperation {
		@SerializedName("addition") ADDITION,
		@SerializedName("multiply_base") MULTIPLY_BASE, 
		@SerializedName("multiply_total") MULTIPLY_TOTAL
	}
}
