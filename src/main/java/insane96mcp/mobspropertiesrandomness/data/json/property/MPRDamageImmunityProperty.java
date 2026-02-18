package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.core.ModNBTData;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.util.SerializerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonAdapter(MPRDamageImmunityProperty.Serializer.class)
public class MPRDamageImmunityProperty extends MPRProperty {
    private static final ResourceLocation DAMAGE_IMMUNITY = MPR.location("damage_type_immunity");
    private static final ResourceLocation DAMAGE_TAG_IMMUNITY = MPR.location("damage_type_tag_immunity");

    @Nullable
    private final List<ResourceKey<DamageType>> damageTypes;
    @Nullable
    private final TagKey<DamageType> damageTypeTag;
    public boolean remove;

    public MPRDamageImmunityProperty(@Nullable List<ResourceKey<DamageType>> damageTypes, @Nullable TagKey<DamageType> damageTypeTag, boolean remove, List<MPRCondition> conditions) {
        super(conditions);
        this.damageTypes = damageTypes;
        this.damageTypeTag = damageTypeTag;
        this.remove = remove;
    }

    @Override
    public boolean apply(LivingEntity living) {
        if (!this.remove) {
            if (this.damageTypes != null) {
                ListTag listTag = new ListTag();
                for (ResourceKey<DamageType> damageType : this.damageTypes) {
                    listTag.add(StringTag.valueOf(damageType.location().toString()));
                }
                ModNBTData.put(living, DAMAGE_IMMUNITY, listTag);
            }
            if (this.damageTypeTag != null) {
                ModNBTData.put(living, DAMAGE_TAG_IMMUNITY, this.damageTypeTag.location().toString());
            }
        }
        else {
            ModNBTData.remove(living, DAMAGE_IMMUNITY);
            ModNBTData.remove(living, DAMAGE_TAG_IMMUNITY);
        }
        return true;
    }

    public static boolean preventDamage(LivingEntity living, DamageSource damageSource) {
        if (living.level().isClientSide)
            return false;

        if (ModNBTData.contains(living, DAMAGE_IMMUNITY)) {
            ListTag listTag = ModNBTData.getList(living, DAMAGE_IMMUNITY, CompoundTag.TAG_STRING);
            for (int i = 0; i < listTag.size(); ++i) {
                ResourceKey<DamageType> damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(listTag.getString(i)));
                if (damageSource.is(damageType))
                    return true;
            }
        }
        if (ModNBTData.contains(living, DAMAGE_TAG_IMMUNITY)) {
            TagKey<DamageType> damageTypeTag = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(ModNBTData.get(living, DAMAGE_TAG_IMMUNITY, String.class)));
            if (damageSource.is(damageTypeTag))
                return true;
        }

        return false;
    }

    public static class Serializer implements JsonDeserializer<MPRDamageImmunityProperty>, JsonSerializer<MPRDamageImmunityProperty> {
        @Override
        public MPRDamageImmunityProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            List<String> sDamageTypes = SerializerUtils.deserializeList(jObject, "damage_types",  context, String.class, false);
            List<ResourceKey<DamageType>> damageTypes = null;
            if (!sDamageTypes.isEmpty()) {
                damageTypes = new ArrayList<>();
                for (String sDamageType : sDamageTypes) {
                    ResourceKey<DamageType> damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(sDamageType));
                    damageTypes.add(damageType);
                }
            }

            TagKey<DamageType> damageTypeTag = null;
            if (jObject.has("damage_type_tag")) {
                damageTypeTag = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(GsonHelper.getAsString(jObject, "damage_type_tag")));
            }
            return new MPRDamageImmunityProperty(
                    damageTypes,
                    damageTypeTag,
                    GsonHelper.getAsBoolean(jObject, "remove", false),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRDamageImmunityProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            if (src.damageTypes != null && !src.damageTypes.isEmpty()) {
                //TODO
            }
            if (src.damageTypeTag != null)
                jObject.addProperty("damage_type_tag", src.damageTypeTag.location().toString());
            if (src.remove)
                jObject.addProperty("remove", true);
            return src.endSerialization(jObject, context);
        }
    }
}
