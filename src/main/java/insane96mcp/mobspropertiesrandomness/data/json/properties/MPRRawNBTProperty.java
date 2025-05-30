package insane96mcp.mobspropertiesrandomness.data.json.properties;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.MPRProperties;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRRawNBTProperty.Serializer.class)
public class MPRRawNBTProperty extends MPRProperty {
    public CompoundTag nbt;

    public MPRRawNBTProperty(CompoundTag nbt, List<MPRCondition> conditions) {
        super(conditions);
        this.nbt = nbt;
    }

    @Override
    protected boolean apply(LivingEntity living) {
        CompoundTag mobTag = new CompoundTag();
        living.saveWithoutId(mobTag);
        mobTag = mobTag.copy().merge(this.nbt);
        living.load(mobTag);
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRRawNBTProperty>, JsonSerializer<MPRRawNBTProperty> {
        @Override
        public MPRRawNBTProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            String sNBT = GsonHelper.getAsString(jObject, "nbt");
            CompoundTag nbt;
            try {
                nbt = TagParser.parseTag(sNBT);
            } catch (Exception e) {
                throw new JsonParseException("Failed to parse NBT for Set Raw NBT property: " + sNBT, e);
            }
            return new MPRRawNBTProperty(nbt, MPRProperties.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRRawNBTProperty src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.addProperty("nbt", src.nbt.getAsString());
            return src.endSerialization(jObject, context);
        }
    }
}
