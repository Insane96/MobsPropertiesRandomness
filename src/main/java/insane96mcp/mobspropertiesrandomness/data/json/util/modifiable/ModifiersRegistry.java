package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ModifiersRegistry {
    public static final Map<ResourceLocation, Class<? extends MPRModifier>> MODIFIERS = new HashMap<>();

    /// Use you own namespace
    private static void register(String id, Class<? extends MPRModifier> clazz) {
        MODIFIERS.put(MPR.id(id), clazz);
    }

    public static Class<? extends MPRModifier> get(ResourceLocation id) {
        return MODIFIERS.get(id);
    }

    public static ResourceLocation get(Class<? extends MPRModifier> clazz) {
        for (Map.Entry<ResourceLocation, Class<? extends MPRModifier>> entry : MODIFIERS.entrySet()) {
            if (entry.getValue() == clazz)
                return entry.getKey();
        }
        return null;
    }

    public static void init() {
        register("difficulty", MPRDifficultyModifier.class);
        register("deepness", MPRDeepnessModifier.class);
        register("time_played", MPRTimePlayedModifier.class);
        register("distance_from_spawn", MPRDistanceFromSpawnModifier.class);
        register("condition", MPRConditionModifier.class);
    }
}
