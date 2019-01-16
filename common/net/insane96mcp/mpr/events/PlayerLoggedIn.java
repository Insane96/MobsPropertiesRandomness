package net.insane96mcp.mpr.events;

import net.insane96mcp.mpr.MobsPropertiesRandomness;
import net.insane96mcp.mpr.json.Group;
import net.insane96mcp.mpr.json.Mob;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Mod.EventBusSubscriber(modid = MobsPropertiesRandomness.MOD_ID)
public class PlayerLoggedIn {

	@SubscribeEvent
	public static void EventPlayerLoggedIn(PlayerLoggedInEvent event) {
		boolean correctlyLoaded = Group.LoadGroups() &&	Mob.LoadJsons();
		
		if (!correctlyLoaded)
			event.player.sendMessage(new TextComponentTranslation(MobsPropertiesRandomness.RESOURCE_PREFIX + "json_reload_error"));
	}
}
