package insane96mcp.mobspropertiesrandomness.data.json.properties.outdated;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.insanelib.util.MCUtils;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.LivingEntity;

public class MPRConditions implements IMPRObject {
	//TODO Add MPRNbt condition
	public String nbt;
	public transient CompoundTag _nbt;

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
	}

	public boolean conditionsApply(LivingEntity livingEntity) {
		boolean result = true;

		if (this.nbt != null) {
			CompoundTag mobNBT = new CompoundTag();
			livingEntity.saveWithoutId(mobNBT);
			result = MCUtils.compareNBT(this._nbt, mobNBT);
		}

		return result;
	}

	@Override
	public String toString() {
		return String.format("Conditions{nbt: %s}", this.nbt);
	}
}
