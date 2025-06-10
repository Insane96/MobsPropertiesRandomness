package insane96mcp.mobspropertiesrandomness.data.json.property.events;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.Nullable;

public class MPRHurtData {
    @Nullable
    private final DirectIndirect directIndirect;
    @Nullable
    private final ResourceKey<DamageType> damageType;
    @Nullable
    private final TagKey<DamageType> damageTypeTag;

    public MPRHurtData(@Nullable DirectIndirect directIndirect, @Nullable ResourceKey<DamageType> damageType, @Nullable TagKey<DamageType> damageTypeTag) {
        this.directIndirect = directIndirect;
        this.damageType = damageType;
        this.damageTypeTag = damageTypeTag;
    }

    public boolean shouldApply(DamageSource source, boolean isDirectDamage) {
        if (this.directIndirect != null
            && ((isDirectDamage && this.directIndirect == MPRHurtData.DirectIndirect.INDIRECT)
            || (!isDirectDamage && this.directIndirect == MPRHurtData.DirectIndirect.DIRECT)))
            return false;

        if ((this.damageType != null && !source.is(this.damageType))
                || (this.damageTypeTag != null && !source.is(this.damageTypeTag)))
            return false;

        return true;
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
                damageTypeTag
        );
    }

    public enum DirectIndirect {
        @SerializedName("direct")
        DIRECT,
        @SerializedName("indirect")
        INDIRECT
    }
}
