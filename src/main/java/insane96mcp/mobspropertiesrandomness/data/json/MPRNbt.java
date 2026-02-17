package insane96mcp.mobspropertiesrandomness.data.json;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.util.NBTType;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.nbt.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

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

    /**
     * Navigates the path inside root and returns the parent CompoundTag of the final key.
     * Returns null if a non-compound intermediate tag is found.
     */
    @Nullable
    public ResolvedPath resolvePath(CompoundTag root) {
        String[] splitPath = this.path.split("\\.");
        CompoundTag current = root;
        for (int i = 0; i < splitPath.length - 1; i++) {
            Tag tag = current.get(splitPath[i]);
            if (tag instanceof CompoundTag compoundTag)
                current = compoundTag;
            else
                return null;
        }
        return new ResolvedPath(current, splitPath[splitPath.length - 1]);
    }

    /**
     * Navigates the path inside root and returns the parent CompoundTag of the final key.
     * Missing intermediate CompoundTags are created.
     */
    public ResolvedPath resolveOrCreatePath(CompoundTag root) {
        String[] splitPath = this.path.split("\\.");
        CompoundTag current = root;
        for (int i = 0; i < splitPath.length - 1; i++) {
            Tag tag = current.get(splitPath[i]);
            if (tag instanceof CompoundTag compoundTag) {
                current = compoundTag;
            } else {
                CompoundTag newTag = new CompoundTag();
                current.put(splitPath[i], newTag);
                current = newTag;
            }
        }
        return new ResolvedPath(current, splitPath[splitPath.length - 1]);
    }

    /**
     * Writes a value to the target CompoundTag at the given key, based on this nbt's type.
     */
    public void writeValue(CompoundTag target, String key, LivingEntity living) {
        switch (this.type) {
            case DOUBLE -> target.putDouble(key, this.value.getDoubleBetween(living));
            case INTEGER -> target.putInt(key, this.value.getIntBetween(living));
            case BOOLEAN -> target.putBoolean(key, living.getRandom().nextFloat() < this.value.getDoubleBetween(living));
            case STRING -> target.putString(key, this.stringValue);
        }
    }

    /**
     * Checks if the given tag matches this nbt's expected value.
     */
    public boolean checkValue(Tag tag, LivingEntity living) {
        return switch (this.type) {
            case DOUBLE -> tag instanceof NumericTag numericTag && this.value.isBetween(living, numericTag.getAsDouble());
            case INTEGER -> tag instanceof NumericTag numericTag && this.value.isBetween(living, numericTag.getAsInt());
            case BOOLEAN -> tag instanceof ByteTag byteTag && this.value.isBetween(living, byteTag.getAsByte());
            case STRING -> tag instanceof StringTag stringTag && this.stringValue.equals(stringTag.getAsString());
        };
    }

    public record ResolvedPath(CompoundTag parent, String key) {}

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
