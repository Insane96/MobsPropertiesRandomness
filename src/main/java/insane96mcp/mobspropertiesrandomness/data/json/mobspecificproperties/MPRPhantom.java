package insane96mcp.mobspropertiesrandomness.data.json.mobspecificproperties;

import insane96mcp.insanelib.exception.JsonValidationException;
import insane96mcp.mobspropertiesrandomness.data.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;

public class MPRPhantom implements IMPRObject {
	public MPRRange size;

	@Override
	public void validate() throws JsonValidationException {
		if (size != null)
			size.validate();
	}

	public void apply(LivingEntity entity, Level world) {
		if (!(entity instanceof Phantom))
			return;

		Phantom phantom = (Phantom) entity;

		phantom.setPhantomSize(this.size.getInt(phantom, world));
	}


	@Override
	public String toString() {
		return String.format("Phantom{size: %s}", size);
	}
}
