package insane96mcp.mobspropertiesrandomness.data.json.properties.outdated;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;

import java.util.List;

public class MPRConditions implements IMPRObject {
	@SerializedName("game_stages_unlocked")
	public List<MPRGameStage> gameStages;
	//TODO Add MPRNbt condition
	public String nbt;
	public transient CompoundTag _nbt;

	public boolean inverted;

	@Override
	public void validate() throws JsonValidationException {
		if (this.nbt != null) {
			try {
				this._nbt = TagParser.parseTag(this.nbt);
			}
			catch (CommandSyntaxException e) {
				throw new JsonValidationException("Invalid nbt for Conditions: " + this.nbt);
			}
		}

		if (this.gameStages != null) {
			if (!ModList.get().isLoaded("gamestages")) {
				throw new JsonValidationException("game_stages_unlocked present in file but no Game Stages mod installed: " + this.nbt);
			}
			else {
				for (MPRGameStage gameStage : this.gameStages) {
					gameStage.validate();
				}
			}
		}
	}

	public boolean conditionsApply(LivingEntity livingEntity) {
		boolean result = true;

		if (this.nbt != null) {
			CompoundTag mobNBT = new CompoundTag();
			livingEntity.saveWithoutId(mobNBT);
			result = MCUtils.compareNBT(this._nbt, mobNBT);
		}

		if (this.gameStages != null) {
			boolean gameStagesCondition = false;
			for (MPRGameStage gameStage : this.gameStages) {
				if (gameStage.conditionApplies(livingEntity)) {
					gameStagesCondition = true;
					break;
				}
			}
			if (!gameStagesCondition)
				result = false;
		}

		if (!this.inverted) return result;
		else return !result;
	}

	@Override
	public String toString() {
		return String.format("Conditions{nbt: %s, game_stages_unlocked: %s, inverted: %s}", this.nbt, this.gameStages, this.inverted);
	}
}
