package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRFreezeProperty.Serializer.class)
public class MPRFreezeProperty extends MPRProperty {
    public MPRRange secondsFrozen;
    public boolean additive;

    public MPRFreezeProperty(MPRRange secondsFrozen, boolean additive, List<MPRCondition> conditions) {
        super(conditions);
        this.secondsFrozen = secondsFrozen;
        this.additive = additive;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        int freezeTicks = (int) (this.secondsFrozen.getDoubleBetween(living) * 20);
        if (this.additive)
            freezeTicks += living.getTicksFrozen();
        living.setTicksFrozen(freezeTicks);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRFreezeProperty>, JsonSerializer<MPRFreezeProperty> {
        @Override
        public MPRFreezeProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();

            return new MPRFreezeProperty(GsonHelper.getAsObject(jObject, "seconds_frozen", context, MPRRange.class), GsonHelper.getAsBoolean(jObject, "additive", false), MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRFreezeProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("seconds_frozen", context.serialize(src.secondsFrozen));
            if (src.additive)
                jObject.addProperty("additive", true);
            return src.endSerialization(jObject, context);
        }
    }

}
