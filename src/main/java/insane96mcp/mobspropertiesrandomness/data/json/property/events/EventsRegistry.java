package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

/// Registry of {@link MPREvent} types.
///
/// To register your own event type from another mod, use {@link #REGISTRY_KEY} with a {@code RegisterEvent}
/// (or a {@code DeferredRegister}) on your mod's event bus, exactly like registering any other NeoForge registry
/// entry (e.g. blocks or items).
public class EventsRegistry {
    public static final ResourceKey<Registry<Class<? extends MPREvent>>> REGISTRY_KEY =
            ResourceKey.createRegistryKey(MPR.id("events"));

    private static final Registry<Class<? extends MPREvent>> REGISTRY =
            new RegistryBuilder<>(REGISTRY_KEY).create();

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener((NewRegistryEvent event) -> event.register(REGISTRY));
        modEventBus.addListener(EventsRegistry::registerDefaults);
    }

    private static void registerDefaults(RegisterEvent event) {
        event.register(REGISTRY_KEY, helper -> {
            helper.register(MPR.id("tick"), MPRTickEvent.class);
            helper.register(MPR.id("death"), MPRDeathEvent.class);
            helper.register(MPR.id("damaged"), MPRDamagedEvent.class);
            helper.register(MPR.id("attack"), MPRAttackEvent.class);
            helper.register(MPR.id("kill"), MPRKillEvent.class);
            helper.register(MPR.id("change_target"), MPRChangeTargetEvent.class);
        });
    }

    @Nullable
    static Class<? extends MPREvent> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    @Nullable
    static ResourceLocation getId(Class<? extends MPREvent> clazz) {
        return REGISTRY.getKey(clazz);
    }
}
