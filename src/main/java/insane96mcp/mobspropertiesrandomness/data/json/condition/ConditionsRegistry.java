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
        register("or", MPROrCondition.class);
        register("chance", MPRChanceCondition.class);
        register("is_baby", MPRBabyCondition.class);
        register("spawn_type", MPRSpawnTypeCondition.class);
        register("moon_phase", MPRMoonPhaseCondition.class);
        register("day_time", MPRDayTimeCondition.class);
        register("time_played", MPRTimePlayedCondition.class);
        register("dimension", MPRDimensionCondition.class);
        register("biome", MPRBiomeCondition.class);
        register("structure", MPRStructureCondition.class);
        register("deepness", MPRDeepnessCondition.class);
        register("distance_from_spawn", MPRDistanceFromSpawnCondition.class);
        register("advancement", MPRAdvancementCondition.class);
        register("difficulty", MPRDifficultyCondition.class);
        register("temperature", MPRTemperatureCondition.class);
        register("has_target", MPRHasTargetCondition.class);
        register("light_level", MPRLightLevelCondition.class);
        register("weather", MPRWeatherCondition.class);
        register("nbt", MPRNBTCondition.class);
        register("mod_loaded", MPRModLoadedCondition.class);
        if (ModList.get().isLoaded("gamestages"))
            register("game_stage", MPRGameStageCondition.class);
    }
}
