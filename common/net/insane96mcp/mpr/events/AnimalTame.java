package net.insane96mcp.mpr.events;

import net.insane96mcp.mpr.MobsPropertiesRandomness;
import net.insane96mcp.mpr.json.mobs.Wolf;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = MobsPropertiesRandomness.MOD_ID)
public class AnimalTame {
	@SubscribeEvent
	public static void EventAnimalTame(AnimalTameEvent event) {
		Wolf.FixOnTame(event);
	}
}
