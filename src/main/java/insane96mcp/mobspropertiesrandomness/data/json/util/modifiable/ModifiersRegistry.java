package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import insane96mcp.mobspropertiesrandomness.data.json.MPRRegistry;

public class ModifiersRegistry {
    public static final MPRRegistry<MPRModifier> REGISTRY = new MPRRegistry<>();

    public static void init() {
        REGISTRY.register("difficulty", MPRDifficultyModifier.class);
        REGISTRY.register("deepness", MPRDeepnessModifier.class);
        REGISTRY.register("time_played", MPRTimePlayedModifier.class);
        REGISTRY.register("distance_from_spawn", MPRDistanceFromSpawnModifier.class);
        REGISTRY.register("condition", MPRConditionModifier.class);
    }
}
