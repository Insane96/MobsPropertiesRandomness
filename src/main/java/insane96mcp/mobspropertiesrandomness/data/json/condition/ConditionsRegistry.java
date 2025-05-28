package insane96mcp.mobspropertiesrandomness.data.json.condition;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.resources.ResourceLocation;

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
        register("is_baby", MPRBabyCondition.class);
        register("chance", MPRChanceCondition.class);
        register("spawn_type", MPRSpawnTypeCondition.class);
        register("moon_phase", MPRMoonPhaseCondition.class);
        register("day_time", MPRDayTimeCondition.class);
        register("days_passed", MPRDaysPassedCondition.class);
        register("dimension", MPRDimensionCondition.class);
        register("biome", MPRBiomeCondition.class);
        register("structure", MPRStructureCondition.class);
        register("deepness", MPRDeepnessCondition.class);
        register("distance_from_spawn", MPRDistanceFromSpawnCondition.class);
        register("advancement", MPRAdvancementCondition.class);
        register("game_stage", MPRGameStageCondition.class);
        register("nbt", MPRNBTCondition.class);
    }
}
