package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.weightedrandom.IWeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject_old;
import insane96mcp.mobspropertiesrandomness.data.json.properties.attribute.MPRItemAttribute;
import insane96mcp.mobspropertiesrandomness.data.json.properties.mods.tconstruct.MPRTiConMaterials;
import insane96mcp.mobspropertiesrandomness.data.json.properties.mods.tconstruct.MPRTiConModifier;
import insane96mcp.mobspropertiesrandomness.data.json.util.MPRWorldWhitelist;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class MPRItem implements IMPRObject_old, IWeightedRandom {

	public String id;
	@SerializedName("weight")
	private MPRModifiableValue modifiableWeight;

	//TODO Make a class for the _weight
	private transient int _weight;

	@SerializedName("drop_chance")
	public MPRModifiableValue dropChance;
	public List<MPREnchantment> enchantments;
	@SerializedName("ticon_modifiers")
	public List<MPRTiConModifier> ticonModifiers;
	@SerializedName("ticon_materials")
	public MPRTiConMaterials ticonMaterials;

	public List<MPRItemAttribute> attributes;
	public String nbt;
	private transient CompoundTag _nbt;

	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

	@Override
	public void validate() throws JsonValidationException {
		if (this.id == null)
			throw new JsonValidationException("Missing id. " + this);
		else if (ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.id)) == null)
			throw new JsonValidationException("Invalid id. " + this);

		if (this.modifiableWeight == null) {
			Logger.info("Weight value missing for %s, will default to 1", this);
			this.modifiableWeight = new MPRModifiableValue(1f);
		}
		this.modifiableWeight.validate();

		if (this.dropChance != null)
			this.dropChance.validate();

		if (this.enchantments != null)
			for (MPREnchantment enchantment : this.enchantments)
				enchantment.validate();

		if (this.ticonModifiers != null)
			for (MPRTiConModifier tiConModifier : this.ticonModifiers)
				tiConModifier.validate();

		if (this.ticonMaterials != null)
			this.ticonMaterials.validate();

		if (this.attributes != null)
			for (MPRItemAttribute itemAttribute : this.attributes)
				itemAttribute.validate();

		if (this.nbt != null) {
			try {
				this._nbt = TagParser.parseTag(this.nbt);
			}
			catch (CommandSyntaxException e) {
				throw new JsonValidationException("Invalid nbt for Item (%s): %s".formatted(e.getMessage(), this.nbt));
			}
		}

		if (this.worldWhitelist != null)
			this.worldWhitelist.validate();
	}

	/**
	 * Returns this MPRItem with the weight calculated based off the modifiers, or null if the world whitelist doesn't match
	 */
	@Nullable
	public MPRItem computeAndGet(LivingEntity entity, Level world) {
		if (this.worldWhitelist != null && !this.worldWhitelist.isWhitelisted(entity))
			return null;

		this._weight = (int) this.modifiableWeight.getValue(entity, world);

		return this;
	}

	public CompoundTag getNBT() {
		return this._nbt.copy();
	}

	@Override
	public String toString() {
		return String.format("Item{id: %s, weight: %s, drop_chance: %s, enchantments: %s, attributes: %s, world_whitelist: %s, nbt: %s}", this.id, this.modifiableWeight, this.dropChance, this.enchantments, this.attributes, this.worldWhitelist, this._nbt);
	}

	@Override
	public int getWeight() {
		return this._weight;
	}
}
