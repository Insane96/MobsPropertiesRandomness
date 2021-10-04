package insane96mcp.mobspropertiesrandomness.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRPresets;
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

import static insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener.MPR_MOBS;

public class MPRMob extends MPRProperties implements IMPRObject {
	@SerializedName("mob_id")
	public String mobId;
	public String group;

	public MPRPresets presets;

	@Override
	public void validate(File file) throws InvalidJsonException {
		super.validate(file);
		if (this.mobId == null && this.group == null)
			throw new InvalidJsonException("Missing mob_id or group. " + this, file);
		else if (this.mobId != null && this.group != null)
			Logger.info("mob_id and group are both present, mob_id will be ignored");

		if (this.mobId != null) {
			String[] splitId = this.mobId.split(":");
			if (splitId.length != 2)
				throw new InvalidJsonException("Invalid mob_id " + this.mobId, file);

			ResourceLocation resourceLocation = new ResourceLocation(this.mobId);
			if (!ForgeRegistries.ENTITIES.containsKey(resourceLocation) && !this.mobId.endsWith("*"))
				throw new InvalidJsonException("Mob with ID " + this.mobId + " does not exist", file);
		}

		if (this.presets != null)
			this.presets.validate(file);
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

			if (mprMob.customName != null)
				mprMob.customName.applyCustomName(mobEntity, world);

			if (mprMob.creeper != null)
				mprMob.creeper.apply(mobEntity, world);
			if (mprMob.ghast != null)
				mprMob.ghast.apply(mobEntity, world);
			if (mprMob.phantom != null)
				mprMob.phantom.apply(mobEntity, world);

			if (mprMob.presets != null)
				mprMob.presets.apply(mobEntity, world);
		}

		tags.putBoolean(Strings.Tags.PROCESSED, true);
	}

	@Override
	public String toString() {
		return String.format("Mob{id: %s, group: %s, spawner_behaviour: %s, structure_behaviour: %s, potion_effects: %s, attributes: %s, equipment: %s, creeper: %s, ghast: %s}", mobId, group, spawnerBehaviour, structureBehaviour, potionEffects, attributes, equipment, creeper, ghast);
	}
}
