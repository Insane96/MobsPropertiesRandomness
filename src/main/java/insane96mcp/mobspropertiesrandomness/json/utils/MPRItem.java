package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.difficulty.MPRDifficulty;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MPRItem extends WeightedRandom.Item implements IMPRObject {

	public String id;
	private int weight;
	@SerializedName("weight_modifier")
	private MPRDifficulty weightModifier;
	@SerializedName("drop_chance")
	public float dropChance;
	public List<MPREnchantment> enchantments;
	public List<MPRItemAttribute> attributes;
	public String nbt;

	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

	public MPRItem(int itemWeightIn) {
		super(itemWeightIn);
	}

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (id == null)
			throw new InvalidJsonException("Missing id. " + this, file);
		else if (ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.id)) == null)
			throw new InvalidJsonException("Invalid id. " + this, file);

		if (weight <= 0)
			throw new InvalidJsonException("Missing weight (or weight <= 0). " + this, file);
		else
			itemWeight = weight;

		if (weightModifier == null)
			weightModifier = new MPRDifficulty();

		if (dropChance == 0f)
			Logger.info("Drop Chance has been set to 0 (or omitted). Mobs will drop this piece only with Looting. " + this);

		if (enchantments == null)
			enchantments = new ArrayList<>();

		if (!enchantments.isEmpty()) {
			for (MPREnchantment enchantment : enchantments) {
				enchantment.validate(file);
			}
		}

		if (attributes == null)
			attributes = new ArrayList<>();

		if (!attributes.isEmpty()) {
			for (MPRItemAttribute itemAttribute : attributes) {
				itemAttribute.validate(file);
			}
		}

		if (worldWhitelist != null)
			worldWhitelist.validate(file);
	}

	/**
	 * Returns an MPRItem with the weight modifier applied to the item's weight
	 * @param world
	 * @return
	 */
	public MPRItem getModifiedWeightItem(World world) {
		MPRItem item2 = this.copy();

		switch (world.getDifficulty()) {
			case EASY:
				item2.itemWeight += weightModifier.easy;
				break;

			case NORMAL:
				item2.itemWeight += weightModifier.normal;
				break;

			case HARD:
				item2.itemWeight += weightModifier.hard;
				break;

			default:
				break;
		}

		return item2;
	}

	/**
	 * Returns a copy of the JItem
	 * @return a copy of the JItem
	 */
	protected MPRItem copy() {
		MPRItem jsonItem = new MPRItem(this.itemWeight);
		jsonItem.attributes = this.attributes;
		jsonItem.dropChance = this.dropChance;
		jsonItem.enchantments = this.enchantments;
		jsonItem.id = this.id;
		jsonItem.nbt = this.nbt;
		jsonItem.weight = this.weight;
		jsonItem.weightModifier = this.weightModifier;
		return jsonItem;
	}

	@Override
	public String toString() {
		return String.format("Item{id: %s, weight: %d, weight_modifier: %s, drop_chance: %f, enchantments: %s, attributes: %s, world_whitelist: %s, nbt: %s}", id, weight, weightModifier, dropChance, enchantments, attributes, worldWhitelist, nbt);
	}
}
