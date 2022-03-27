package insane96mcp.mobspropertiesrandomness.json.util;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.util.attribute.MPRItemAttribute;
import insane96mcp.mobspropertiesrandomness.util.weightedrandom.IWeightedRandom;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

public class MPRItem implements IMPRObject, IWeightedRandom {

	public String id;
	@SerializedName("weight")
	private MPRModifiableValue modifiableWeight;

	//TODO Make a class for the _weight
	private transient int _weight;

	@SerializedName("drop_chance")
	public MPRModifiableValue dropChance;
	public List<MPREnchantment> enchantments;
	public List<MPRItemAttribute> attributes;
	public String nbt;
	private transient CompoundTag _nbt;

	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (id == null)
			throw new InvalidJsonException("Missing id. " + this, file);
		else if (ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.id)) == null)
			throw new InvalidJsonException("Invalid id. " + this, file);

		if (modifiableWeight == null)
			throw new InvalidJsonException("Missing weight. " + this, file);
		modifiableWeight.validate(file);

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
				this._nbt = TagParser.parseTag(this.nbt);
			}
			catch (CommandSyntaxException e) {
				throw new InvalidJsonException("Invalid nbt for Item: " + this.nbt, file);
			}
		}

		if (worldWhitelist != null)
			worldWhitelist.validate(file);
	}

	/**
	 * Returns this MPRItem with the weight calculated based off the modifiers, or null if the world whitelist doesn't match
	 */
	@Nullable
	public MPRItem computeAndGet(LivingEntity entity, Level world) {
		if (worldWhitelist != null && !worldWhitelist.isWhitelisted(entity))
			return null;

		this._weight = (int) this.modifiableWeight.getValue(entity, world);

		return this;
	}

	public CompoundTag getNBT() {
		return this._nbt;
	}

	@Override
	public String toString() {
		return String.format("Item{id: %s, weight: %s, drop_chance: %s, enchantments: %s, attributes: %s, world_whitelist: %s, nbt: %s}", id, modifiableWeight, dropChance, enchantments, attributes, worldWhitelist, _nbt);
	}

	@Override
	public int getWeight() {
		return this._weight;
	}
}
