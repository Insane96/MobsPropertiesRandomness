package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.equipment.MPREquipment;
import insane96mcp.mobspropertiesrandomness.data.json.properties.outdated.events.MPREvents;
import net.minecraft.world.entity.LivingEntity;

public abstract class MPRPropertiesLegacy implements IMPRObject {
	public MPREquipment equipment;

	@SerializedName("events")
	public MPREvents events;

	@Override
	public void validate() throws JsonValidationException {
		if (this.equipment == null)
			this.equipment = new MPREquipment();
		this.equipment.validate();

		if (this.events != null)
			this.events.validate();
	}

	public boolean apply(LivingEntity entity) {
		this.equipment.apply(entity);

		if (this.events != null)
			this.events.addToNBT(entity);

		return true;
	}
}
