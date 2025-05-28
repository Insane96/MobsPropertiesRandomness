package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.NBTType;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRNBTProperty.Serializer.class)
public class MPRNBTProperty extends MPRProperty {
    public String path;
    public NBTType type;
    public MPRRange value;

    public MPRNBTProperty(String path, NBTType type, MPRRange value, List<MPRCondition> conditions) {
        super(conditions);
        this.path = path;
        this.type = type;
        this.value = value;
    }

    // {ForgeData:{"enhancedai:miner":1b}}
    // {}
    // {ForgeData:{}}
    @Override
    protected boolean apply(LivingEntity living) {
        CompoundTag mobTag = new CompoundTag();
        living.saveWithoutId(mobTag);
        String[] splitPath = this.path.split("\\.");
        CompoundTag innerCompoundTag = mobTag;
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
                switch (this.type) {
                    case DOUBLE -> {
                        innerCompoundTag.putDouble(splitPath[i], this.value.getFloatBetween(living));
                        living.load(mobTag);
                        return true;
                    }
                    case INTEGER -> {
                        innerCompoundTag.putInt(splitPath[i], this.value.getIntBetween(living));
                        living.load(mobTag);
                        return true;
                    }
                    case BOOLEAN -> {
                        innerCompoundTag.putBoolean(splitPath[i], living.getRandom().nextFloat() < this.value.getFloatBetween(living));
                        living.load(mobTag);
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<MPRNBTProperty>, JsonSerializer<MPRNBTProperty> {
        @Override
        public MPRNBTProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRNBTProperty(
                    GsonHelper.getAsString(jObject, "path"),
                    context.deserialize(jObject.get("type"), NBTType.class),
                    context.deserialize(jObject.get("value"), MPRRange.class),
                    deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRNBTProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("path", context.serialize(src.path));
            jObject.add("type", context.serialize(src.type));
            jObject.add("value", context.serialize(src.value));
            return src.endSerialization(jObject, context);
        }
    }
}
