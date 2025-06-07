package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class EventsRegistry {
    public static final Map<ResourceLocation, Class<? extends MPREvent>> EVENTS = new HashMap<>();

    /// Use you own namespace
    private static void register(String id, Class<? extends MPREvent> clazz) {
        EVENTS.put(MPR.location(id), clazz);
    }

    public static Class<? extends MPREvent> get(ResourceLocation id) {
        return EVENTS.get(id);
    }

    public static ResourceLocation get(Class<? extends MPREvent> clazz) {
        for (Map.Entry<ResourceLocation, Class<? extends MPREvent>> entry : EVENTS.entrySet()) {
            if (entry.getValue() == clazz)
                return entry.getKey();
        }
        return null;
    }

    public static void init() {
        register("tick", MPRTickEvent.class);
    }
}
