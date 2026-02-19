package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import insane96mcp.mobspropertiesrandomness.mixin.accessor.DamageSourcesAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRDamageProperty.Serializer.class)
public class MPRDamageProperty extends MPRProperty {
    public MPRRange amount;
    public ResourceKey<DamageType> damageType;

    public MPRDamageProperty(MPRRange amount, ResourceKey<DamageType> damageType, List<MPRCondition> conditions) {
        super(conditions);
        this.amount = amount;
        this.damageType = damageType;
    }

    @Override
    public boolean apply(LivingEntity living) {
        living.hurt(((DamageSourcesAccessor)living.damageSources()).invokeSource(damageType), (float) this.amount.getDoubleBetween(living));
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRDamageProperty>, JsonSerializer<MPRDamageProperty> {
        @Override
        public MPRDamageProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            String s = GsonHelper.getAsString(jObject, "damage_type", null);
            ResourceKey<DamageType> damageType;
            if (s == null)
                damageType = DamageTypes.GENERIC_KILL;
            else
                damageType = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(s));
            return new MPRDamageProperty(
                    GsonHelper.getAsObject(jObject, "amount", context, MPRRange.class),
                    damageType,
                    MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRDamageProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("amount", context.serialize(src.amount));
            return src.endSerialization(jObject, context);
        }
    }

}
