package insane96mcp.mobspropertiesrandomness.config;

import insane96mcp.mobspropertiesrandomness.MobsPropertiesRandomness;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MobsPropertiesRandomness.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {
	public static boolean debug;

	private static void load() {
		debug = Config.COMMON.debug.get();
	}

	@SubscribeEvent
	public static void onModConfigEvent(final net.minecraftforge.fml.config.ModConfig.ModConfigEvent event) {
		ModConfig.load();
	}
}
