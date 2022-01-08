package insane96mcp.mobspropertiesrandomness.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.mobs.MPRCreeper;
import insane96mcp.mobspropertiesrandomness.json.mobs.MPRGhast;
import insane96mcp.mobspropertiesrandomness.json.mobs.MPRPhantom;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRConditions;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRCustomName;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.json.utils.attribute.MPRMobAttribute;
import insane96mcp.mobspropertiesrandomness.json.utils.onhit.MPROnHitEffects;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class MPRProperties implements IMPRObject {

	public MPRConditions conditions;

	@SerializedName("potion_effects")
	public List<MPRPotionEffect> potionEffects;

	public List<MPRMobAttribute> attributes;

	public MPREquipment equipment;

	@SerializedName("on_hit_effects")
	public MPROnHitEffects onHitEffects;

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

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (this.conditions != null)
			this.conditions.validate(file);

		if (this.potionEffects == null)
			this.potionEffects = new ArrayList<>();
		for (MPRPotionEffect potionEffect : this.potionEffects) {
			potionEffect.validate(file);
		}

		if (this.attributes == null)
			this.attributes = new ArrayList<>();
		for (MPRMobAttribute attribute : this.attributes) {
			attribute.validate(file);
		}

		if (this.equipment == null)
			this.equipment = new MPREquipment();
		this.equipment.validate(file);

		if (this.onHitEffects != null)
			this.onHitEffects.validate(file);

		if (this.customName != null)
			this.customName.validate(file);

		if (this.silent != null)
			this.silent.validate(file);

		if (this.experienceMultiplier != null)
			this.experienceMultiplier.validate(file);

		//Mob specific validations
		if (this.creeper != null)
			this.creeper.validate(file);

		if (this.ghast != null)
			this.ghast.validate(file);

		if (this.phantom != null)
			this.phantom.validate(file);

		if (this.lootTable != null) {
			if (this.lootTable.equals(""))
				throw new InvalidJsonException("\"loot_table\": \"\" is not valid. To use an empty loot_table use \"minecraft:empty\". " + this, file);
			else if (ResourceLocation.tryParse(this.lootTable) == null)
				throw new InvalidJsonException("\"loot_table\": \"" + this.lootTable + "\" is not valid. You must use a valid Resource Location (modid:loot_table_id). " + this, file);
		}
	}

	public boolean apply(LivingEntity livingEntity, World world) {
		if (this.conditions != null && !this.conditions.conditionsApply(livingEntity))
			return false;
		for (MPRPotionEffect potionEffect : this.potionEffects) {
			potionEffect.apply(livingEntity, world);
		}
		for (MPRMobAttribute attribute : this.attributes) {
			attribute.apply(livingEntity, world);
		}
		this.equipment.apply(livingEntity, world);

		if (this.onHitEffects != null)
			this.onHitEffects.addToNBT(livingEntity);

		if (this.customName != null)
			this.customName.applyCustomName(livingEntity, world);

		if (this.silent != null && world.random.nextDouble() < this.silent.getValue(livingEntity, world))
			livingEntity.setSilent(true);

		if (this.experienceMultiplier != null)
			livingEntity.getPersistentData().putDouble(Strings.Tags.EXPERIENCE_MULTIPLIER, this.experienceMultiplier.getValue(livingEntity, world));

		if (this.creeper != null)
			this.creeper.apply(livingEntity, world);
		if (this.ghast != null)
			this.ghast.apply(livingEntity, world);
		if (this.phantom != null)
			this.phantom.apply(livingEntity, world);

		if (this.lootTable != null && livingEntity instanceof MobEntity) {
			((MobEntity) livingEntity).lootTable = new ResourceLocation(this.lootTable);
		}

		return true;
	}
}
