package insane96mcp.mobspropertiesrandomness.data.json.util.modifiable;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRDistanceFromSpawnModifier.Serializer.class)
public class MPRDistanceFromSpawnModifier extends MPRModifier {
    /*
     Each 'blocks' from spawn will increase the value to modify by 'amount_per_blocks'.
     If operation is "multiply" the result is treated as a percentage increase, the formula is 'value * (1 + ((distance + shift) / blocks) * amount_per_blocks)'.
     Else if the operation is "add" the result is added to the value, the formula is 'value + (((distance + shift) / blocks) * amount_per_blocks)'
     E.g. A mob with 50% chance (0.5) to spawn with a potion effect, with this modifier set as blocks = 100, amount_per_blocks = 0.02 and operation = "multiply" when it spawns 150 blocks from world spawn it will have the value modified as 'chance * (1 + ((distance + shift) / blocks) * amount_per_blocks)' = '0.5 * (1 + ((150 + 0) / 100))' = '0.5 * 1.5' (an increase of 50%) = '0.75 (75% chance)'
     */
    public MPRModifiableValue amountPerBlocks;
    public MPRModifiableValue blocks;
    public MPRModifiableValue shift;
    @Nullable
    public MPRModifiableValue cap;

    public MPRDistanceFromSpawnModifier(MPRModifiableValue amountPerBlocks, MPRModifiableValue blocks, MPRModifiableValue shift, @Nullable MPRModifiableValue cap, Operation operation, List<MPRCondition> conditions) {
        super(operation, conditions);
        this.amountPerBlocks = amountPerBlocks;
        this.blocks = blocks;
        this.shift = shift;
        this.cap = cap;
    }

    @Override
    protected double getModifier(LivingEntity living) {
        Vec3 spawnPos = new Vec3(living.level().getLevelData().getXSpawn(), living.level().getLevelData().getYSpawn(), living.level().getLevelData().getZSpawn());
        double distance = (float) spawnPos.distanceTo(living.position());
        distance += this.shift.getValue(living);
        double modifier = (distance / this.blocks.getValue(living)) * this.amountPerBlocks.getValue(living);
        if (this.cap != null)
            modifier = Math.min(modifier, this.cap.getValue(living));
        return modifier;
    }

    public static class Serializer implements JsonDeserializer<MPRDistanceFromSpawnModifier>, JsonSerializer<MPRDistanceFromSpawnModifier> {
        @Override
        public MPRDistanceFromSpawnModifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRDistanceFromSpawnModifier(
                    GsonHelper.getAsObject(jObject, "amount_per_blocks", context, MPRModifiableValue.class),
                    GsonHelper.getAsObject(jObject, "blocks", context, MPRModifiableValue.class),
                    GsonHelper.getAsObject(jObject, "shift", MPRModifiableValue.ZERO, context, MPRModifiableValue.class),
                    GsonHelper.getAsObject(jObject, "cap", null, context, MPRModifiableValue.class),
                    deserializeOperation(jObject, context),
                    MPRCondition.deserializeConditions(jObject, context)
            );
        }

        @Override
        public JsonElement serialize(MPRDistanceFromSpawnModifier src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("amount_per_blocks", context.serialize(src.amountPerBlocks));
            jObject.add("blocks", context.serialize(src.blocks));
            jObject.add("shift", context.serialize(src.shift));
            jObject.add("cap", context.serialize(src.cap));
            return src.endSerialization(jObject, context);
        }
    }
}
