package insane96mcp.mobspropertiesrandomness.data.json.properties;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class PropertiesRegistry {
    public static final Map<ResourceLocation, Class<? extends MPRProperty>> PROPERTIES = new HashMap<>();

    /// Use you own namespace
    private static void register(String id, Class<? extends MPRProperty> clazz) {
        PROPERTIES.put(MPR.location(id), clazz);
    }

    public static void init() {
        register("potion_effect", MPRPotionEffectProperty.class);
    }
}
