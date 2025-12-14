package insane96mcp.mobspropertiesrandomness.data.json.condition;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.Map;

public class ConditionsRegistry {
    public static final Map<ResourceLocation, Class<? extends MPRCondition>> CONDITIONS = new HashMap<>();

    /// Use you own namespace
    private static void register(String id, Class<? extends MPRCondition> clazz) {
        CONDITIONS.put(MPR.location(id), clazz);
    }

    public static Class<? extends MPRCondition> get(ResourceLocation id) {
        return CONDITIONS.get(id);
    }

    public static ResourceLocation get(Class<? extends MPRCondition> clazz) {
        for (Map.Entry<ResourceLocation, Class<? extends MPRCondition>> entry : CONDITIONS.entrySet()) {
            if (entry.getValue() == clazz)
                return entry.getKey();
        }
        return null;
    }

    public static void init() {
        register("advancement", MPRAdvancementCondition.class);
        register("biome", MPRBiomeCondition.class);
        register("block_in", MPRBlockInCondition.class);
        register("block_on", MPRBlockOnCondition.class);
        register("chance", MPRChanceCondition.class);
        register("day_time", MPRDayTimeCondition.class);
        register("deepness", MPRDeepnessCondition.class);
        register("difficulty", MPRDifficultyCondition.class);
        register("dimension", MPRDimensionCondition.class);
        register("distance_from_spawn", MPRDistanceFromSpawnCondition.class);
        register("effect", MPREffectCondition.class);
        register("equipment", MPREquipmentCondition.class);
        register("hardcore", MPRHardcoreCondition.class);
        register("has_target", MPRHasTargetCondition.class);
        register("health", MPRHealthCondition.class);
        register("is_baby", MPRBabyCondition.class);
        register("light_level", MPRLightLevelCondition.class);
        register("mod_loaded", MPRModLoadedCondition.class);
        register("moon_phase", MPRMoonPhaseCondition.class);
        register("nbt", MPRNBTCondition.class);
        register("or", MPROrCondition.class);
        register("spawn_type", MPRSpawnTypeCondition.class);
        register("structure", MPRStructureCondition.class);
        register("tag", MPRTagCondition.class);
        register("temperature", MPRTemperatureCondition.class);
        register("time_played", MPRTimePlayedCondition.class);
        register("weather", MPRWeatherCondition.class);
        if (ModList.get().isLoaded("gamestages"))
            register("game_stage", MPRGameStageCondition.class);
        if (ModList.get().isLoaded("sereneseasons"))
            register("season", MPRSeasonCondition.class);
    }
}
