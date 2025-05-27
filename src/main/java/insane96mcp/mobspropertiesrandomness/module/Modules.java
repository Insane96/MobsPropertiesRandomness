package insane96mcp.mobspropertiesrandomness.module;

import insane96mcp.insanelib.base.Module;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.setup.Config;
import net.minecraftforge.fml.config.ModConfig;

public class Modules {
	public static Module base;

	public static void init() {
		base = Module.Builder.create(MPR.MOD_ID, "base", "Base", ModConfig.Type.COMMON, Config.builder)
				.canBeDisabled(false)
				.enabledByDefault(true)
				.build();

	}
}
