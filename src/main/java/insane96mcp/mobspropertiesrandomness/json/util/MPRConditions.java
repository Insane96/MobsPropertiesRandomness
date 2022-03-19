package insane96mcp.mobspropertiesrandomness.json.util;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.util.condition.MPRAdvancement;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import insane96mcp.mobspropertiesrandomness.util.MPRUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;

import java.io.File;
import java.util.List;

public class MPRConditions implements IMPRObject {

	@SerializedName("is_baby")
	public Boolean isBaby;
	@SerializedName("spawner_behaviour")
	public SpawnerBehaviour spawnerBehaviour;
	@SerializedName("structure_behaviour")
	public StructureBehaviour structureBehaviour;
	public List<MPRAdvancement> advancements;
	public String nbt;
	public transient CompoundNBT _nbt;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (this.spawnerBehaviour == null)
			this.spawnerBehaviour = SpawnerBehaviour.NONE;

		if (this.structureBehaviour == null)
			this.structureBehaviour = StructureBehaviour.NONE;

		if (this.nbt != null) {
			try {
				this._nbt = JsonToNBT.parseTag(this.nbt);
			}
			catch (CommandSyntaxException e) {
				throw new InvalidJsonException("Invalid nbt for Conditions: " + this.nbt, file);
			}
		}

		if (this.advancements != null) {
			for (MPRAdvancement advancement : this.advancements) {
				advancement.validate(file);
			}
		}
	}

	public boolean conditionsApply(LivingEntity livingEntity) {
		boolean result = true;
		if (isBaby != null)
			result = (isBaby && livingEntity.isBaby()) || (!isBaby && !livingEntity.isBaby());

		if (nbt != null) {
			CompoundNBT mobNBT = new CompoundNBT();
			livingEntity.addAdditionalSaveData(mobNBT);
			mobNBT.put("ForgeData", livingEntity.getPersistentData());
			result = MPRUtils.compareNBT(this._nbt, mobNBT);
		}

		CompoundNBT mobPersistentData = livingEntity.getPersistentData();
		boolean spawnedFromSpawner = mobPersistentData.getBoolean(Strings.Tags.SPAWNED_FROM_SPAWNER);
		boolean spawnedFromStructure = mobPersistentData.getBoolean(Strings.Tags.SPAWNED_FROM_STRUCTURE);
		if ((!spawnedFromSpawner && this.spawnerBehaviour == SpawnerBehaviour.SPAWNER_ONLY) || (spawnedFromSpawner && this.spawnerBehaviour == SpawnerBehaviour.NATURAL_ONLY))
			result = false;
		if ((!spawnedFromStructure && this.structureBehaviour == StructureBehaviour.STRUCTURE_ONLY) || (spawnedFromStructure && this.structureBehaviour == StructureBehaviour.NATURAL_ONLY))
			result = false;

		boolean advancementCondition = false;
		for (MPRAdvancement advancement : this.advancements) {
			if (advancement.conditionApplies(livingEntity))
				advancementCondition = true;
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
