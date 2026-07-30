package insane96mcp.mobspropertiesrandomness.data.json.condition;

import insane96mcp.mobspropertiesrandomness.data.json.MPRRegistry;
import insane96mcp.mobspropertiesrandomness.event.MPRRegisterEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

/// Registry of {@link MPRCondition} types.
///
/// To register your own condition type from another mod, subscribe to {@link MPRRegisterEvent}, check for
/// {@link MPRRegisterEvent.Type#CONDITION}, and call {@link MPRRegisterEvent#register} with your own mod's
/// namespace. This registry's own entries (and the `mobspropertiesrandomness` namespace) are always locked
/// before the event fires, so registering in response to it is safe regardless of mod load order.
public class ConditionsRegistry {
    private static final MPRRegistry<MPRCondition> REGISTRY = new MPRRegistry<>(MPRCondition.class);

    public static void init() {
        REGISTRY.register("advancement", MPRAdvancementCondition.class);
        REGISTRY.register("biome", MPRBiomeCondition.class);
        REGISTRY.register("block_in", MPRBlockInCondition.class);
        REGISTRY.register("block_on", MPRBlockOnCondition.class);
        REGISTRY.register("chance", MPRChanceCondition.class);
        REGISTRY.register("day_time", MPRDayTimeCondition.class);
        REGISTRY.register("deepness", MPRDeepnessCondition.class);
        REGISTRY.register("difficulty", MPRDifficultyCondition.class);
        REGISTRY.register("dimension", MPRDimensionCondition.class);
        REGISTRY.register("distance_from_spawn", MPRDistanceFromSpawnCondition.class);
        REGISTRY.register("effect", MPREffectCondition.class);
        REGISTRY.register("equipment", MPREquipmentCondition.class);
        REGISTRY.register("hardcore", MPRHardcoreCondition.class);
        REGISTRY.register("has_owner", MPRHasOwnerCondition.class);
        REGISTRY.register("has_target", MPRHasTargetCondition.class);
        REGISTRY.register("health", MPRHealthCondition.class);
        REGISTRY.register("is_baby", MPRBabyCondition.class);
        REGISTRY.register("light_level", MPRLightLevelCondition.class);
        REGISTRY.register("mod_loaded", MPRModLoadedCondition.class);
        REGISTRY.register("moon_phase", MPRMoonPhaseCondition.class);
        REGISTRY.register("nbt", MPRNBTCondition.class);
        REGISTRY.register("or", MPROrCondition.class);
        REGISTRY.register("score", MPRScoreCondition.class);
        REGISTRY.register("spawn_type", MPRSpawnTypeCondition.class);
        REGISTRY.register("structure", MPRStructureCondition.class);
        REGISTRY.register("tag", MPRTagCondition.class);
        REGISTRY.register("temperature", MPRTemperatureCondition.class);
        REGISTRY.register("time_played", MPRTimePlayedCondition.class);
        REGISTRY.register("weather", MPRWeatherCondition.class);
        //if (ModList.get().isLoaded("gamestages"))
        //    REGISTRY.register("game_stage", MPRGameStageCondition.class);
        //if (ModList.get().isLoaded("sereneseasons"))
        //    REGISTRY.register("season", MPRSeasonCondition.class);
        REGISTRY.lockMprNamespace();
        NeoForge.EVENT_BUS.post(new MPRRegisterEvent(MPRRegisterEvent.Type.CONDITION, REGISTRY));
    }

    @Nullable
    static Class<? extends MPRCondition> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    @Nullable
    static ResourceLocation getId(Class<? extends MPRCondition> clazz) {
        return REGISTRY.getId(clazz);
    }
}
