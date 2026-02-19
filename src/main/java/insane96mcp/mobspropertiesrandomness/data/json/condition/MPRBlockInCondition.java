package insane96mcp.mobspropertiesrandomness.data.json.condition;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.data.ObjTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRBlockInCondition.Serializer.class)
public class MPRBlockInCondition extends MPRCondition {
    List<ObjTag<Block>> blocks;

    public MPRBlockInCondition(List<ObjTag<Block>> blocks, boolean inverted) {
        super(inverted);
        this.blocks = blocks;
    }

    @Override
    protected boolean conditionCheck(LivingEntity living) {
        for (ObjTag<Block> block : this.blocks) {
            AABB bb = living.getBoundingBox();
            for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(bb.minX), Mth.floor(bb.minY), Mth.floor(bb.minZ), Mth.floor(bb.maxX), Mth.floor(bb.maxY), Mth.floor(bb.maxZ))) {
                if (block.matches(living.level().getBlockState(pos).getBlock()))
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
            List<ObjTag<Block>> values = ObjTag.deserializeList(aBlocks, Registries.BLOCK);
            return new MPRBlockInCondition(values, MPRCondition.deserializeInverted(jObject));
        }

        @Override
        public JsonElement serialize(MPRBlockInCondition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("blocks", ObjTag.serializeList(src.blocks));
            return src.endSerialization(jObject);
        }
    }
}
