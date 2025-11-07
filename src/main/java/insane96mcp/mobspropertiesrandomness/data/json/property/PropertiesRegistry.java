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
        register("potion_effect", MPRPotionEffectProperty.class);
        register("attribute_modifier", MPRAttributeModifierProperty.class);
        register("custom_name", MPRCustomNameProperty.class);
        register("silent", MPRSilentProperty.class);
        register("experience_multiplier", MPRExperienceMultiplierProperty.class);
        register("loot_table", MPRLootTableProperty.class);
        register("effect_immunity", MPREffectImmunityProperty.class);
        register("team", MPRTeamProperty.class);
        register("set_nbt", MPRNBTProperty.class);
        register("set_raw_nbt", MPRRawNBTProperty.class);
        register("equipment", MPREquipmentProperty.class);
        register("boss_bar", MPRBossBarProperty.class);
        register("play_sound", MPRPlaySoundProperty.class);
        register("fire", MPRFireProperty.class);
        register("freeze", MPRFreezeProperty.class);
        register("heal", MPRHealProperty.class);
        register("damage", MPRDamageProperty.class);
        register("function", MPRFunctionProperty.class);
        register("event", MPREventProperty.class);
        register("presets", MPRPresetsProperty.class);
        if (ModList.get().isLoaded("pehkui"))
            register("scale", MPRScalePehkuiProperty.class);
    }
}
