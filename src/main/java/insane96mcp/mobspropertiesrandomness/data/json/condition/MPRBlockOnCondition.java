package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRBlockOnCondition.Serializer.class)
public class MPRBlockOnCondition extends MPRCondition {
    List<IdTagMatcher> blocks;

    public MPRBlockOnCondition(List<IdTagMatcher> blocks, boolean inverted) {
        super(inverted);
        this.blocks = blocks;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        for (IdTagMatcher block : this.blocks) {
            // TODO Find a way to make it work so it checks the block the entity is actually standing on, and not the block below. On spawn the mainSupportingBlockPos is not yet set (I think) thus just checking the block below
            if (block.matchesBlock(living.getBlockStateOn()))
                return true;
        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<MPRBlockOnCondition>, JsonSerializer<MPRBlockOnCondition> {
        @Override
        public MPRBlockOnCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            JsonArray aBlocks = jObject.getAsJsonArray("blocks");
            if (aBlocks == null)
                throw new JsonParseException("Missing blocks array");
            List<IdTagMatcher> values = context.deserialize(aBlocks, IdTagMatcher.LIST_TYPE);
            return new MPRBlockOnCondition(values, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRBlockOnCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("blocks", context.serialize(src.blocks));
            return src.endSerialization(jObject);
        }
    }
}
