package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.MPRNbt;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRNBTCondition.Serializer.class)
public class MPRNBTCondition extends MPRCondition {
    public MPRNbt nbt;

    public MPRNBTCondition(MPRNbt nbt, boolean inverted) {
        super(inverted);
        this.nbt = nbt;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        CompoundTag mobTag = new CompoundTag();
        living.saveWithoutId(mobTag);
        String[] splitPath = this.nbt.path.split("\\.");
        for (int i = 0; i < splitPath.length; i++) {
            Tag tag = mobTag.get(splitPath[i]);
            if (tag == null)
                return false;
            if (i < splitPath.length - 1) {
                if (tag instanceof CompoundTag compoundTag)
                    mobTag = compoundTag;
                else
                    return false;
            }
            else {
                switch (this.nbt.type) {
                    case DOUBLE -> {
                        if (tag instanceof NumericTag numericTag)
                            return this.nbt.value.isBetween(living, numericTag.getAsDouble());
                        return false;
                    }
                    case INTEGER -> {
                        if (tag instanceof NumericTag numericTag)
                            return this.nbt.value.isBetween(living, numericTag.getAsInt());
                        return false;
                    }
                    case BOOLEAN -> {
                        if (tag instanceof ByteTag byteTag)
                            return this.nbt.value.isBetween(living, byteTag.getAsByte());
                        return false;
                    }
                    case STRING -> {
                        if (tag instanceof StringTag stringTag)
                            return this.nbt.stringValue.equals(stringTag.getAsString());
                        return false;
                    }
                }
            }

        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<MPRNBTCondition>, JsonSerializer<MPRNBTCondition> {
        @Override
        public MPRNBTCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRNBTCondition(
                    context.deserialize(jObject, MPRNbt.class),
                    MPRCondition.deserializeInverted(jObject)
            );
        }

        @Override
        public JsonElement serialize(MPRNBTCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.nbt).getAsJsonObject();
            return src.endSerialization(jObject);
        }
    }

}
