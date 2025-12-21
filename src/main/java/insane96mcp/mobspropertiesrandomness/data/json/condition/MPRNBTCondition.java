package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.NBTType;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRNBTCondition.Serializer.class)
public class MPRNBTCondition extends MPRCondition {
    public String path;
    public NBTType type;
    public MPRRange value;

    public MPRNBTCondition(String path, NBTType type, MPRRange value, boolean inverted) {
        super(inverted);
        this.path = path;
        this.type = type;
        this.value = value;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        CompoundTag mobTag = new CompoundTag();
        living.saveWithoutId(mobTag);
        String[] splitPath = this.path.split("\\.");
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
                switch (this.type) {
                    case DOUBLE -> {
                        if (tag instanceof NumericTag numericTag)
                            return this.value.isBetween(living, numericTag.getAsDouble());
                        return false;
                    }
                    case INTEGER -> {
                        if (tag instanceof NumericTag numericTag)
                            return this.value.isBetween(living, numericTag.getAsInt());
                        return false;
                    }
                    case BOOLEAN -> {
                        if (tag instanceof ByteTag byteTag)
                            return this.value.isBetween(living, byteTag.getAsByte());
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
                    GsonHelper.getAsString(jObject, "path"),
                    context.deserialize(jObject.get("type"), NBTType.class),
                    context.deserialize(jObject.get("value"), MPRRange.class),
                    MPRCondition.deserializeInverted(jObject)
            );
        }

        @Override
        public JsonElement serialize(MPRNBTCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("path", context.serialize(src.path));
            jObject.add("type", context.serialize(src.type));
            jObject.add("value", context.serialize(src.value));
            return src.endSerialization(jObject);
        }
    }

}
