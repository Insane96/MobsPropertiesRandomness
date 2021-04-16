package insane96mcp.mobspropertiesrandomness.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final CommonConfig COMMON;

	static {
		final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON = specPair.getLeft();
		COMMON_SPEC = specPair.getRight();
	}

	public static class CommonConfig {

		public ForgeConfigSpec.ConfigValue<Boolean> debug;

		public CommonConfig(final ForgeConfigSpec.Builder builder) {
			debug = builder
					.comment("All the loaded JSONs will be logged. Recommended to be disabled when your Pack is released.")
					.define("Debug loaded JSONs", true);
		}
	}
}