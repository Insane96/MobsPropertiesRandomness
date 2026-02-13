package insane96mcp.mobspropertiesrandomness.data.json.property;

import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.property.equipment.MPREquipmentProperty;
import insane96mcp.mobspropertiesrandomness.data.json.property.preset.MPRPresetsProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.util.HashMap;
import java.util.Map;

public class PropertiesRegistry {
    public static final Map<ResourceLocation, Class<? extends MPRProperty>> PROPERTIES = new HashMap<>();

    /// Use you own namespace
    private static void register(String id, Class<? extends MPRProperty> clazz) {
        PROPERTIES.put(MPR.location(id), clazz);
    }

    public static Class<? extends MPRProperty> get(ResourceLocation id) {
        return PROPERTIES.get(id);
    }

    public static ResourceLocation get(Class<? extends MPRProperty> clazz) {
        for (Map.Entry<ResourceLocation, Class<? extends MPRProperty>> entry : PROPERTIES.entrySet()) {
            if (entry.getValue() == clazz)
                return entry.getKey();
        }
        return null;
    }

    public static void init() {
        register("attribute_modifier", MPRAttributeModifierProperty.class);
        register("boss_bar", MPRBossBarProperty.class);
        register("custom_name", MPRCustomNameProperty.class);
        register("despawn", MPRDespawnProperty.class);
        register("damage", MPRDamageProperty.class);
        register("damage_immunity", MPRDamageImmunityProperty.class);
        register("effect_immunity", MPREffectImmunityProperty.class);
        register("equipment", MPREquipmentProperty.class);
        register("experience_multiplier", MPRExperienceMultiplierProperty.class);
        register("event", MPREventProperty.class);
        register("fire", MPRFireProperty.class);
        register("freeze", MPRFreezeProperty.class);
        register("function", MPRFunctionProperty.class);
        register("heal", MPRHealProperty.class);
        register("loot_table", MPRLootTableProperty.class);
        register("nbt", MPRNBTProperty.class);
        register("potion_effect", MPRPotionEffectProperty.class);
        register("play_sound", MPRPlaySoundProperty.class);
        register("presets", MPRPresetsProperty.class);
        register("raw_nbt", MPRRawNBTProperty.class);
        register("silent", MPRSilentProperty.class);
        register("team", MPRTeamProperty.class);
        if (ModList.get().isLoaded("pehkui"))
            register("scale", MPRScalePehkuiProperty.class);
    }
}
