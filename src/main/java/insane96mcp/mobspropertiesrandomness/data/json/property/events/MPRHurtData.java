package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class MPRHurtData {
    @Nullable
    private final DirectIndirect directIndirect;
    @Nullable
    private final ResourceKey<DamageType> damageType;
    @Nullable
    private final TagKey<DamageType> damageTypeTag;
    /// If true, the damage must have been caused by a living entity (e.g. ignores fall damage, lava, /kill, etc.)
    private final boolean requireEntity;

    public MPRHurtData(@Nullable DirectIndirect directIndirect, @Nullable ResourceKey<DamageType> damageType, @Nullable TagKey<DamageType> damageTypeTag, boolean requireEntity) {
        this.directIndirect = directIndirect;
        this.damageType = damageType;
        this.damageTypeTag = damageTypeTag;
        this.requireEntity = requireEntity;
    }

    public boolean shouldApply(DamageSource source, boolean isDirectDamage) {
        if (this.requireEntity && !(source.getEntity() instanceof LivingEntity))
            return false;

        if (this.directIndirect != null
            && ((isDirectDamage && this.directIndirect == DirectIndirect.INDIRECT)
            || (!isDirectDamage && this.directIndirect == DirectIndirect.DIRECT)))
            return false;

        if ((this.damageType != null && !source.is(this.damageType))
                || (this.damageTypeTag != null && !source.is(this.damageTypeTag)))
            return false;

        return true;
    }

    /// Hurt data fields are written directly in the event object, the same way they are read
    public void serialize(JsonObject jObject, JsonSerializationContext context) {
        if (this.directIndirect != null)
            jObject.add("direct_indirect", context.serialize(this.directIndirect));
        if (this.damageType != null)
            jObject.addProperty("damage_type", this.damageType.location().toString());
        else if (this.damageTypeTag != null)
            jObject.addProperty("damage_type", "#" + this.damageTypeTag.location());
        if (this.requireEntity)
            jObject.addProperty("require_entity", true);
    }

    public static MPRHurtData deserialize(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
        String s = GsonHelper.getAsString(json, "damage_type", null);
        ResourceKey<DamageType> damageType = null;
        TagKey<DamageType> damageTypeTag = null;
        if (s != null) {
            if (s.startsWith("#")) {
                ResourceLocation rl = ResourceLocation.parse(s.substring(1));
                damageTypeTag = TagKey.create(Registries.DAMAGE_TYPE, rl);
            }
            else {
                ResourceLocation rl = ResourceLocation.parse(s);
                damageType = ResourceKey.create(Registries.DAMAGE_TYPE, rl);
            }
        }
        return new MPRHurtData(
                GsonHelper.getAsObject(json, "direct_indirect", null, context, DirectIndirect.class),
                damageType,
                damageTypeTag,
                GsonHelper.getAsBoolean(json, "require_entity", false)
        );
    }

    public enum DirectIndirect {
        @SerializedName("direct")
        DIRECT,
        @SerializedName("indirect")
        INDIRECT
    }
}
