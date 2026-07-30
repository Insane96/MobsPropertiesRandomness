package insane96mcp.mobspropertiesrandomness.data.json;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MPRRegistry<T> {
    /// Set to true once this registry has registered all of {@value MPR#MOD_ID}'s own entries. From then on, the {@value MPR#MOD_ID} namespace is off-limits to new registrations on this registry.
    private boolean mprNamespaceLocked = false;

    private final Class<T> type;
    private final Map<ResourceLocation, Class<? extends T>> entries = new HashMap<>();

    public MPRRegistry(Class<T> type) {
        this.type = type;
    }

    /// Use your own mod's namespace. The {@value MPR#MOD_ID} namespace is reserved for this mod's own entries.
    ///
    /// Validates that {@code clazz} actually extends this registry's type at runtime, even if the caller went
    /// through an unchecked cast (e.g. {@code MPRRegisterEvent#register}) to get here.
    public void register(ResourceLocation id, Class<? extends T> clazz) {
        if (!type.isAssignableFrom(clazz))
            throw new IllegalArgumentException("Cannot register '" + id + "': " + clazz.getName() + " does not extend " + type.getName());
        if (id.getNamespace().equals(MPR.MOD_ID) && mprNamespaceLocked)
            throw new IllegalArgumentException("Cannot register '" + id + "': the '" + MPR.MOD_ID + "' namespace is reserved for " + MPR.MOD_ID + " itself. Use your own mod's namespace instead.");
        entries.put(id, clazz);
    }

    /// Shorthand for {@link #register(ResourceLocation, Class)} with this mod's own namespace.
    public void register(String path, Class<? extends T> clazz) {
        register(MPR.id(path), clazz);
    }

    /// Called by each registry's own init() once it has registered all of {@value MPR#MOD_ID}'s own entries.
    public void lockMprNamespace() {
        mprNamespaceLocked = true;
    }

    @Nullable
    public Class<? extends T> get(ResourceLocation id) {
        return entries.get(id);
    }

    @Nullable
    public ResourceLocation getId(Class<? extends T> clazz) {
        for (Map.Entry<ResourceLocation, Class<? extends T>> entry : entries.entrySet()) {
            if (entry.getValue() == clazz)
                return entry.getKey();
        }
        return null;
    }
}
