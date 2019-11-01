package insane96mcp.mpr;

import com.mojang.brigadier.Command;
import insane96mcp.mpr.commands.ReloadJson;
import insane96mcp.mpr.init.ModConfig;
import insane96mcp.mpr.init.Reflection;
import insane96mcp.mpr.utils.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.File;
import java.nio.file.Paths;

@Mod("mpr")
public class MobsPropertiesRandomness {

    public static final String MOD_ID = "mpr";
    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    public static String configPath;

    public MobsPropertiesRandomness() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    // PreInit
    private void setup(final FMLCommonSetupEvent event) {
        configPath = "config/MobsPropertiesRandomness/";

        Reflection.init();
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);

        //Create config folder if doesn't exist
        new File(configPath).mkdirs();
        ModConfig.init(Paths.get("config", "MobsPropertiesRandomness", MOD_ID + ".toml"));
        Logger.init("logs/MobsPropertiesRandomness.log");
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        ReloadJson.register(event.getCommandDispatcher());
    }
}
