package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.setup.Strings;
import insane96mcp.mobspropertiesrandomness.utils.MPRUtils;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;

import java.io.File;

public class MPRConditions implements IMPRObject {

	@SerializedName("is_baby")
	public Boolean isBaby;
	@SerializedName("spawner_behaviour")
	public SpawnerBehaviour spawnerBehaviour;
	@SerializedName("structure_behaviour")
	public StructureBehaviour structureBehaviour;
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
	}

	public boolean conditionsApply(MobEntity mobEntity) {
		boolean result = true;
		if (isBaby != null)
			result = (isBaby && mobEntity.isBaby()) || (!isBaby && !mobEntity.isBaby());

		if (nbt != null) {
			CompoundNBT mobNBT = new CompoundNBT();
			mobEntity.addAdditionalSaveData(mobNBT);
			mobNBT.put("ForgeData", mobEntity.getPersistentData());
			result = MPRUtils.compareNBT(this._nbt, mobNBT);
		}

		CompoundNBT mobPersistentData = mobEntity.getPersistentData();
		boolean spawnedFromSpawner = mobPersistentData.getBoolean(Strings.Tags.SPAWNED_FROM_SPAWNER);
		boolean spawnedFromStructure = mobPersistentData.getBoolean(Strings.Tags.SPAWNED_FROM_STRUCTURE);
		if ((!spawnedFromSpawner && this.spawnerBehaviour == SpawnerBehaviour.SPAWNER_ONLY) || (spawnedFromSpawner && this.spawnerBehaviour == SpawnerBehaviour.NATURAL_ONLY))
			result = false;
		if ((!spawnedFromStructure && this.structureBehaviour == StructureBehaviour.STRUCTURE_ONLY) || (spawnedFromStructure && this.structureBehaviour == StructureBehaviour.NATURAL_ONLY))
			result = false;

		return result;
	}

	@Override
	public String toString() {
		return String.format("Conditions{is_baby: %s, nbt: %s, spawner_behaviour: %s, structure_behaviour: %s}", isBaby, nbt, spawnerBehaviour, structureBehaviour);
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
