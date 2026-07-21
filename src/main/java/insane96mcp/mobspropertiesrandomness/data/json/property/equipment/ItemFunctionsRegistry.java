package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import insane96mcp.mobspropertiesrandomness.data.json.MPRRegistry;

public class ItemFunctionsRegistry {
    public static final MPRRegistry<MPRItemFunction> REGISTRY = new MPRRegistry<>();

    public static void init() {
        REGISTRY.register("set_count", MPRSetCountItemFunction.class);
        REGISTRY.register("set_drop_chance", MPRSetDropChanceItemFunction.class);
        REGISTRY.register("set_component", MPRSetComponentFunction.class);
        REGISTRY.register("add_attribute_modifier", MPRAttributeModifierItemFunction.class);
        REGISTRY.register("enchant", MPREnchantItemFunction.class);
    }
}
