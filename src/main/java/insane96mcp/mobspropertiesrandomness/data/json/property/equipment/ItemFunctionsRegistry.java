package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ItemFunctionsRegistry {
    public static final Map<ResourceLocation, Class<? extends MPRItemFunction>> FUNCTIONS = new HashMap<>();

    /// Use you own namespace
    private static void register(String id, Class<? extends MPRItemFunction> clazz) {
        FUNCTIONS.put(MPR.location(id), clazz);
    }

    public static Class<? extends MPRItemFunction> get(ResourceLocation id) {
        return FUNCTIONS.get(id);
    }

    public static ResourceLocation get(Class<? extends MPRItemFunction> clazz) {
        for (Map.Entry<ResourceLocation, Class<? extends MPRItemFunction>> entry : FUNCTIONS.entrySet()) {
            if (entry.getValue() == clazz)
                return entry.getKey();
        }
        return null;
    }

    public static void init() {
        register("set_count", MPRSetCountItemFunction.class);
        register("set_drop_chance", MPRSetDropChanceItemFunction.class);
        register("set_nbt", MPRNBTItemFunction.class);
        register("set_raw_nbt", MPRRawNBTItemFunction.class);
        register("add_attribute_modifier", MPRAttributeModifierItemFunction.class);
        register("enchant", MPREnchantItemFunction.class);
    }
}
