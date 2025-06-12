package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.MPRNbt;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRRawNBTItemFunction.Serializer.class)
public class MPRRawNBTItemFunction extends MPRItemFunction {
    public CompoundTag nbt;

    public MPRRawNBTItemFunction(CompoundTag nbt, List<MPRCondition> conditions) {
        super(conditions);
        this.nbt = nbt;
    }

    @Override
    protected boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
        stack.setTag(this.nbt.copy().merge(stack.getOrCreateTag()));
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRRawNBTItemFunction>, JsonSerializer<MPRRawNBTItemFunction> {
        @Override
        public MPRRawNBTItemFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRRawNBTItemFunction(
                    MPRNbt.deserialize(GsonHelper.getAsString(jObject, "nbt", null)),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRRawNBTItemFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.addProperty("nbt", src.nbt.getAsString());
            return src.endSerialization(jObject, context);
        }
    }
}
