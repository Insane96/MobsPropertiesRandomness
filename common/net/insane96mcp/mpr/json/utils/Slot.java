package net.insane96mcp.mpr.json.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mpr.exceptions.InvalidJsonException;

public class Slot {

	@SerializedName("override_vanilla")
	public boolean overrideVanilla;
	public Chance chance;
	public List<Item> items = new ArrayList<Item>();
	
	@Override
	public String toString() {
		return String.format("Slot{chance: %s, items: %s}", chance, items);
	}

	public void Validate(final File file) throws InvalidJsonException{
		if (chance != null)
			chance.Validate(file);
		else
			throw new InvalidJsonException("Missing Chance for " + this, file);
		
		if (items.isEmpty())
			throw new InvalidJsonException("There's no item set in the slot " + this, file);
		else {
			for (Item item : items) {
				item.Validate(file);
			}
		}
	}
}
