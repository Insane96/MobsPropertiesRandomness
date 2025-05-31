package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRSetCountItemFunction.Serializer.class)
public class MPRSetCountItemFunction extends MPRItemFunction {
    public MPRRange count;

    public MPRSetCountItemFunction(MPRRange count, List<MPRCondition> conditions) {
        super(conditions);
        this.count = count;
    }

    @Override
    protected boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
        stack.setCount(this.count.getIntBetween(living));
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRSetCountItemFunction>, JsonSerializer<MPRSetCountItemFunction> {
        @Override
        public MPRSetCountItemFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRSetCountItemFunction(context.deserialize(jObject.get("count"), MPRRange.class), MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRSetCountItemFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("count", context.serialize(src.count));
            return src.endSerialization(jObject, context);
        }
    }
}
