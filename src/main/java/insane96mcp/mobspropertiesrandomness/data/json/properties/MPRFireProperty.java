package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRFireProperty.Serializer.class)
public class MPRFireProperty extends MPRProperty {
    public MPRRange secondsOnFire;
    public boolean additive;

    public MPRFireProperty(MPRRange secondsOnFire, boolean additive, List<MPRCondition> conditions) {
        super(conditions);
        this.secondsOnFire = secondsOnFire;
        this.additive = additive;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        int fireTicks = (int) (this.secondsOnFire.getDoubleBetween(living) * 20);
        if (this.additive)
            fireTicks += living.getRemainingFireTicks();
        living.setRemainingFireTicks(fireTicks);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRFireProperty>, JsonSerializer<MPRFireProperty> {
        @Override
        public MPRFireProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();

            return new MPRFireProperty(GsonHelper.getAsObject(jObject, "seconds_on_fire", context, MPRRange.class), GsonHelper.getAsBoolean(jObject, "additive", false), MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRFireProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("seconds_on_fire", context.serialize(src.secondsOnFire));
            if (src.additive)
                jObject.addProperty("additive", true);
            return src.endSerialization(jObject, context);
        }
    }

}
