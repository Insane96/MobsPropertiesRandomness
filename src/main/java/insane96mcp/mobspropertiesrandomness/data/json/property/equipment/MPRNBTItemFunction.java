package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.MPRNbt;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRNBTItemFunction.Serializer.class)
public class MPRNBTItemFunction extends MPRItemFunction {
    public MPRNbt nbt;

    public MPRNBTItemFunction(MPRNbt nbt, List<MPRCondition> conditions) {
        super(conditions);
        this.nbt = nbt;
    }

    @Override
    protected boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
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
                    case DOUBLE -> innerCompoundTag.putDouble(splitPath[i], this.nbt.value.getDoubleBetween(living));
                    case INTEGER -> innerCompoundTag.putInt(splitPath[i], this.nbt.value.getIntBetween(living));
                    case BOOLEAN -> innerCompoundTag.putBoolean(splitPath[i], living.getRandom().nextFloat() < this.nbt.value.getDoubleBetween(living));
                }
            }
        }
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRNBTItemFunction>, JsonSerializer<MPRNBTItemFunction> {
        @Override
        public MPRNBTItemFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRNBTItemFunction(
                    context.deserialize(jObject, MPRNbt.class),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRNBTItemFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.nbt).getAsJsonObject();
            return src.endSerialization(jObject, context);
        }
    }
}
