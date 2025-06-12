package insane96mcp.mobspropertiesrandomness;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import insane96mcp.mobspropertiesrandomness.command.MPRCommand;
import insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener;
import insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener;
import insane96mcp.mobspropertiesrandomness.data.json.condition.ConditionsRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRScalePehkuiProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.PropertiesRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.property.equipment.ItemFunctionsRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.EventsRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.MPRChangeTargetEvent;
import insane96mcp.mobspropertiesrandomness.data.json.util.NBTType;
import insane96mcp.mobspropertiesrandomness.data.json.util.PlayerMode;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.ModifiersRegistry;
import insane96mcp.mobspropertiesrandomness.data.serializer.*;
import insane96mcp.mobspropertiesrandomness.setup.Config;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
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
        ModifiersRegistry.init();
        EventsRegistry.init();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(MPRPresetReloadListener.INSTANCE);
        event.addListener(MPRMobReloadListener.INSTANCE);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext context = event.getBuildContext();
        MPRCommand.register(dispatcher, context);
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
                .registerTypeAdapter(Difficulty.class, new DifficultySerializer())
                .registerTypeAdapter(LightLayer.class, new LightLayerSerializer())
                .registerTypeAdapter(NBTType.class, new StrictEnumDeserializer<>(NBTType.class))
                .registerTypeAdapter(PlayerMode.class, new StrictEnumDeserializer<>(PlayerMode.class))
                .registerTypeAdapter(MPRChangeTargetEvent.ChangeType.class, new StrictEnumDeserializer<>(MPRChangeTargetEvent.ChangeType.class))
                .registerTypeAdapter(MPRScalePehkuiProperty.Operation.class, new StrictEnumDeserializer<>(MPRScalePehkuiProperty.Operation.class))
                .create();
    }
}
