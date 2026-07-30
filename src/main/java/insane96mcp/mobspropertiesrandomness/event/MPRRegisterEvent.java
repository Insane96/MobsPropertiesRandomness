package insane96mcp.mobspropertiesrandomness.event;

import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.MPRRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

/// Fired once per {@link Type} on {@link NeoForge#EVENT_BUS}, immediately after the matching registry has
/// registered and locked all of {@value MPR#MOD_ID}'s own entries, during {@code FMLCommonSetupEvent}.
///
/// Other mods should register their own entries by subscribing to this event and calling {@link #register}
/// with their own mod's namespace — instead of registering from their own mod constructor. Mod construction
/// order between different mods isn't guaranteed, but this event is only ever fired after the matching registry
/// has already locked the {@code mobspropertiesrandomness} namespace, so registering in response to it is
/// always safe.
///
/// It's up to the listener to check {@link #getType()} first and pass a class that actually extends the right
/// base type (e.g. {@code MPRProperty} for {@link Type#PROPERTY}) — {@link #register} doesn't know about
/// {@link #getType()} itself. It does still validate against the registry's own type at runtime, though, so a
/// mismatched class fails immediately with an {@code IllegalArgumentException}.
///
/// ```java
/// @SubscribeEvent
/// public void onMPRRegister(MPRRegisterEvent event) {
///     if (event.getType() == MPRRegisterEvent.Type.PROPERTY)
///         event.register(ResourceLocation.fromNamespaceAndPath("mymod", "my_property"), MyProperty.class);
/// }
/// ```
public class MPRRegisterEvent extends Event {
    public enum Type {
        CONDITION,
        PROPERTY,
        ITEM_FUNCTION,
        MODIFIER,
        EVENT
    }

    private final Type type;
    private final MPRRegistry<?> registry;

    public MPRRegisterEvent(Type type, MPRRegistry<?> registry) {
        this.type = type;
        this.registry = registry;
    }

    public Type getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public void register(ResourceLocation id, Class<?> clazz) {
        ((MPRRegistry<Object>) registry).register(id, clazz);
    }
}
