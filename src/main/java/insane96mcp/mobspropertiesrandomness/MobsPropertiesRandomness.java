package insane96mcp.mobspropertiesrandomness;

import insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener;
import insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener;
import insane96mcp.mobspropertiesrandomness.data.json.properties.condition.ConditionsRegistry;
import insane96mcp.mobspropertiesrandomness.setup.Config;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MobsPropertiesRandomness.MOD_ID)
public class MobsPropertiesRandomness
{
    public static final String MOD_ID = "mobspropertiesrandomness";
    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    public MobsPropertiesRandomness(FMLJavaModLoadingContext modContext) {
        modContext.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, Config.COMMON_SPEC);
        modContext.getModEventBus().addListener(this::preInit);
        MinecraftForge.EVENT_BUS.register(this);

        ConditionsRegistry.init();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(MPRPresetReloadListener.INSTANCE);
        event.addListener(MPRMobReloadListener.INSTANCE);
    }

    public void preInit(FMLCommonSetupEvent event) {
        Logger.init("logs/MobsPropertiesRandomness.log");
        Logger.debug("Initialized!");
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
