package insane96mcp.mobspropertiesrandomness.data.json.properties.condition;

import insane96mcp.mobspropertiesrandomness.MobsPropertiesRandomness;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ConditionsRegistry {
    public static final Map<ResourceLocation, Class<? extends MPRCondition>> CONDITIONS = new HashMap<>();

    public static void register(String id, Class<? extends MPRCondition> clazz) {
        CONDITIONS.put(MobsPropertiesRandomness.location(id), clazz);
    }

    public static void init() {
        register("is_baby", MPRBabyCondition.class);
        register("chance", MPRChanceCondition.class);
        register("spawn_type", MPRSpawnTypeCondition.class);
        register("moon_phase", MPRMoonPhaseCondition.class);
    }
}
