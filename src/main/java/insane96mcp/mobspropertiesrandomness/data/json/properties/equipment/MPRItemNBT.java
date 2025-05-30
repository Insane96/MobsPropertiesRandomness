package insane96mcp.mobspropertiesrandomness.data.json.properties.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.MPRNBT;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRItemNBT.Serializer.class)
public class MPRItemNBT extends MPRConditionable {
    public MPRNBT nbt;

    public MPRItemNBT(MPRNBT nbt,List<MPRCondition> conditions) {
        super(conditions);
        this.nbt = nbt;
    }

    public void setStackNBT(LivingEntity living, ItemStack stack) {
        CompoundTag stackTag = stack.getOrCreateTag();
        String[] splitPath = this.nbt.path.split("\\.");
        CompoundTag innerCompoundTag = stackTag;
        for (int i = 0; i < splitPath.length; i++) {
            if (i < splitPath.length - 1) {
                Tag tag = innerCompoundTag.get(splitPath[i]);
                if (tag instanceof CompoundTag compoundTag)
                    innerCompoundTag = compoundTag;
                else {
                    CompoundTag newCompoundTag = new CompoundTag();
                    innerCompoundTag.put(splitPath[i], newCompoundTag);
                    innerCompoundTag = newCompoundTag;
                }
            }
            else {
                switch (this.nbt.type) {
                    case DOUBLE -> innerCompoundTag.putDouble(splitPath[i], this.nbt.value.getFloatBetween(living));
                    case INTEGER -> innerCompoundTag.putInt(splitPath[i], this.nbt.value.getIntBetween(living));
                    case BOOLEAN -> innerCompoundTag.putBoolean(splitPath[i], living.getRandom().nextFloat() < this.nbt.value.getFloatBetween(living));
                }
            }
        }
    }

    public static class Serializer implements JsonDeserializer<MPRItemNBT>, JsonSerializer<MPRItemNBT> {
        @Override
        public MPRItemNBT deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRItemNBT(
                    context.deserialize(jObject, MPRNBT.class),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRItemNBT src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.nbt).getAsJsonObject();
            return src.endSerialization(jObject, context);
        }
    }
}
