package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import insane96mcp.mobspropertiesrandomness.data.json.MPRRegistry;
import insane96mcp.mobspropertiesrandomness.event.MPRRegisterEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

/// Registry of {@link MPRModifier} types.
///
/// To register your own modifier type from another mod, subscribe to {@link MPRRegisterEvent}, check for
/// {@link MPRRegisterEvent.Type#MODIFIER}, and call {@link MPRRegisterEvent#register} with your own mod's
/// namespace. This registry's own entries (and the `mobspropertiesrandomness` namespace) are always locked
/// before the event fires, so registering in response to it is safe regardless of mod load order.
public class ModifiersRegistry {
    private static final MPRRegistry<MPRModifier> REGISTRY = new MPRRegistry<>(MPRModifier.class);

    public static void init() {
        REGISTRY.register("difficulty", MPRDifficultyModifier.class);
        REGISTRY.register("deepness", MPRDeepnessModifier.class);
        REGISTRY.register("time_played", MPRTimePlayedModifier.class);
        REGISTRY.register("distance_from_spawn", MPRDistanceFromSpawnModifier.class);
        REGISTRY.register("condition", MPRConditionModifier.class);
        REGISTRY.lockMprNamespace();
        NeoForge.EVENT_BUS.post(new MPRRegisterEvent(MPRRegisterEvent.Type.MODIFIER, REGISTRY));
    }

    @Nullable
    static Class<? extends MPRModifier> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    @Nullable
    static ResourceLocation getId(Class<? extends MPRModifier> clazz) {
        return REGISTRY.getId(clazz);
    }
}
