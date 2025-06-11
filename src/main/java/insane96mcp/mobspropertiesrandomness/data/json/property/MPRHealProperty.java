package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRHealProperty.Serializer.class)
public class MPRHealProperty extends MPRProperty {
    public MPRRange amount;

    public MPRHealProperty(MPRRange amount, List<MPRCondition> conditions) {
        super(conditions);
        this.amount = amount;
    }

    @Override
    public boolean apply(LivingEntity living) {
        living.heal((float) this.amount.getDoubleBetween(living));
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRHealProperty>, JsonSerializer<MPRHealProperty> {
        @Override
        public MPRHealProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRHealProperty(GsonHelper.getAsObject(jObject, "amount", context, MPRRange.class), MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRHealProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("amount", context.serialize(src.amount));
            return src.endSerialization(jObject, context);
        }
    }

}
