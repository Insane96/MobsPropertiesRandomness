package insane96mcp.mobspropertiesrandomness;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import insane96mcp.insanelib.data.AttributeModifierOperationSerializer;
import insane96mcp.insanelib.setup.ILModConfig;
import insane96mcp.mobspropertiesrandomness.command.MPRCommand;
import insane96mcp.mobspropertiesrandomness.data.MPRMobReloadListener;
import insane96mcp.mobspropertiesrandomness.data.MPRMobsPresetReloadListener;
import insane96mcp.mobspropertiesrandomness.data.MPRRawPresetLoader;
import insane96mcp.mobspropertiesrandomness.data.json.condition.ConditionsRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRWeatherCondition;
import insane96mcp.mobspropertiesrandomness.data.json.property.PropertiesRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.property.equipment.ItemFunctionsRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.EventsRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.property.events.MPRChangeTargetEvent;
import insane96mcp.mobspropertiesrandomness.data.json.util.NBTType;
import insane96mcp.mobspropertiesrandomness.data.json.util.PlayerMode;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.ModifiersRegistry;
import insane96mcp.mobspropertiesrandomness.data.serializer.NameEnumSerializer;
import insane96mcp.mobspropertiesrandomness.data.serializer.StrictEnumDeserializer;
import insane96mcp.mobspropertiesrandomness.util.MPRChatNotifier;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;

import javax.annotation.Nullable;

@Mod(MPR.MOD_ID)
public class MPR
{
    public static final String MOD_ID = "mobspropertiesrandomness";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ILModConfig CONFIG;

    public MPR(IEventBus modEventBus, ModContainer modContainer) {
        CONFIG = new ILModConfig(id("main"), "Single Module", ModConfig.Type.COMMON, modEventBus, MPR.class.getClassLoader());
        modContainer.registerConfig(ModConfig.Type.COMMON, CONFIG.spec);
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
        SerializerUtils.REGISTRY_ACCESS = event.getRegistryAccess();
        event.addListener(MPRRawPresetLoader.MODIFIER_LOADER);
        event.addListener(MPRRawPresetLoader.CONDITION_LOADER);
        event.addListener(MPRRawPresetLoader.FUNCTION_LOADER);
        event.addListener(MPRMobsPresetReloadListener.INSTANCE);
        event.addListener(MPRMobReloadListener.INSTANCE);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
            MPRChatNotifier.notifyJoiningOp(serverPlayer);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext context = event.getBuildContext();
        MPRCommand.register(dispatcher, context);
    }

    public void preInit(FMLCommonSetupEvent event) {
        MPRLogger.init("logs/MobsPropertiesRandomness.log");
    }

    public static ResourceLocation id(String path) {
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
                .registerTypeAdapter(EquipmentSlot.class, new NameEnumSerializer<>("EquipmentSlot", EquipmentSlot::byName, EquipmentSlot::getName))
                .registerTypeAdapter(AttributeModifier.Operation.class, new AttributeModifierOperationSerializer())
                .registerTypeAdapter(BossEvent.BossBarColor.class, new NameEnumSerializer<>("BossBarColor", BossEvent.BossBarColor::byName, BossEvent.BossBarColor::getName))
                .registerTypeAdapter(BossEvent.BossBarOverlay.class, new NameEnumSerializer<>("BossBarOverlay", BossEvent.BossBarOverlay::byName, BossEvent.BossBarOverlay::getName))
                .registerTypeAdapter(Difficulty.class, new NameEnumSerializer<>("Difficulty", Difficulty::byName, Difficulty::getKey))
                .registerTypeAdapter(LightLayer.class, new NameEnumSerializer<>("LightLayer", n -> LightLayer.valueOf(n.toUpperCase()), l -> l.toString().toLowerCase()))
                .registerTypeAdapter(MobSpawnType.class, new NameEnumSerializer<>("MobSpawnType", n -> MobSpawnType.valueOf(n.toUpperCase()), t -> t.name().toLowerCase()))
                .registerTypeAdapter(NBTType.class, new StrictEnumDeserializer<>(NBTType.class))
                .registerTypeAdapter(EquipmentSlotGroup.class, new StrictEnumDeserializer<>(EquipmentSlotGroup.class))
                .registerTypeAdapter(PlayerMode.class, new StrictEnumDeserializer<>(PlayerMode.class))
                .registerTypeAdapter(MPRChangeTargetEvent.ChangeType.class, new StrictEnumDeserializer<>(MPRChangeTargetEvent.ChangeType.class))
                .registerTypeAdapter(MPRWeatherCondition.Weather.class, new StrictEnumDeserializer<>(MPRWeatherCondition.Weather.class))
                .registerTypeAdapter(MPRRange.Bias.class, new StrictEnumDeserializer<>(MPRRange.Bias.class))
                .registerTypeAdapter(EventPriority.class, new StrictEnumDeserializer<>(EventPriority.class));
        //if (ModList.get().isLoaded("sereneseasons"))
        //    gsonBuilder.registerTypeAdapter(Season.SubSeason.class, new SubSeasonSerializer());
        return gsonBuilder.create();
    }
}
