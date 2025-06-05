package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.annotations.SerializedName;
import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.properties.events.MPREvents;
import net.minecraft.world.entity.LivingEntity;

public abstract class MPRPropertiesLegacy implements IMPRObject {
	@SerializedName("events")
	public MPREvents events;

	@Override
	public void validate() throws JsonValidationException {
		if (this.events != null)
			this.events.validate();
	}

	public boolean apply(LivingEntity entity) {
		if (this.events != null)
			this.events.addToNBT(entity);

		return true;
	}
}
