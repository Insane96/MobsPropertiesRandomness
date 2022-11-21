package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.setup.ILStrings;
import insane96mcp.mobspropertiesrandomness.data.json.mobspecificproperties.MPRCreeper;
import insane96mcp.mobspropertiesrandomness.data.json.mobspecificproperties.MPRGhast;
import insane96mcp.mobspropertiesrandomness.data.json.mobspecificproperties.MPRPhantom;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRCustomName;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRNbt;
import insane96mcp.mobspropertiesrandomness.data.json.properties.MPRPotionEffect;
import insane96mcp.mobspropertiesrandomness.data.json.properties.attribute.MPRMobAttribute;
import insane96mcp.mobspropertiesrandomness.data.json.properties.condition.MPRConditions;
import insane96mcp.mobspropertiesrandomness.data.json.properties.equipment.MPREquipment;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPREvents;
import insane96mcp.mobspropertiesrandomness.data.json.util.MPRWorldWhitelist;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public abstract class MPRProperties implements IMPRObject {

	public MPRConditions conditions;
	@SerializedName("world_whitelist")
	public MPRWorldWhitelist worldWhitelist;

	@SerializedName("potion_effects")
	public List<MPRPotionEffect> potionEffects;

	public List<MPRMobAttribute> attributes;

	public MPREquipment equipment;

	@SerializedName("events")
	public MPREvents mprEvents;

	@SerializedName("custom_name")
	public MPRCustomName customName;

	public MPRCreeper creeper;
	public MPRGhast ghast;
	public MPRPhantom phantom;

	public MPRModifiableValue silent;

	@SerializedName("experience_multiplier")
	public MPRModifiableValue experienceMultiplier;

	@SerializedName("loot_table")
	public String lootTable;

	public List<MPRNbt> nbt;

	@SerializedName("raw_nbt")
	public String rawNbt;
	public transient CompoundTag _rawNbt = null;

	@Override
	public void validate() throws JsonValidationException {
		if (this.conditions != null)
			this.conditions.validate();
		if (this.worldWhitelist != null)
			this.worldWhitelist.validate();

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

		if (this.mprEvents != null)
			this.mprEvents.validate();

		if (this.customName != null)
			this.customName.validate();

		if (this.silent != null)
			this.silent.validate();

		if (this.experienceMultiplier != null)
			this.experienceMultiplier.validate();

		//Mob specific validations
		if (this.creeper != null)
			this.creeper.validate();

		if (this.ghast != null)
			this.ghast.validate();

		if (this.phantom != null)
			this.phantom.validate();

		if (this.lootTable != null) {
			if (this.lootTable.equals(""))
				throw new JsonValidationException("\"loot_table\": \"\" is not valid. To use an empty loot_table use \"minecraft:empty\". " + this);
			else if (ResourceLocation.tryParse(this.lootTable) == null)
				throw new JsonValidationException("\"loot_table\": \"" + this.lootTable + "\" is not valid. You must use a valid Resource Location (modid:loot_table_id). " + this);
		}

		if (this.nbt == null)
			this.nbt = new ArrayList<>();
		for (MPRNbt mprNbt : this.nbt) {
			mprNbt.validate();
		}

		if (this.rawNbt != null) {
			try {
				this._rawNbt = TagParser.parseTag(this.rawNbt);
			}
			catch (CommandSyntaxException e) {
				throw new JsonValidationException("Invalid raw nbt in properties: " + this.rawNbt);
			}
		}
	}

	public boolean apply(LivingEntity livingEntity, Level level) {
		if (this.conditions != null && !this.conditions.conditionsApply(livingEntity))
			return false;
		if (this.worldWhitelist != null && !this.worldWhitelist.isWhitelisted(livingEntity))
			return false;
		for (MPRPotionEffect potionEffect : this.potionEffects) {
			potionEffect.apply(livingEntity, level);
		}
		for (MPRMobAttribute attribute : this.attributes) {
			attribute.apply(livingEntity, level);
		}
		this.equipment.apply(livingEntity, level);

		if (this.mprEvents != null)
			this.mprEvents.addToNBT(livingEntity);

		if (this.customName != null)
			this.customName.applyCustomName(livingEntity, level);

		if (this.silent != null && level.random.nextDouble() < this.silent.getValue(livingEntity, level))
			livingEntity.setSilent(true);

		if (this.experienceMultiplier != null)
			livingEntity.getPersistentData().putDouble(ILStrings.Tags.EXPERIENCE_MULTIPLIER, this.experienceMultiplier.getValue(livingEntity, level));

		if (this.creeper != null)
			this.creeper.apply(livingEntity, level);
		if (this.ghast != null)
			this.ghast.apply(livingEntity, level);
		if (this.phantom != null)
			this.phantom.apply(livingEntity, level);

		if (this.lootTable != null && livingEntity instanceof Mob) {
			((Mob) livingEntity).lootTable = new ResourceLocation(this.lootTable);
		}

		for (MPRNbt mprNbt : this.nbt) {
			mprNbt.apply(livingEntity, level);
		}

		if (this._rawNbt != null) {
			livingEntity.readAdditionalSaveData(this._rawNbt);
		}

		return true;
	}
}
