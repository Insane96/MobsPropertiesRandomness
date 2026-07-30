package insane96mcp.mobspropertiesrandomness.data.json.property;

import insane96mcp.mobspropertiesrandomness.data.json.MPRRegistry;
import insane96mcp.mobspropertiesrandomness.data.json.property.equipment.MPREquipmentProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.preset.MPRPresetsProperty;
import insane96mcp.mobspropertiesrandomness.event.MPRRegisterEvent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

/// Registry of {@link MPRProperty} types.
///
/// To register your own property type from another mod, subscribe to {@link MPRRegisterEvent}, check for
/// {@link MPRRegisterEvent.Type#PROPERTY}, and call {@link MPRRegisterEvent#register} with your own mod's
/// namespace. This registry's own entries (and the `mobspropertiesrandomness` namespace) are always locked
/// before the event fires, so registering in response to it is safe regardless of mod load order.
public class PropertiesRegistry {
    private static final MPRRegistry<MPRProperty> REGISTRY = new MPRRegistry<>(MPRProperty.class);

    public static void init() {
        REGISTRY.register("attribute_modifier", MPRAttributeModifierProperty.class);
        REGISTRY.register("boss_bar", MPRBossBarProperty.class);
        REGISTRY.register("custom_name", MPRCustomNameProperty.class);
        REGISTRY.register("despawn", MPRDespawnProperty.class);
        REGISTRY.register("damage", MPRDamageProperty.class);
        REGISTRY.register("damage_immunity", MPRDamageImmunityProperty.class);
        REGISTRY.register("effect_immunity", MPREffectImmunityProperty.class);
        REGISTRY.register("equipment", MPREquipmentProperty.class);
        REGISTRY.register("experience_multiplier", MPRExperienceMultiplierProperty.class);
        REGISTRY.register("event", MPREventProperty.class);
        REGISTRY.register("fire", MPRFireProperty.class);
        REGISTRY.register("freeze", MPRFreezeProperty.class);
        REGISTRY.register("function", MPRFunctionProperty.class);
        REGISTRY.register("heal", MPRHealProperty.class);
        REGISTRY.register("loot_table", MPRLootTableProperty.class);
        REGISTRY.register("nbt", MPRNBTProperty.class);
        REGISTRY.register("potion_effect", MPRPotionEffectProperty.class);
        REGISTRY.register("play_sound", MPRPlaySoundProperty.class);
        REGISTRY.register("presets", MPRPresetsProperty.class);
        REGISTRY.register("raw_nbt", MPRRawNBTProperty.class);
        REGISTRY.register("silent", MPRSilentProperty.class);
        REGISTRY.register("team", MPRTeamProperty.class);
        REGISTRY.lockMprNamespace();
        NeoForge.EVENT_BUS.post(new MPRRegisterEvent(MPRRegisterEvent.Type.PROPERTY, REGISTRY));
    }

    @Nullable
    static Class<? extends MPRProperty> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    @Nullable
    static ResourceLocation getId(Class<? extends MPRProperty> clazz) {
        return REGISTRY.getId(clazz);
    }
}
