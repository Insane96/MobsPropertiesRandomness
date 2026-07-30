package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import insane96mcp.mobspropertiesrandomness.data.json.MPRRegistry;
import insane96mcp.mobspropertiesrandomness.event.MPRRegisterEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

/// Registry of {@link MPREvent} types.
///
/// To register your own event type from another mod, subscribe to {@link MPRRegisterEvent}, check for
/// {@link MPRRegisterEvent.Type#EVENT}, and call {@link MPRRegisterEvent#register} with your own mod's
/// namespace. This registry's own entries (and the `mobspropertiesrandomness` namespace) are always locked
/// before the event fires, so registering in response to it is safe regardless of mod load order.
public class EventsRegistry {
    private static final MPRRegistry<MPREvent> REGISTRY = new MPRRegistry<>(MPREvent.class);

    public static void init() {
        REGISTRY.register("tick", MPRTickEvent.class);
        REGISTRY.register("death", MPRDeathEvent.class);
        REGISTRY.register("damaged", MPRDamagedEvent.class);
        REGISTRY.register("attack", MPRAttackEvent.class);
        REGISTRY.register("kill", MPRKillEvent.class);
        REGISTRY.register("change_target", MPRChangeTargetEvent.class);
        REGISTRY.lockMprNamespace();
        NeoForge.EVENT_BUS.post(new MPRRegisterEvent(MPRRegisterEvent.Type.EVENT, REGISTRY));
    }

    @Nullable
    static Class<? extends MPREvent> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    @Nullable
    static ResourceLocation getId(Class<? extends MPREvent> clazz) {
        return REGISTRY.getId(clazz);
    }
}
