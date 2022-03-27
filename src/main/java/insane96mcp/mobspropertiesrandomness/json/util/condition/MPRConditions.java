package insane96mcp.mobspropertiesrandomness.json.util.condition;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.setup.ILStrings;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class MPRConditions implements IMPRObject {

	@SerializedName("is_baby")
	public Boolean isBaby;
	@SerializedName("spawner_behaviour")
	public SpawnerBehaviour spawnerBehaviour;
	@SerializedName("structure_behaviour")
	public StructureBehaviour structureBehaviour;
	@SerializedName("advancements_done")
	public List<MPRAdvancement> advancements;
	public String nbt;
	public transient CompoundTag _nbt;

	@Override
	public void validate() throws JsonValidationException {
		if (this.spawnerBehaviour == null)
			this.spawnerBehaviour = SpawnerBehaviour.NONE;

		if (this.structureBehaviour == null)
			this.structureBehaviour = StructureBehaviour.NONE;

		if (this.nbt != null) {
			try {
				this._nbt = TagParser.parseTag(this.nbt);
			}
			catch (CommandSyntaxException e) {
				throw new JsonValidationException("Invalid nbt for Conditions: " + this.nbt);
			}
		}

		if (this.advancements != null) {
			for (MPRAdvancement advancement : this.advancements) {
				advancement.validate();
			}
		}
	}

	public boolean conditionsApply(LivingEntity livingEntity) {
		boolean result = true;
		if (isBaby != null)
			result = (isBaby && livingEntity.isBaby()) || (!isBaby && !livingEntity.isBaby());

		if (nbt != null) {
			CompoundTag mobNBT = new CompoundTag();
			livingEntity.addAdditionalSaveData(mobNBT);
			mobNBT.put("ForgeData", livingEntity.getPersistentData());
			result = MCUtils.compareNBT(this._nbt, mobNBT);
		}

		CompoundTag mobPersistentData = livingEntity.getPersistentData();
		boolean spawnedFromSpawner = mobPersistentData.getBoolean(ILStrings.Tags.SPAWNED_FROM_SPAWNER);
		boolean spawnedFromStructure = mobPersistentData.getBoolean(ILStrings.Tags.SPAWNED_FROM_STRUCTURE);
		if ((!spawnedFromSpawner && this.spawnerBehaviour == SpawnerBehaviour.SPAWNER_ONLY) || (spawnedFromSpawner && this.spawnerBehaviour == SpawnerBehaviour.NATURAL_ONLY))
			result = false;
		if ((!spawnedFromStructure && this.structureBehaviour == StructureBehaviour.STRUCTURE_ONLY) || (spawnedFromStructure && this.structureBehaviour == StructureBehaviour.NATURAL_ONLY))
			result = false;

		boolean advancementCondition = false;
		for (MPRAdvancement advancement : this.advancements) {
			if (advancement.conditionApplies(livingEntity)) {
				advancementCondition = true;
				break;
			}
		}
		if (!advancementCondition)
			result = false;

		return result;
	}

	@Override
	public String toString() {
		return String.format("Conditions{is_baby: %s, nbt: %s, spawner_behaviour: %s, structure_behaviour: %s, advancements: %s}", isBaby, nbt, spawnerBehaviour, structureBehaviour, advancements);
	}

	public enum SpawnerBehaviour {
		@SerializedName("none")
		NONE,
		@SerializedName("spawner_only")
		SPAWNER_ONLY,
		@SerializedName("natural_only")
		NATURAL_ONLY
	}

	public enum StructureBehaviour {
		@SerializedName("none")
		NONE,
		@SerializedName("structure_only")
		STRUCTURE_ONLY,
		@SerializedName("natural_only")
		NATURAL_ONLY
	}
}
