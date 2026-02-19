package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.data.ObjTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRBlockOnCondition.Serializer.class)
public class MPRBlockOnCondition extends MPRCondition {
    List<ObjTag<Block>> blocks;

    public MPRBlockOnCondition(List<ObjTag<Block>> blocks, boolean inverted) {
        super(inverted);
        this.blocks = blocks;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        for (ObjTag<Block> block : this.blocks) {
            if (block.matches(living.getBlockStateOn().getBlock()))
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
            List<ObjTag<Block>> values = ObjTag.deserializeList(aBlocks, Registries.BLOCK);
            return new MPRBlockOnCondition(values, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRBlockOnCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("blocks", ObjTag.serializeList(src.blocks));
            return src.endSerialization(jObject);
        }
    }
}
