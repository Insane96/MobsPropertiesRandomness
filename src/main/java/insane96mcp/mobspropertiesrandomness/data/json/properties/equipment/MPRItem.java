package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.weightedrandom.IWeightedRandom;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.properties.attribute.MPRItemAttribute;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class MPRItem implements IMPRObject, IWeightedRandom {

	public String id;
	private transient Item item;
	public MPRRange count;
	@SerializedName("weight")
	private MPRModifiableValue modifiableWeight;

	private transient int _weight;

	@SerializedName("drop_chance")
	public MPRModifiableValue dropChance;
	public List<MPREnchantment> enchantments;
	//@SerializedName("ticon_modifiers")
	//public List<MPRTiConModifier> ticonModifiers;
	//@SerializedName("ticon_materials")
	//public MPRTiConMaterials ticonMaterials;

	public List<MPRItemAttribute> attributes;
	public String nbt;
	private transient CompoundTag _nbt;

	private transient boolean valid = true;

	@Override
	public void validate() throws JsonValidationException {
		if (this.id == null)
			throw new JsonValidationException("Missing id. %s".formatted(this));

		this.item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(this.id));
		if (this.item == Items.AIR && !this.id.equals("minecraft:air")) {
			Logger.warn("Item not found %s: %s".formatted(this.id, this));
			this.valid = false;
			return;
		}

		if (this.count == null)
			this.count = new MPRRange(1f);

		if (this.modifiableWeight == null) {
			Logger.debug("Weight value missing for %s, will default to 1", this);
			this.modifiableWeight = new MPRModifiableValue(1f);
		}
		else if (this.modifiableWeight.getValue() < 1) {
			throw new JsonValidationException("Invalid weight for Item. Must be > 0. " + this);
		}
		this.modifiableWeight.validate();

		if (this.dropChance != null)
			this.dropChance.validate();

		if (this.enchantments != null)
			for (MPREnchantment enchantment : this.enchantments)
				enchantment.validate();

		/*if (this.ticonModifiers != null)
			for (MPRTiConModifier tiConModifier : this.ticonModifiers)
				tiConModifier.validate();

		if (this.ticonMaterials != null)
			this.ticonMaterials.validate();*/

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
	}

	/**
	 * Returns this MPRItem with the weight calculated based off the modifiers, or null if the world whitelist doesn't match
	 */
	@Nullable
	public MPRItem computeAndGet(LivingEntity entity) {
		this._weight = (int) this.modifiableWeight.getValue(entity);

		return this;
	}

	public CompoundTag getNBT() {
		return this._nbt.copy();
	}

	public Item getItem() {
		return this.item;
	}

	public boolean isValid() {
		return this.valid;
	}

	@Override
	public int getWeight() {
		return this._weight;
	}
}
