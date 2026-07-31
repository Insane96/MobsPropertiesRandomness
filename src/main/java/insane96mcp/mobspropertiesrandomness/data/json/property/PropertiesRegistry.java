package insane96mcp.mobspropertiesrandomness.data.json.property;

import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.property.equipment.MPREquipmentProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.preset.MPRPresetsProperty;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

/// Registry of {@link MPRProperty} types.
///
/// To register your own property type from another mod, use {@link #REGISTRY_KEY} with a {@code RegisterEvent}
/// (or a {@code DeferredRegister}) on your mod's event bus, exactly like registering any other NeoForge registry
/// entry (e.g. blocks or items).
public class PropertiesRegistry {
    public static final ResourceKey<Registry<Class<? extends MPRProperty>>> REGISTRY_KEY =
            ResourceKey.createRegistryKey(MPR.id("properties"));

    private static final Registry<Class<? extends MPRProperty>> REGISTRY =
            new RegistryBuilder<>(REGISTRY_KEY).create();

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener((NewRegistryEvent event) -> event.register(REGISTRY));
        modEventBus.addListener(PropertiesRegistry::registerDefaults);
    }

    private static void registerDefaults(RegisterEvent event) {
        event.register(REGISTRY_KEY, helper -> {
            helper.register(MPR.id("attribute_modifier"), MPRAttributeModifierProperty.class);
            helper.register(MPR.id("boss_bar"), MPRBossBarProperty.class);
            helper.register(MPR.id("custom_name"), MPRCustomNameProperty.class);
            helper.register(MPR.id("despawn"), MPRDespawnProperty.class);
            helper.register(MPR.id("damage"), MPRDamageProperty.class);
            helper.register(MPR.id("damage_immunity"), MPRDamageImmunityProperty.class);
            helper.register(MPR.id("effect_immunity"), MPREffectImmunityProperty.class);
            helper.register(MPR.id("equipment"), MPREquipmentProperty.class);
            helper.register(MPR.id("experience_multiplier"), MPRExperienceMultiplierProperty.class);
            helper.register(MPR.id("event"), MPREventProperty.class);
            helper.register(MPR.id("fire"), MPRFireProperty.class);
            helper.register(MPR.id("freeze"), MPRFreezeProperty.class);
            helper.register(MPR.id("function"), MPRFunctionProperty.class);
            helper.register(MPR.id("heal"), MPRHealProperty.class);
            helper.register(MPR.id("loot_table"), MPRLootTableProperty.class);
            helper.register(MPR.id("nbt"), MPRNBTProperty.class);
            helper.register(MPR.id("potion_effect"), MPRPotionEffectProperty.class);
            helper.register(MPR.id("play_sound"), MPRPlaySoundProperty.class);
            helper.register(MPR.id("presets"), MPRPresetsProperty.class);
            helper.register(MPR.id("raw_nbt"), MPRRawNBTProperty.class);
            helper.register(MPR.id("remove_attribute_modifiers"), MPRRemoveAttributeModifiersProperty.class);
            helper.register(MPR.id("remove_potion_effects"), MPRRemovePotionEffectsProperty.class);
            helper.register(MPR.id("silent"), MPRSilentProperty.class);
            helper.register(MPR.id("team"), MPRTeamProperty.class);
        });
    }

    @Nullable
    static Class<? extends MPRProperty> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    @Nullable
    static ResourceLocation getId(Class<? extends MPRProperty> clazz) {
        return REGISTRY.getKey(clazz);
    }
}
