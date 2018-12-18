package net.insane96mcp.mpr.json;

import java.io.File;

import com.google.gson.annotations.SerializedName;

import net.insane96mcp.mpr.exceptions.InvalidJsonException;
import net.insane96mcp.mpr.json.utils.Slot;

public class Equipment {

	public Slot head;
	public Slot chest;
	public Slot legs;
	public Slot feets;
	@SerializedName("main_hand")
	public Slot mainHand;
	@SerializedName("off_hand")
	public Slot offHand;
	
	@Override
	public String toString() {
		return String.format("Equipment{head: %s, chest: %s, legs: %s, feets: %s, mainHand: %s, offHand: %s}", head, chest, legs, feets, mainHand, offHand);
	}

	public void Validate(final File file) throws InvalidJsonException{
		if (head != null)
			head.Validate(file);
		if (chest != null)
			chest.Validate(file);
		if (legs != null)
			legs.Validate(file);
		if (feets != null)
			feets.Validate(file);
		if (mainHand != null)
			mainHand.Validate(file);
		if (offHand != null)
			offHand.Validate(file);
	}
}
