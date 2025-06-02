package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.MPRNBT;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRNBTProperty.Serializer.class)
public class MPRNBTProperty extends MPRProperty {
    public MPRNBT nbt;

    public MPRNBTProperty(MPRNBT nbt, List<MPRCondition> conditions) {
        super(conditions);
        this.nbt = nbt;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        CompoundTag mobTag = new CompoundTag();
        living.saveWithoutId(mobTag);
        String[] splitPath = this.nbt.path.split("\\.");
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
                switch (this.nbt.type) {
                    case DOUBLE -> {
                        innerCompoundTag.putDouble(splitPath[i], this.nbt.value.getDoubleBetween(living));
                        living.load(mobTag);
                        return true;
                    }
                    case INTEGER -> {
                        innerCompoundTag.putInt(splitPath[i], this.nbt.value.getIntBetween(living));
                        living.load(mobTag);
                        return true;
                    }
                    case BOOLEAN -> {
                        innerCompoundTag.putBoolean(splitPath[i], living.getRandom().nextFloat() < this.nbt.value.getDoubleBetween(living));
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
                    context.deserialize(jObject, MPRNBT.class),
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
