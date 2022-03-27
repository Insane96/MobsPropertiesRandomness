package insane96mcp.mobspropertiesrandomness.json.mob;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRAppliable;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.util.MPRRange;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;

import java.io.File;

public class MPRPhantom implements IMPRObject, IMPRAppliable {
	public MPRRange size;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (size != null)
			size.validate(file);
	}

	@Override
	public void apply(LivingEntity entity, Level world) {
		if (!(entity instanceof Phantom))
			return;

		Phantom phantom = (Phantom) entity;

		phantom.setPhantomSize(this.size.getIntBetween(phantom, world));
	}


	@Override
	public String toString() {
		return String.format("Phantom{size: %s}", size);
	}
}
