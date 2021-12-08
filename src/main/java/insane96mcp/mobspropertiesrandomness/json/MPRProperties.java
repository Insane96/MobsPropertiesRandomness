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
import net.minecraft.entity.MobEntity;
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

	@SerializedName("custom_name")
	public MPRCustomName customName;

	public MPRCreeper creeper;
	public MPRGhast ghast;
	public MPRPhantom phantom;

	public MPRModifiableValue silent;

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

		if (this.customName != null)
			this.customName.validate(file);

		if (this.silent != null)
			this.silent.validate(file);

		//Mob specific validations
		if (this.creeper != null)
			this.creeper.validate(file);

		if (this.ghast != null)
			this.ghast.validate(file);

		if (this.phantom != null)
			this.phantom.validate(file);
	}

	public boolean apply(MobEntity mobEntity, World world) {
		if (this.conditions != null && !this.conditions.conditionsApply(mobEntity))
			return false;
		for (MPRPotionEffect potionEffect : this.potionEffects) {
			potionEffect.apply(mobEntity, world);
		}
		for (MPRMobAttribute attribute : this.attributes) {
			attribute.apply(mobEntity, world);
		}
		this.equipment.apply(mobEntity, world);

		if (this.customName != null)
			this.customName.applyCustomName(mobEntity, world);

		if (this.silent != null && world.random.nextDouble() < this.silent.getValue(mobEntity, world))
			mobEntity.setSilent(true);

		if (this.creeper != null)
			this.creeper.apply(mobEntity, world);
		if (this.ghast != null)
			this.ghast.apply(mobEntity, world);
		if (this.phantom != null)
			this.phantom.apply(mobEntity, world);

		return true;
	}
}