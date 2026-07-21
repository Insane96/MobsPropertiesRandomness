package insane96mcp.mobspropertiesrandomness.data.json;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MPRRegistry<T> {
    private final Map<ResourceLocation, Class<? extends T>> entries = new HashMap<>();

    public void register(String id, Class<? extends T> clazz) {
        entries.put(MPR.id(id), clazz);
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
