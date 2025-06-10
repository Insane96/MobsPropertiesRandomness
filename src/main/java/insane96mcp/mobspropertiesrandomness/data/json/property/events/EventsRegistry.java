package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public static ResourceLocation getId(Class<? extends MPREvent> clazz) {
        for (Map.Entry<ResourceLocation, Class<? extends MPREvent>> entry : EVENTS.entrySet()) {
            if (entry.getValue() == clazz)
                return entry.getKey();
        }
        return null;
    }

    public static void init() {
        register("tick", MPRTickEvent.class);
        register("death", MPRDeathEvent.class);
        register("attacked", MPRAttackedEvent.class);
        register("attack", MPRAttackEvent.class);
    }
}
