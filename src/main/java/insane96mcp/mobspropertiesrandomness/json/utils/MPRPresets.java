package insane96mcp.mobspropertiesrandomness.json.utils;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.MPRPotionEffect;
import insane96mcp.mobspropertiesrandomness.json.MPRPreset;
import insane96mcp.mobspropertiesrandomness.json.MPRProperties;
import insane96mcp.mobspropertiesrandomness.json.utils.attribute.MPRMobAttribute;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener.MPR_PRESETS;

public class MPRPresets implements IMPRObject {

	public MPRModifiableValue chance;
	public List<MPRWeightedPreset> list;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (this.chance != null)
			this.chance.validate(file);
		if (list == null)
			throw new InvalidJsonException("Missing list in Presets. " + this, file);
	}

	public void apply(MobEntity entity, World world) {
		if (this.chance != null && world.rand.nextDouble() >= this.chance.getValue(entity, world))
			return;

		MPRWeightedPreset weightedPreset = this.getRandomPreset(entity, world);
		if (weightedPreset == null)
			return;
		for (MPRPreset preset : MPR_PRESETS) {
			if (!preset.name.equals(weightedPreset.name))
				continue;

			CompoundNBT tags = entity.getPersistentData();
			boolean spawnedFromSpawner = tags.getBoolean(Strings.Tags.SPAWNED_FROM_SPAWNER);
			boolean spawnedFromStructure = tags.getBoolean(Strings.Tags.SPAWNED_FROM_STRUCTURE);

			if ((!spawnedFromSpawner && preset.spawnerBehaviour == MPRProperties.SpawnerBehaviour.SPAWNER_ONLY) || (spawnedFromSpawner && preset.spawnerBehaviour == MPRProperties.SpawnerBehaviour.NATURAL_ONLY))
				continue;
			if ((!spawnedFromStructure && preset.structureBehaviour == MPRProperties.StructureBehaviour.STRUCTURE_ONLY) || (spawnedFromStructure && preset.structureBehaviour == MPRProperties.StructureBehaviour.NATURAL_ONLY))
				continue;
			for (MPRPotionEffect potionEffect : preset.potionEffects) {
				potionEffect.apply(entity, world);
			}
			for (MPRMobAttribute attribute : preset.attributes) {
				attribute.apply(entity, world);
			}
			preset.equipment.apply(entity, world);

			if (preset.customName != null)
				preset.customName.applyCustomName(entity, world);

			if (preset.creeper != null)
				preset.creeper.apply(entity, world);
			if (preset.ghast != null)
				preset.ghast.apply(entity, world);
			if (preset.phantom != null)
				preset.phantom.apply(entity, world);
		}
	}

	private List<MPRWeightedPreset> getPresets(MobEntity entity, World world){
		ArrayList<MPRWeightedPreset> weightedPresets = new ArrayList<>();
		for (MPRWeightedPreset weightedPreset : this.list) {
			MPRWeightedPreset mprWeightedPreset = weightedPreset.getPresetWithModifiedWeight(entity, world);
			if (mprWeightedPreset != null)
				weightedPresets.add(mprWeightedPreset);
		}
		return weightedPresets;
	}

	/**
	 * Returns a random item from the pool based of weights, dimensions whitelist and biomes whitelist
	 * @param entity
	 * @param world
	 * @return a WeightedPreset or null if no items were available
	 */
	@Nullable
	public MPRWeightedPreset getRandomPreset(MobEntity entity, World world) {
		List<MPRWeightedPreset> items = getPresets(entity, world);
		if (items.isEmpty())
			return null;
		return WeightedRandom.getRandomItem(world.rand, items);
	}

	@Override
	public String toString() {
		return String.format("Presets{chance: %s, list: %s}", chance, list);
	}
}