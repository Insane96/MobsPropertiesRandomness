package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.NBTType;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

@JsonAdapter(MPRNbt.Serializer.class)
public class MPRNbt {
    public String path;
    public NBTType type;
    public MPRRange value;
    public String stringValue;

    public MPRNbt(String path, NBTType type, MPRRange value, String stringValue) {
        this.path = path;
        this.type = type;
        this.value = value;
        this.stringValue = stringValue;
    }

    @Nullable
    public static CompoundTag deserialize(@Nullable String s) throws JsonParseException {
        if (s == null)
            return null;
        CompoundTag nbt;
        try {
            nbt = TagParser.parseTag(s);
        } catch (Exception e) {
            throw new JsonParseException("Failed to parse NBT: " + s, e);
        }
        return nbt;
    }

    public static class Serializer implements JsonDeserializer<MPRNbt>, JsonSerializer<MPRNbt> {
        @Override
        public MPRNbt deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            NBTType type = GsonHelper.getAsObject(jObject, "type", context, NBTType.class);
            MPRRange value = GsonHelper.getAsObject(jObject, "value", null, context, MPRRange.class);
            String stringValue = GsonHelper.getAsString(jObject, "string_value", null);
            if (type == NBTType.STRING && stringValue == null)
                throw new JsonParseException("String NBT requires a `string_value`");
            else if (type != NBTType.STRING && value == null)
                throw new JsonParseException("Numeric NBT requires a `value`");
            return new MPRNbt(
                    GsonHelper.getAsString(jObject, "path"),
                    type,
                    value,
                    stringValue
            );
        }

        @Override
        public JsonElement serialize(MPRNbt src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("path", context.serialize(src.path));
            jObject.add("type", context.serialize(src.type));
            jObject.add("value", context.serialize(src.value));
            jObject.addProperty("string_value", src.stringValue);
            return jObject;
        }
    }
}
