package insane96mcp.mpr.events;

import insane96mcp.mpr.MobsPropertiesRandomness;
import insane96mcp.mpr.init.ModConfig;
import insane96mcp.mpr.json.Group;
import insane96mcp.mpr.json.Mob;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Mod.EventBusSubscriber(modid = MobsPropertiesRandomness.MOD_ID)
public class PlayerLoggedIn {

	@SubscribeEvent
	public static void eventPlayerLoggedIn(PlayerLoggedInEvent event) {
		if (ModConfig.General.debug.get())
			event.getPlayer().sendMessage(new TranslationTextComponent(MobsPropertiesRandomness.RESOURCE_PREFIX + "reload_json"));
		boolean correctlyLoaded = Group.loadGroups() &&	Mob.loadJsons();
		
		if (!correctlyLoaded) {
			event.getPlayer().sendMessage(new TranslationTextComponent(MobsPropertiesRandomness.RESOURCE_PREFIX + "json_reloaded_error"));
		}
		else if (ModConfig.General.debug.get())
		event.getPlayer().sendMessage(new TranslationTextComponent(MobsPropertiesRandomness.RESOURCE_PREFIX + "json_reloaded"));
	}
}
