package insane96mcp.mobspropertiesrandomness.json.mobs;

import insane96mcp.mobspropertiesrandomness.exception.InvalidJsonException;
import insane96mcp.mobspropertiesrandomness.json.IMPRAppliable;
import insane96mcp.mobspropertiesrandomness.json.IMPRObject;
import insane96mcp.mobspropertiesrandomness.json.utils.MPRRange;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.world.World;

import java.io.File;

public class MPRPhantom implements IMPRObject, IMPRAppliable {
	public MPRRange size;

	@Override
	public void validate(File file) throws InvalidJsonException {
		if (size != null)
			size.validate(file);
	}

	@Override
	public void apply(MobEntity entity, World world) {
		if (!(entity instanceof PhantomEntity))
			return;

		PhantomEntity phantom = (PhantomEntity) entity;

		phantom.setPhantomSize(this.size.getIntBetween(phantom, world));
	}


	@Override
	public String toString() {
		return String.format("Phantom{size: %s}", size);
	}
}
