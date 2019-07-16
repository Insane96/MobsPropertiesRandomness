package insane96mcp.mpr.init;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import java.nio.file.Path;

public class ModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static void init(Path file) {
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

            public static ConfigValue<Boolean> debug;

            public static void init() {
                BUILDER.push(name);
                debug = BUILDER
                        .comment("Enable debug info in log file (logs/MobsPropertiesRandomness.log), useful when configuring the mod by modifying JSONs")
                        .define("Debug", true);
                BUILDER.pop();
        }
    }

    public static class Difficulty {
        public static String name = "Difficulty";

        public static ConfigValue<Double> easyMultiplier;
        public static ConfigValue<Double> normalMultiplier;
        public static ConfigValue<Double> hardMultiplier;

        public static void init() {
            BUILDER.push(name);
            easyMultiplier = BUILDER
                    .comment("Values affected by difficulty will be multiplied by this value in Easy Difficulty")
                    .defineInRange("Easy Multiplier", 0.5, 0.0, Double.MAX_VALUE);
            normalMultiplier = BUILDER
                    .comment("Values affected by difficulty will be multiplied by this value in Normal Difficulty")
                    .defineInRange("Normal Multiplier", 1.0, 0.0, Double.MAX_VALUE);
            hardMultiplier = BUILDER
                    .comment("Values affected by difficulty will be multiplied by this value in Hard Difficulty")
                    .defineInRange("Hard Multiplier", 2.0, 0.0, Double.MAX_VALUE);
            BUILDER.pop();
        }
    }

    static {
        General.init();
        Difficulty.init();

        SPEC = BUILDER.build();
    }
}
