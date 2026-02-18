package insane96mcp.mobspropertiesrandomness;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import insane96mcp.mobspropertiesrandomness.command.MPRCommand;
import insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener;
import insane96mcp.mobspropertiesrandomness.data.MPRPresetReloadListener;
import insane96mcp.mobspropertiesrandomness.data.json.condition.ConditionsRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRWeatherCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.MPRScalePehkuiProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.PropertiesRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.property.equipment.ItemFunctionsRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.EventsRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.MPRChangeTargetEvent;
import insane96mcp.mobspropertiesrandomness.data.json.util.NBTType;
import insane96mcp.mobspropertiesrandomness.data.json.util.PlayerMode;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.ModifiersRegistry;
import insane96mcp.mobspropertiesrandomness.data.serializer.*;
import insane96mcp.mobspropertiesrandomness.setup.MPRConfig;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.LightLayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import javax.annotation.Nullable;

@Mod(MPR.MOD_ID)
public class MPR
{
    public static final String MOD_ID = "mobspropertiesrandomness";
    public static final String RESOURCE_PREFIX = MOD_ID + ":";

    public MPR(IEventBus modEventBus, ModContainer modContainer) {
        MPRConfig.init(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, MPRConfig.COMMON_SPEC);
        modEventBus.addListener(this::preInit);
        NeoForge.EVENT_BUS.register(this);

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
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(EquipmentSlot.class, new EquipmentSlotSerializer())
                .registerTypeAdapter(AttributeModifier.Operation.class, new AttributeModifierOperationSerializer())
                .registerTypeAdapter(BossEvent.BossBarColor.class, new BossBarColorSerializer())
                .registerTypeAdapter(BossEvent.BossBarOverlay.class, new BossBarOverlaySerializer())
                .registerTypeAdapter(Difficulty.class, new DifficultySerializer())
                .registerTypeAdapter(LightLayer.class, new LightLayerSerializer())
                .registerTypeAdapter(MobSpawnType.class, new MobSpawnTypeSerializer())
                .registerTypeAdapter(NBTType.class, new StrictEnumDeserializer<>(NBTType.class))
                .registerTypeAdapter(EquipmentSlotGroup.class, new StrictEnumDeserializer<>(EquipmentSlotGroup.class))
                .registerTypeAdapter(PlayerMode.class, new StrictEnumDeserializer<>(PlayerMode.class))
                .registerTypeAdapter(MPRChangeTargetEvent.ChangeType.class, new StrictEnumDeserializer<>(MPRChangeTargetEvent.ChangeType.class))
                .registerTypeAdapter(MPRScalePehkuiProperty.Operation.class, new StrictEnumDeserializer<>(MPRScalePehkuiProperty.Operation.class))
                .registerTypeAdapter(MPRWeatherCondition.Weather.class, new StrictEnumDeserializer<>(MPRWeatherCondition.Weather.class))
                .registerTypeAdapter(MPRRange.Bias.class, new StrictEnumDeserializer<>(MPRRange.Bias.class))
                .registerTypeAdapter(EventPriority.class, new StrictEnumDeserializer<>(EventPriority.class));
        //if (ModList.get().isLoaded("sereneseasons"))
        //    gsonBuilder.registerTypeAdapter(Season.SubSeason.class, new SubSeasonSerializer());
        return gsonBuilder.create();
    }
}
