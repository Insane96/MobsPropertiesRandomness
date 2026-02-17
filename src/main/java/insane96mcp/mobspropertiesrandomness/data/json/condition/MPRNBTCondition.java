package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.MPRNbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
        MPRNbt.ResolvedPath resolved = this.nbt.resolvePath(mobTag);
        if (resolved == null)
            return false;
        Tag tag = resolved.parent().get(resolved.key());
        if (tag == null)
            return false;
        return this.nbt.checkValue(tag, living);
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
