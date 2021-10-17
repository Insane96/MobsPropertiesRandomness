package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.attribute.MPRItemAttribute;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

public class MPRItem extends WeightedRandom.Item implements IMPRObject {

	public String id;
	private MPRModifiableValue weight;
	@SerializedName("drop_chance")
	public MPRModifiableValue dropChance;
	public List<MPREnchantment> enchantments;
	public List<MPRItemAttribute> attributes;
	public String nbt;
	private transient CompoundNBT _nbt;

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

		if (weight == null)
			throw new InvalidJsonException("Missing weight. " + this, file);
		weight.validate(file);

		if (dropChance != null)
			dropChance.validate(file);

		if (enchantments != null)
			for (MPREnchantment enchantment : enchantments)
				enchantment.validate(file);

		if (attributes != null)
			for (MPRItemAttribute itemAttribute : attributes)
				itemAttribute.validate(file);

		if (this.nbt != null) {
			try {
				this._nbt = JsonToNBT.getTagFromJson(this.nbt);
			}
			catch (CommandSyntaxException e) {
				throw new InvalidJsonException("Invalid nbt for Item: " + this.nbt, file);
			}
		}

		if (worldWhitelist != null)
			worldWhitelist.validate(file);
	}

	/**
	 * Returns an MPRItem with the weight modifier applied to the item's weight. Returns null if the entity doesn't fulfill the world whitelist
	 * @param world
	 * @return
	 */
	@Nullable
	public MPRItem getItemWithModifiedWeight(MobEntity entity, World world) {
		if (worldWhitelist != null && !worldWhitelist.isWhitelisted(entity))
			return null;

		MPRItem item2 = this.copy();

		item2.itemWeight = (int) this.weight.getValue(entity, world);

		return item2;
	}

	public CompoundNBT getNBT() {
		return this._nbt;
	}

	protected MPRItem copy() {
		MPRItem mprItem = new MPRItem(this.itemWeight);
		mprItem.attributes = this.attributes;
		mprItem.dropChance = this.dropChance;
		mprItem.enchantments = this.enchantments;
		mprItem.id = this.id;
		mprItem.nbt = this.nbt;
		mprItem._nbt = this._nbt;
		mprItem.weight = this.weight;
		return mprItem;
	}

	@Override
	public String toString() {
		return String.format("Item{id: %s, weight: %s, drop_chance: %s, enchantments: %s, attributes: %s, world_whitelist: %s, nbt: %s}", id, weight, dropChance, enchantments, attributes, worldWhitelist, _nbt);
	}
}
