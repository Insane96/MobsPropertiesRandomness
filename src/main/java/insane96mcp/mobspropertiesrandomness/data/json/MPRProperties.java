package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.setup.ILStrings;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRCustomName;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRNbt;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRPotionEffect;
import insane96mcp.mobspropertiesrandomness.data.json.properties.attribute.MPRMobAttribute;
import insane96mcp.mobspropertiesrandomness.data.json.properties.condition.MPRConditions;
import insane96mcp.mobspropertiesrandomness.data.json.properties.equipment.MPREquipment;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPREvents;
import insane96mcp.mobspropertiesrandomness.data.json.properties.mods.pehuki.MPRScalePehkui;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public abstract class MPRProperties implements IMPRObject {

	public MPRConditions conditions;

	@SerializedName("potion_effects")
	public List<MPRPotionEffect> potionEffects;

	public List<MPRMobAttribute> attributes;

	public MPREquipment equipment;

	@SerializedName("events")
	public MPREvents events;

	@SerializedName("custom_name")
	public MPRCustomName customName;

	public MPRModifiableValue silent;

	@SerializedName("experience_multiplier")
	public MPRModifiableValue experienceMultiplier;

	@SerializedName("loot_table")
	public String lootTable;

	@SerializedName("effect_immunity")
	public List<String> effectImmunity;

	@SerializedName("set_nbt")
	public List<MPRNbt> setNbt;

	@SerializedName("set_raw_nbt")
	public String setRawNbt;
	public transient CompoundTag _rawNbt = null;

	@SerializedName("scale_pehkui")
	public List<MPRScalePehkui> scalePehkui;

	@Override
	public void validate() throws JsonValidationException {
		if (this.conditions != null)
			this.conditions.validate();

		if (this.potionEffects == null)
			this.potionEffects = new ArrayList<>();
		for (MPRPotionEffect potionEffect : this.potionEffects) {
			potionEffect.validate();
		}

		if (this.attributes == null)
			this.attributes = new ArrayList<>();
		for (MPRMobAttribute attribute : this.attributes) {
			attribute.validate();
		}

		if (this.equipment == null)
			this.equipment = new MPREquipment();
		this.equipment.validate();

		if (this.events != null)
			this.events.validate();

		if (this.customName != null)
			this.customName.validate();

		if (this.silent != null)
			this.silent.validate();

		if (this.experienceMultiplier != null)
			this.experienceMultiplier.validate();

		if (this.lootTable != null) {
			if (this.lootTable.equals(""))
				throw new JsonValidationException("\"loot_table\": \"\" is not valid. To use an empty loot_table use \"minecraft:empty\". " + this);
			else if (ResourceLocation.tryParse(this.lootTable) == null)
				throw new JsonValidationException("\"loot_table\": \"" + this.lootTable + "\" is not valid. You must use a valid Resource Location (namespace:loot_table_id). " + this);
		}

		if (this.effectImmunity != null) {
			for (String mobEffect : this.effectImmunity) {
				if (ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(mobEffect)) == null) {
					throw new JsonValidationException("Invalid MobEffect ID " + mobEffect + " for " + this);
				}
			}
		}

		if (this.setNbt == null)
			this.setNbt = new ArrayList<>();
		for (MPRNbt mprNbt : this.setNbt) {
			mprNbt.validate();
		}

		if (this.setRawNbt != null) {
			try {
				this._rawNbt = TagParser.parseTag(this.setRawNbt);
			}
			catch (CommandSyntaxException e) {
				throw new JsonValidationException("Invalid raw nbt in properties: " + this.setRawNbt);
			}
		}

		if (this.scalePehkui != null)
		{
			for (MPRScalePehkui scalePehkui1 : this.scalePehkui) {
				scalePehkui1.validate();
			}
		}
	}

	public boolean apply(LivingEntity entity) {
		if (this.conditions != null && !this.conditions.conditionsApply(entity))
			return false;
		for (MPRPotionEffect potionEffect : this.potionEffects) {
			potionEffect.apply(entity);
		}
		for (MPRMobAttribute attribute : this.attributes) {
			attribute.apply(entity);
		}
		this.equipment.apply(entity);

		if (this.events != null)
			this.events.addToNBT(entity);

		if (this.customName != null)
			this.customName.applyCustomName(entity);

		if (this.silent != null && entity.level().random.nextDouble() < this.silent.getValue(entity))
			entity.setSilent(true);

		if (this.experienceMultiplier != null)
			entity.getPersistentData().putDouble(ILStrings.Tags.EXPERIENCE_MULTIPLIER, this.experienceMultiplier.getValue(entity));

		if (this.lootTable != null && entity instanceof Mob) {
			((Mob) entity).lootTable = new ResourceLocation(this.lootTable);
		}

		if (this.effectImmunity != null) {
			ListTag listTag = new ListTag();
			entity.getPersistentData().put(ILStrings.Tags.EXPERIENCE_MULTIPLIER, );
		}

		for (MPRNbt mprNbt : this.setNbt) {
			mprNbt.apply(entity);
		}

		if (this._rawNbt != null) {
			entity.readAdditionalSaveData(this._rawNbt);
		}

		if (this.scalePehkui != null) {
			for (MPRScalePehkui scalePehkui1 : this.scalePehkui) {
				scalePehkui1.apply(entity);
			}
		}

		return true;
	}
}
