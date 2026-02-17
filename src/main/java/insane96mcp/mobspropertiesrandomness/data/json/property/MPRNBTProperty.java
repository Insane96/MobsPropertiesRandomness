package insane96mcp.mobspropertiesrandomness.data.json.property;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.MPRNbt;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRNBTProperty.Serializer.class)
public class MPRNBTProperty extends MPRProperty {
    public MPRNbt nbt;

    public MPRNBTProperty(MPRNbt nbt, List<MPRCondition> conditions) {
        super(conditions);
        this.nbt = nbt;
    }

    @Override
    public boolean apply(LivingEntity living) {
        CompoundTag mobTag = new CompoundTag();
        living.saveWithoutId(mobTag);
        MPRNbt.ResolvedPath resolved = this.nbt.resolveOrCreatePath(mobTag);
        this.nbt.writeValue(resolved.parent(), resolved.key(), living);
        living.load(mobTag);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRNBTProperty>, JsonSerializer<MPRNBTProperty> {
        @Override
        public MPRNBTProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRNBTProperty(
                    context.deserialize(jObject, MPRNbt.class),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRNBTProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = context.serialize(src.nbt).getAsJsonObject();
            return src.endSerialization(jObject, context);
        }
    }
}
