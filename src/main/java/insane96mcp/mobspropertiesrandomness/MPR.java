package insane96mcp.mobspropertiesrandomness;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener;
import insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener;
import insane96mcp.mobspropertiesrandomness.data.json.condition.ConditionsRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.properties.PropertiesRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.properties.equipment.ItemFunctionsRegistry;
import insane96mcp.mobspropertiesrandomness.data.serializer.AttributeModifierOperationSerializer;
import insane96mcp.mobspropertiesrandomness.data.serializer.BossBarColorSerializer;
import insane96mcp.mobspropertiesrandomness.data.serializer.BossBarOverlaySerializer;
import insane96mcp.mobspropertiesrandomness.data.serializer.EquipmentSlotSerializer;
import insane96mcp.mobspropertiesrandomness.setup.Config;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

@Mod(MPR.MOD_ID)
public class MPR
{
    public static final String MOD_ID = "mobspropertiesrandomness";
    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    public MPR(FMLJavaModLoadingContext modContext) {
        modContext.registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, Config.COMMON_SPEC);
        modContext.getModEventBus().addListener(this::preInit);
        MinecraftForge.EVENT_BUS.register(this);

        ConditionsRegistry.init();
        PropertiesRegistry.init();
        ItemFunctionsRegistry.init();
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

    @Nullable
    public static ResourceLocation locationFrom(String s) {
        String[] split = s.split(":");
        if (s.contains(":"))
            return ResourceLocation.tryParse(s);
        else
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, split[0]);
    }

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(EquipmentSlot.class, new EquipmentSlotSerializer())
                .registerTypeAdapter(AttributeModifier.Operation.class, new AttributeModifierOperationSerializer())
                .registerTypeAdapter(BossEvent.BossBarColor.class, new BossBarColorSerializer())
                .registerTypeAdapter(BossEvent.BossBarOverlay.class, new BossBarOverlaySerializer())
                .create();
    }
}
