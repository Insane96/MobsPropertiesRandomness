package insane96mcp.mobspropertiesrandomness.data.json.condition;

import insane96mcp.mobspropertiesrandomness.data.json.MPRRegistry;

public class ConditionsRegistry {
    public static final MPRRegistry<MPRCondition> REGISTRY = new MPRRegistry<>();

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
    }
}
