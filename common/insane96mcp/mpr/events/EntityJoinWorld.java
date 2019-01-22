package insane96mcp.mpr.events;

import insane96mcp.mpr.MobsPropertiesRandomness;
import insane96mcp.mpr.json.Mob;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = MobsPropertiesRandomness.MOD_ID)
public class EntityJoinWorld { 
	
	@SubscribeEvent
	public static void EventEntityJoinWorld(EntityJoinWorldEvent event) {
		Mob.Apply(event);
	}
}
