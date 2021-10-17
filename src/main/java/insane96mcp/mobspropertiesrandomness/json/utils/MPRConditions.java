package insane96mcp.mobspropertiesrandomness.json.utils;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.utils.MPRUtils;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;

import java.io.File;

public class MPRConditions implements IMPRObject {

	@SerializedName("is_baby")
	public Boolean isBaby;
	public String nbt;
	public transient CompoundNBT _nbt;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (this.nbt != null) {
			try {
				this._nbt = JsonToNBT.getTagFromJson(this.nbt);
			}
			catch (CommandSyntaxException e) {
				throw new InvalidJsonException("Invalid nbt for Conditions: " + this.nbt, file);
			}
		}
	}

	public boolean conditionsApply(MobEntity mobEntity) {
		boolean result = true;
		if (isBaby != null)
			result = (isBaby && mobEntity.isChild()) || (!isBaby && !mobEntity.isChild());

		if (nbt != null) {
			CompoundNBT mobNBT = new CompoundNBT();
			mobEntity.writeAdditional(mobNBT);
			result = MPRUtils.compareNBT(this._nbt, mobNBT);
		}
		return result;
	}

	@Override
	public String toString() {
		return String.format("Conditions{is_baby: %s, nbt: %s}", isBaby, nbt);
	}
}
