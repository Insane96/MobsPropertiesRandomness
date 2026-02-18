package insane96mcp.mobspropertiesrandomness.setup;

import insane96mcp.insanelib.core.feature.Module;
import insane96mcp.insanelib.setup.ILConfig;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.module.MPRModules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class MPRConfig {
	public static ModConfigSpec COMMON_SPEC;
	public static ILConfig.CommonConfig COMMON;

	public static ModConfigSpec.Builder builder;

	public static void init(IEventBus eventBus) {
		builder = new ModConfigSpec.Builder();
		final Pair<ILConfig.CommonConfig, ModConfigSpec> specPair = builder.configure(b -> new ILConfig.CommonConfig(b, eventBus));
		COMMON = specPair.getLeft();
		COMMON_SPEC = specPair.getRight();
	}

	public static class CommonConfig {
		public CommonConfig(final ModConfigSpec.Builder builder, IEventBus modEventBus) {
			MPRModules.init(modEventBus);
			Module.loadFeatures(ModConfig.Type.COMMON, MPR.MOD_ID, this.getClass().getClassLoader());
		}
	}
}