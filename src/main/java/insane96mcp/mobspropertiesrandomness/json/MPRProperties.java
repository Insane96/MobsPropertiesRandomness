package insane96mcp.mobspropertiesrandomness.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.mobs.MPRCreeper;
import insane96mcp.mobspropertiesrandomness.json.mobs.MPRGhast;
import insane96mcp.mobspropertiesrandomness.json.mobs.MPRPhantom;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRCustomName;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRModifiableValue;
import insane96mcp.mobspropertiesrandomness.json.utils.attribute.MPRMobAttribute;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class MPRProperties implements IMPRObject {
	@SerializedName("spawner_behaviour")
	public SpawnerBehaviour spawnerBehaviour;
	@SerializedName("structure_behaviour")
	public StructureBehaviour structureBehaviour;

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
		if (this.spawnerBehaviour == null)
			this.spawnerBehaviour = SpawnerBehaviour.NONE;

		if (this.structureBehaviour == null)
			this.structureBehaviour = StructureBehaviour.NONE;

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

	public void apply(MobEntity mobEntity, World world) {
		CompoundNBT tags = mobEntity.getPersistentData();
		boolean spawnedFromSpawner = tags.getBoolean(Strings.Tags.SPAWNED_FROM_SPAWNER);
		boolean spawnedFromStructure = tags.getBoolean(Strings.Tags.SPAWNED_FROM_STRUCTURE);

		if ((!spawnedFromSpawner && this.spawnerBehaviour == SpawnerBehaviour.SPAWNER_ONLY) || (spawnedFromSpawner && this.spawnerBehaviour == SpawnerBehaviour.NATURAL_ONLY))
			return;
		if ((!spawnedFromStructure && this.structureBehaviour == StructureBehaviour.STRUCTURE_ONLY) || (spawnedFromStructure && this.structureBehaviour == StructureBehaviour.NATURAL_ONLY))
			return;
		for (MPRPotionEffect potionEffect : this.potionEffects) {
			potionEffect.apply(mobEntity, world);
		}
		for (MPRMobAttribute attribute : this.attributes) {
			attribute.apply(mobEntity, world);
		}
		this.equipment.apply(mobEntity, world);

		if (this.customName != null)
			this.customName.applyCustomName(mobEntity, world);

		if (this.silent != null && world.rand.nextDouble() < this.silent.getValue(mobEntity, world))
			mobEntity.setSilent(true);

		if (this.creeper != null)
			this.creeper.apply(mobEntity, world);
		if (this.ghast != null)
			this.ghast.apply(mobEntity, world);
		if (this.phantom != null)
			this.phantom.apply(mobEntity, world);
	}

	public enum SpawnerBehaviour {
		NONE,
		SPAWNER_ONLY,
		NATURAL_ONLY
	}

	public enum StructureBehaviour {
		NONE,
		STRUCTURE_ONLY,
		NATURAL_ONLY
	}
}
