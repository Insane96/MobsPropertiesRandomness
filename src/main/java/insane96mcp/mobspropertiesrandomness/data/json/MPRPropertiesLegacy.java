package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.MPRNbt;
import insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.equipment.MPREquipment;
import insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.events.MPREvents;
import insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.pehuki.MPRScalePehkui;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class MPRPropertiesLegacy implements IMPRObject {
	public MPREquipment equipment;

	@SerializedName("events")
	public MPREvents events;

	@SerializedName("set_nbt")
	public List<MPRNbt> setNbt;

	@SerializedName("set_raw_nbt")
	public String setRawNbt;
	public transient CompoundTag _rawNbt = null;

	@SerializedName("scale_pehkui")
	public List<MPRScalePehkui> scalePehkui;

	@Override
	public void validate() throws JsonValidationException {
		if (this.equipment == null)
			this.equipment = new MPREquipment();
		this.equipment.validate();

		if (this.events != null)
			this.events.validate();

		if (this.setNbt == null)
			this.setNbt = new ArrayList<>();
		for (MPRNbt mprNbt : this.setNbt) {
			mprNbt.validate();
		}

		if (this.setRawNbt != null) {
			try {
				this._rawNbt = TagParser.parseTag(this.setRawNbt);
			}
			catch (CommandSyntaxException e) {
				throw new JsonValidationException("Invalid raw nbt in properties: " + this.setRawNbt);
			}
		}

		if (this.scalePehkui != null)
		{
			for (MPRScalePehkui scalePehkui1 : this.scalePehkui) {
				scalePehkui1.validate();
			}
		}
	}

	public boolean apply(LivingEntity entity) {
		this.equipment.apply(entity);

		if (this.events != null)
			this.events.addToNBT(entity);

		for (MPRNbt mprNbt : this.setNbt) {
			mprNbt.apply(entity);
		}

		if (this._rawNbt != null) {
			entity.readAdditionalSaveData(this._rawNbt);
		}

		if (this.scalePehkui != null) {
			for (MPRScalePehkui scalePehkui1 : this.scalePehkui) {
				scalePehkui1.scheduleApply(entity);
			}
		}

		return true;
	}
}
