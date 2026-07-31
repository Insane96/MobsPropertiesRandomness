package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

/// Registry of {@link MPRModifier} types.
///
/// To register your own modifier type from another mod, use {@link #REGISTRY_KEY} with a {@code RegisterEvent}
/// (or a {@code DeferredRegister}) on your mod's event bus, exactly like registering any other NeoForge registry
/// entry (e.g. blocks or items).
public class ModifiersRegistry {
    public static final ResourceKey<Registry<Class<? extends MPRModifier>>> REGISTRY_KEY =
            ResourceKey.createRegistryKey(MPR.id("modifiers"));

    private static final Registry<Class<? extends MPRModifier>> REGISTRY =
            new RegistryBuilder<>(REGISTRY_KEY).create();

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener((NewRegistryEvent event) -> event.register(REGISTRY));
        modEventBus.addListener(ModifiersRegistry::registerDefaults);
    }

    private static void registerDefaults(RegisterEvent event) {
        event.register(REGISTRY_KEY, helper -> {
            helper.register(MPR.id("difficulty"), MPRDifficultyModifier.class);
            helper.register(MPR.id("deepness"), MPRDeepnessModifier.class);
            helper.register(MPR.id("time_played"), MPRTimePlayedModifier.class);
            helper.register(MPR.id("distance_from_spawn"), MPRDistanceFromSpawnModifier.class);
            helper.register(MPR.id("condition"), MPRConditionModifier.class);
        });
    }

    @Nullable
    static Class<? extends MPRModifier> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    @Nullable
    static ResourceLocation getId(Class<? extends MPRModifier> clazz) {
        return REGISTRY.getKey(clazz);
    }
}
