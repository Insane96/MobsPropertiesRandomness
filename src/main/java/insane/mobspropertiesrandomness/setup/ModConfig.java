package insane.mobspropertiesrandomness.setup;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import insane.mobspropertiesrandomness.MobsPropertiesRandomness;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = MobsPropertiesRandomness.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec SPEC;

	public static void init(Path file) {
		new File("config/" + MobsPropertiesRandomness.MOD_ID).mkdirs();
		final CommentedFileConfig configData = CommentedFileConfig.builder(file)
				.sync()
				.autosave()
				.writingMode(WritingMode.REPLACE)
				.build();

		configData.load();
		SPEC.setConfig(configData);
	}

	public static class General {
		public static String name = "General";

		public static ForgeConfigSpec.ConfigValue<Boolean> debug;

		public static void init() {
			BUILDER.push(name);
			debug = BUILDER
					.comment("Enable debug info in log file (logs/mobspropertiesrandomness.log), useful when configuring the mod by adding and modifying JSONs")
					.define("Debug", true);
			BUILDER.pop();

		}
	}

	static {
		General.init();

		SPEC = BUILDER.build();
	}

	//TODO Those don't work
	@SubscribeEvent
	public static void eventConfigReload(final net.minecraftforge.fml.config.ModConfig.ConfigReloading event) {
		Logger.Info("Config Reloaded");
	}

	@SubscribeEvent
	public static void eventConfigLoad(final net.minecraftforge.fml.config.ModConfig.Loading event) {
		Logger.Info("Config Loaded");
	}
}