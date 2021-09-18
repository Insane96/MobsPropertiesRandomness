package insane96mcp.mobspropertiesrandomness.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.data.MPRGroupReloadListener;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.mobs.MPRCreeper;
import insane96mcp.mobspropertiesrandomness.json.mobs.MPRGhast;
import insane96mcp.mobspropertiesrandomness.json.mobs.MPRPhantom;
import insane96mcp.mobspropertiesrandomness.json.utils.attribute.MPRMobAttribute;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import insane96mcp.mobspropertiesrandomness.utils.Logger;
import insane96mcp.mobspropertiesrandomness.utils.MPRUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener.MPR_MOBS;

public class MPRMob implements IMPRObject {
	@SerializedName("mob_id")
	public String mobId;
	public String group;

	@SerializedName("spawner_behaviour")
	public SpawnerBehaviour spawnerBehaviour;
	@SerializedName("structure_behaviour")
	public StructureBehaviour structureBehaviour;

	@SerializedName("potion_effects")
	public List<MPRPotionEffect> potionEffects;

	public List<MPRMobAttribute> attributes;

	public MPREquipment equipment;

	public MPRCreeper creeper;
	public MPRGhast ghast;
	public MPRPhantom phantom;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (mobId == null && group == null)
			throw new InvalidJsonException("Missing mob_id or group. " + this, file);
		else if (mobId != null && group != null)
			Logger.info("mob_id and group are both present, mob_id will be ignored");

		if (spawnerBehaviour == null)
			spawnerBehaviour = SpawnerBehaviour.NONE;

		if (mobId != null) {
			String[] splitId = mobId.split(":");
			if (splitId.length != 2)
				throw new InvalidJsonException("Invalid mob_id " + mobId, file);

			ResourceLocation resourceLocation = new ResourceLocation(mobId);
			if (!ForgeRegistries.ENTITIES.containsKey(resourceLocation) && !mobId.endsWith("*"))
				throw new InvalidJsonException("Mob with ID " + mobId + " does not exist", file);
		}

		if (group != null) {
			if (!MPRGroupReloadListener.INSTANCE.doesGroupExist(group))
				throw new InvalidJsonException("Group " + group + " does not exist", file);
		}

		if (potionEffects == null)
			potionEffects = new ArrayList<>();
		for (MPRPotionEffect potionEffect : potionEffects) {
			potionEffect.validate(file);
		}

		if (attributes == null)
			attributes = new ArrayList<>();
		for (MPRMobAttribute attribute : attributes) {
			attribute.validate(file);
		}

		if (equipment == null)
			equipment = new MPREquipment();
		equipment.validate(file);

		//Mob specific validations
		if (creeper != null)
			creeper.validate(file);

		if (ghast != null)
			ghast.validate(file);

		if (phantom != null)
			phantom.validate(file);
	}

	public static void apply(EntityJoinWorldEvent event) {
		if (MPR_MOBS.isEmpty())
			return;

		Entity entity = event.getEntity();
		World world = event.getWorld();

		if (!(entity instanceof MobEntity))
			return;

		MobEntity mobEntity = (MobEntity) entity;

		CompoundNBT tags = mobEntity.getPersistentData();
		boolean isAlreadyChecked = tags.getBoolean(Strings.Tags.PROCESSED);
		if (isAlreadyChecked)
			return;

		boolean spawnedFromSpawner = tags.getBoolean(Strings.Tags.SPAWNED_FROM_SPAWNER);
		boolean spawnedFromStructure = tags.getBoolean(Strings.Tags.SPAWNED_FROM_STRUCTURE);

		for (MPRMob mprMob : MPR_MOBS) {
			if (!MPRUtils.matchesEntity(mobEntity, mprMob))
				continue;
			if ((!spawnedFromSpawner && mprMob.spawnerBehaviour == SpawnerBehaviour.SPAWNER_ONLY) || (spawnedFromSpawner && mprMob.spawnerBehaviour == SpawnerBehaviour.NATURAL_ONLY))
				continue;
			if ((!spawnedFromStructure && mprMob.structureBehaviour == StructureBehaviour.STRUCTURE_ONLY) || (spawnedFromStructure && mprMob.structureBehaviour == StructureBehaviour.NATURAL_ONLY))
				continue;
			for (MPRPotionEffect potionEffect : mprMob.potionEffects) {
				potionEffect.apply(mobEntity, world);
			}
			for (MPRMobAttribute attribute : mprMob.attributes) {
				attribute.apply(mobEntity, world);
			}
			mprMob.equipment.apply(mobEntity, world);

			if (mprMob.creeper != null)
				mprMob.creeper.apply(mobEntity, world);
			if (mprMob.ghast != null)
				mprMob.ghast.apply(mobEntity, world);
			if (mprMob.phantom != null)
				mprMob.phantom.apply(mobEntity, world);
		}

		tags.putBoolean(Strings.Tags.PROCESSED, true);
	}

	@Override
	public String toString() {
		return String.format("Mob{id: %s, group: %s, spawner_behaviour: %s, structure_behaviour: %s, potion_effects: %s, attributes: %s, equipment: %s, creeper: %s, ghast: %s}", mobId, group, spawnerBehaviour, structureBehaviour, potionEffects, attributes, equipment, creeper, ghast);
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
