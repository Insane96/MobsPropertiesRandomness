package insane96mcp.mobspropertiesrandomness.data.json;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface IMPRAppliable {
	public void apply(LivingEntity entity, Level world);
}
