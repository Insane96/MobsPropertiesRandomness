package insane96mcp.mobspropertiesrandomness.module;

import insane96mcp.insanelib.core.feature.Module;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.setup.MPRConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.config.ModConfig;

public class MPRModules {
	public static Module base;

	public static void init(IEventBus modEventBus) {
		base = Module.Builder.create(MPR.MOD_ID, "base", "Base", ModConfig.Type.COMMON, MPRConfig.builder, modEventBus)
				.canBeDisabled(false)
				.enabledByDefault(true)
				.build();

	}
}
