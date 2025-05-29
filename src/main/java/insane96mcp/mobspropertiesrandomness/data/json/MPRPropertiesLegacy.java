package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.equipment.MPREquipment;
import insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.events.MPREvents;
import insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.pehuki.MPRScalePehkui;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public abstract class MPRPropertiesLegacy implements IMPRObject {
	public MPREquipment equipment;

	@SerializedName("events")
	public MPREvents events;

	@SerializedName("scale_pehkui")
	public List<MPRScalePehkui> scalePehkui;

	@Override
	public void validate() throws JsonValidationException {
		if (this.equipment == null)
			this.equipment = new MPREquipment();
		this.equipment.validate();

		if (this.events != null)
			this.events.validate();

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

		if (this.scalePehkui != null) {
			for (MPRScalePehkui scalePehkui1 : this.scalePehkui) {
				scalePehkui1.scheduleApply(entity);
			}
		}

		return true;
	}
}
