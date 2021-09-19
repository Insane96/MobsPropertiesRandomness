package insane96mcp.mobspropertiesrandomness;

import insane96mcp.mobspropertiesrandomness.data.MPRGroupReloadListener;
import insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener;
import insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener;
import insane96mcp.mobspropertiesrandomness.network.NetworkHandler;
import insane96mcp.mobspropertiesrandomness.setup.Config;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.File;

@Mod(MobsPropertiesRandomness.MOD_ID)
public class MobsPropertiesRandomness
{
    public static final String MOD_ID = "mobspropertiesrandomness";
    public static final String RESOURCE_PREFIX = MOD_ID + ":";
    public static final String CONFIG_FOLDER = "config/MobsPropertiesRandomness";

    public MobsPropertiesRandomness() {
        File directory = new File(CONFIG_FOLDER);
        if (!directory.exists())
            directory.mkdir();

        MPRGroupReloadListener.groupsFolder = new File(MobsPropertiesRandomness.CONFIG_FOLDER + "/groups");
        if (!MPRGroupReloadListener.groupsFolder.exists())
            MPRGroupReloadListener.groupsFolder.mkdir();

        MPRMobReloadListener.mobsFolder = new File(MobsPropertiesRandomness.CONFIG_FOLDER + "/mobs");
        File oldFolder = new File(MobsPropertiesRandomness.CONFIG_FOLDER + "/json");
        if (!MPRMobReloadListener.mobsFolder.exists()) {
            if (oldFolder.exists())
                oldFolder.renameTo(MPRMobReloadListener.mobsFolder);
            else
                MPRMobReloadListener.mobsFolder.mkdir();
        }

        MPRPresetReloadListener.presetsFolder = new File(MobsPropertiesRandomness.CONFIG_FOLDER + "/presets");
        if (!MPRPresetReloadListener.presetsFolder.exists())
            MPRPresetReloadListener.presetsFolder.mkdir();

        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, Config.COMMON_SPEC, "MobsPropertiesRandomness/" + MOD_ID + ".toml");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(MPRGroupReloadListener.INSTANCE);
        event.addListener(MPRMobReloadListener.INSTANCE);
        event.addListener(MPRPresetReloadListener.INSTANCE);
    }

    public void preInit(FMLCommonSetupEvent event) {
        insane96mcp.mobspropertiesrandomness.utils.Logger.init("logs/MobsPropertiesRandomness.log");
        insane96mcp.mobspropertiesrandomness.utils.Logger.debug("Initialized!");
        NetworkHandler.init();
    }
}
