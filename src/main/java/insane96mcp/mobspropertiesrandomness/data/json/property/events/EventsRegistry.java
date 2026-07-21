package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import insane96mcp.mobspropertiesrandomness.data.json.MPRRegistry;

public class EventsRegistry {
    public static final MPRRegistry<MPREvent> REGISTRY = new MPRRegistry<>();

    public static void init() {
        REGISTRY.register("tick", MPRTickEvent.class);
        REGISTRY.register("death", MPRDeathEvent.class);
        REGISTRY.register("damaged", MPRDamagedEvent.class);
        REGISTRY.register("attack", MPRAttackEvent.class);
        REGISTRY.register("kill", MPRKillEvent.class);
        REGISTRY.register("change_target", MPRChangeTargetEvent.class);
    }
}
