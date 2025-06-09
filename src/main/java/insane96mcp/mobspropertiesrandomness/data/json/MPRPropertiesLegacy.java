package insane96mcp.mobspropertiesrandomness.data.json;

import net.minecraft.world.entity.LivingEntity;

public abstract class MPRPropertiesLegacy implements IMPRObject {
	public boolean apply(LivingEntity entity) {
		return true;
	}
}
