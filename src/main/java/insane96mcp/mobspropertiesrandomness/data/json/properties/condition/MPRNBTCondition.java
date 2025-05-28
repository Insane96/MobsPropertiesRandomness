package insane96mcp.mobspropertiesrandomness.data.json.properties.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.nbt.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;

@JsonAdapter(MPRNBTCondition.Serializer.class)
public class MPRNBTCondition extends MPRCondition {
    public String nbtTag;
    public NBTType type;
    public MPRRange value;

    public MPRNBTCondition(String nbtPath, NBTType type, MPRRange value, boolean inverted) {
        super(inverted);
        this.nbtTag = nbtPath;
        this.type = type;
        this.value = value;
    }

    //TODO Arrays
    @Override
    protected boolean conditionCheck(LivingEntity livingEntity) {
        CompoundTag mobTag = new CompoundTag();
        livingEntity.saveWithoutId(mobTag);
        String[] splitPath = this.nbtTag.split("\\.");
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
                        if (tag instanceof DoubleTag doubleTag)
                            return this.value.isBetween(livingEntity, doubleTag.getAsFloat());
                        return false;
                    }
                    case INTEGER -> {
                        if (tag instanceof IntTag intTag)
                            return this.value.isBetween(livingEntity, intTag.getAsInt());
                        return false;
                    }
                    case BOOLEAN -> {
                        if (tag instanceof ByteTag byteTag)
                            return this.value.isBetween(livingEntity, byteTag.getAsByte());
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
                    GsonHelper.getAsString(jObject, "nbt_tag"),
                    context.deserialize(jObject.get("type"), NBTType.class),
                    context.deserialize(jObject.get("value"), MPRRange.class),
                    MPRCondition.deserializeInverted(jObject)
            );
        }

        @Override
        public JsonElement serialize(MPRNBTCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = src.serializeInverted();
            jObject.add("nbt_tag", context.serialize(src.nbtTag));
            jObject.add("type", context.serialize(src.type));
            jObject.add("value", context.serialize(src.value));
            return jObject;
        }
    }

    public enum NBTType {
        @SerializedName("double")
        DOUBLE,
        @SerializedName("integer")
        INTEGER,
        @SerializedName("boolean")
        BOOLEAN,
    }
}
