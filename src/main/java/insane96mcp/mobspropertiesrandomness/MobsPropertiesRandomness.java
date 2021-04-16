package insane96mcp.mobspropertiesrandomness;

import insane96mcp.mobspropertiesrandomness.config.Config;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(MobsPropertiesRandomness.MOD_ID)
public class MobsPropertiesRandomness
{
    public static final String MOD_ID = "mobspropertiesrandomness";
    public static final String RESOURCE_PREFIX = MOD_ID + ":";
    private static final Logger LOGGER = LogManager.getLogger();

    public MobsPropertiesRandomness() {
        File directory = new File("config/MobsPropertiesRandomness");
        if (!directory.exists())
            directory.mkdir();

        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, Config.COMMON_SPEC, "MobsPropertiesRandomness/" + MOD_ID + ".toml");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
    }

    public void preInit(FMLCommonSetupEvent event) {
        insane96mcp.mobspropertiesrandomness.utils.Logger.init("logs/MobsPropertiesRandomness.log");
        insane96mcp.mobspropertiesrandomness.utils.Logger.debug("Initialized!");
    }
}
