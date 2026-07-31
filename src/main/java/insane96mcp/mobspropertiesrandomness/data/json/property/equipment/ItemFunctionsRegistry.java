package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import insane96mcp.mobspropertiesrandomness.MPR;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

/// Registry of {@link MPRItemFunction} types.
///
/// To register your own item function type from another mod, use {@link #REGISTRY_KEY} with a {@code RegisterEvent}
/// (or a {@code DeferredRegister}) on your mod's event bus, exactly like registering any other NeoForge registry
/// entry (e.g. blocks or items).
public class ItemFunctionsRegistry {
    public static final ResourceKey<Registry<Class<? extends MPRItemFunction>>> REGISTRY_KEY =
            ResourceKey.createRegistryKey(MPR.id("item_functions"));

    private static final Registry<Class<? extends MPRItemFunction>> REGISTRY =
            new RegistryBuilder<>(REGISTRY_KEY).create();

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener((NewRegistryEvent event) -> event.register(REGISTRY));
        modEventBus.addListener(ItemFunctionsRegistry::registerDefaults);
    }

    private static void registerDefaults(RegisterEvent event) {
        event.register(REGISTRY_KEY, helper -> {
            helper.register(MPR.id("set_count"), MPRSetCountItemFunction.class);
            helper.register(MPR.id("set_drop_chance"), MPRSetDropChanceItemFunction.class);
            helper.register(MPR.id("set_component"), MPRSetComponentFunction.class);
            helper.register(MPR.id("add_attribute_modifier"), MPRAttributeModifierItemFunction.class);
            helper.register(MPR.id("enchant"), MPREnchantItemFunction.class);
        });
    }

    @Nullable
    static Class<? extends MPRItemFunction> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    @Nullable
    static ResourceLocation getId(Class<? extends MPRItemFunction> clazz) {
        return REGISTRY.getKey(clazz);
    }
}
