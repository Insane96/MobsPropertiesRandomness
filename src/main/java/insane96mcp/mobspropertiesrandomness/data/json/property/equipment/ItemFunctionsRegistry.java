package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import insane96mcp.mobspropertiesrandomness.data.json.MPRRegistry;
import insane96mcp.mobspropertiesrandomness.event.MPRRegisterEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

/// Registry of {@link MPRItemFunction} types.
///
/// To register your own item function type from another mod, subscribe to {@link MPRRegisterEvent}, check for
/// {@link MPRRegisterEvent.Type#ITEM_FUNCTION}, and call {@link MPRRegisterEvent#register} with your own mod's
/// namespace. This registry's own entries (and the `mobspropertiesrandomness` namespace) are always locked
/// before the event fires, so registering in response to it is safe regardless of mod load order.
public class ItemFunctionsRegistry {
    private static final MPRRegistry<MPRItemFunction> REGISTRY = new MPRRegistry<>(MPRItemFunction.class);

    public static void init() {
        REGISTRY.register("set_count", MPRSetCountItemFunction.class);
        REGISTRY.register("set_drop_chance", MPRSetDropChanceItemFunction.class);
        REGISTRY.register("set_component", MPRSetComponentFunction.class);
        REGISTRY.register("add_attribute_modifier", MPRAttributeModifierItemFunction.class);
        REGISTRY.register("enchant", MPREnchantItemFunction.class);
        REGISTRY.lockMprNamespace();
        NeoForge.EVENT_BUS.post(new MPRRegisterEvent(MPRRegisterEvent.Type.ITEM_FUNCTION, REGISTRY));
    }

    @Nullable
    static Class<? extends MPRItemFunction> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    @Nullable
    static ResourceLocation getId(Class<? extends MPRItemFunction> clazz) {
        return REGISTRY.getId(clazz);
    }
}
