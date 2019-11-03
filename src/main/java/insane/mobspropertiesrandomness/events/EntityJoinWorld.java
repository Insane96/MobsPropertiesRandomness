package insane.mobspropertiesrandomness.events;

import insane.mobspropertiesrandomness.MobsPropertiesRandomness;
import insane.mobspropertiesrandomness.json.JsonMob;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MobsPropertiesRandomness.MOD_ID)
public class EntityJoinWorld {
	@SubscribeEvent
	public static void eventEntityJoinWorld(EntityJoinWorldEvent event) {
		JsonMob.apply(event);
	}
}
