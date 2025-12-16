package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRBlockInCondition.Serializer.class)
public class MPRBlockInCondition extends MPRCondition {
    List<IdTagMatcher> blocks;

    public MPRBlockInCondition(List<IdTagMatcher> blocks, boolean inverted) {
        super(inverted);
        this.blocks = blocks;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        for (IdTagMatcher block : this.blocks) {
            for (BlockPos pos : BlockPos.betweenClosedStream(living.getBoundingBox()).toList()) {
                if (block.matchesBlock(living.level().getBlockState(pos)))
                    return true;
            }
        }
        return false;
    }

    public static class Serializer implements JsonDeserializer<MPRBlockInCondition>, JsonSerializer<MPRBlockInCondition> {
        @Override
        public MPRBlockInCondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            JsonArray aBlocks = jObject.getAsJsonArray("blocks");
            if (aBlocks == null)
                throw new JsonParseException("Missing blocks array");
            List<IdTagMatcher> values = context.deserialize(aBlocks, IdTagMatcher.LIST_TYPE);
            return new MPRBlockInCondition(values, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRBlockInCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("blocks", context.serialize(src.blocks));
            return src.endSerialization(jObject);
        }
    }
}
