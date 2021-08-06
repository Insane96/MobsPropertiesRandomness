package insane96mcp.mobspropertiesrandomness.setup;

import insane96mcp.mobspropertiesrandomness.MobsPropertiesRandomness;
import insane96mcp.mobspropertiesrandomness.module.Modules;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = MobsPropertiesRandomness.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final CommonConfig COMMON;

	public static final ForgeConfigSpec.Builder builder;

	static {
		builder = new ForgeConfigSpec.Builder();
		final Pair<CommonConfig, ForgeConfigSpec> specPair = builder.configure(CommonConfig::new);
		COMMON = specPair.getLeft();
		COMMON_SPEC = specPair.getRight();
	}

	public static class CommonConfig {
		public CommonConfig(final ForgeConfigSpec.Builder builder) {
			Modules.init();
		}
	}

	@SubscribeEvent
	public static void onModConfigEvent(final net.minecraftforge.fml.config.ModConfig.ModConfigEvent event) {
		Modules.loadConfig();
	}
}