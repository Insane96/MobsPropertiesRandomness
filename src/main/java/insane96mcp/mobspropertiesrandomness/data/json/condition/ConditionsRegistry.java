package insane96mcp.mobspropertiesrandomness.data.json.condition;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

/// Registry of {@link MPRCondition} types.
///
/// To register your own condition type from another mod, use {@link #REGISTRY_KEY} with a {@code RegisterEvent}
/// (or a {@code DeferredRegister}) on your mod's event bus, exactly like registering any other NeoForge registry
/// entry (e.g. blocks or items).
public class ConditionsRegistry {
    public static final ResourceKey<Registry<Class<? extends MPRCondition>>> REGISTRY_KEY =
            ResourceKey.createRegistryKey(MPR.id("conditions"));

    private static final Registry<Class<? extends MPRCondition>> REGISTRY =
            new RegistryBuilder<>(REGISTRY_KEY).create();

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener((NewRegistryEvent event) -> event.register(REGISTRY));
        modEventBus.addListener(ConditionsRegistry::registerDefaults);
    }

    private static void registerDefaults(RegisterEvent event) {
        event.register(REGISTRY_KEY, helper -> {
            helper.register(MPR.id("advancement"), MPRAdvancementCondition.class);
            helper.register(MPR.id("biome"), MPRBiomeCondition.class);
            helper.register(MPR.id("block_in"), MPRBlockInCondition.class);
            helper.register(MPR.id("block_on"), MPRBlockOnCondition.class);
            helper.register(MPR.id("chance"), MPRChanceCondition.class);
            helper.register(MPR.id("day_time"), MPRDayTimeCondition.class);
            helper.register(MPR.id("deepness"), MPRDeepnessCondition.class);
            helper.register(MPR.id("difficulty"), MPRDifficultyCondition.class);
            helper.register(MPR.id("dimension"), MPRDimensionCondition.class);
            helper.register(MPR.id("distance_from_spawn"), MPRDistanceFromSpawnCondition.class);
            helper.register(MPR.id("effect"), MPREffectCondition.class);
            helper.register(MPR.id("equipment"), MPREquipmentCondition.class);
            helper.register(MPR.id("hardcore"), MPRHardcoreCondition.class);
            helper.register(MPR.id("has_owner"), MPRHasOwnerCondition.class);
            helper.register(MPR.id("has_target"), MPRHasTargetCondition.class);
            helper.register(MPR.id("health"), MPRHealthCondition.class);
            helper.register(MPR.id("is_baby"), MPRBabyCondition.class);
            helper.register(MPR.id("light_level"), MPRLightLevelCondition.class);
            helper.register(MPR.id("mod_loaded"), MPRModLoadedCondition.class);
            helper.register(MPR.id("moon_phase"), MPRMoonPhaseCondition.class);
            helper.register(MPR.id("nbt"), MPRNBTCondition.class);
            helper.register(MPR.id("or"), MPROrCondition.class);
            helper.register(MPR.id("score"), MPRScoreCondition.class);
            helper.register(MPR.id("spawn_type"), MPRSpawnTypeCondition.class);
            helper.register(MPR.id("structure"), MPRStructureCondition.class);
            helper.register(MPR.id("tag"), MPRTagCondition.class);
            helper.register(MPR.id("temperature"), MPRTemperatureCondition.class);
            helper.register(MPR.id("time_played"), MPRTimePlayedCondition.class);
            helper.register(MPR.id("weather"), MPRWeatherCondition.class);
            //if (ModList.get().isLoaded("gamestages"))
            //    helper.register(MPR.id("game_stage"), MPRGameStageCondition.class);
            //if (ModList.get().isLoaded("sereneseasons"))
            //    helper.register(MPR.id("season"), MPRSeasonCondition.class);
        });
    }

    @Nullable
    static Class<? extends MPRCondition> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    @Nullable
    static ResourceLocation getId(Class<? extends MPRCondition> clazz) {
        return REGISTRY.getKey(clazz);
    }
}
