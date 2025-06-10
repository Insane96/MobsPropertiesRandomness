package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.util.modifiable.MPRModifiableValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.List;

@JsonAdapter(MPRSetDropChanceItemFunction.Serializer.class)
public class MPRSetDropChanceItemFunction extends MPRItemFunction {
    public MPRModifiableValue dropChance;

    public MPRSetDropChanceItemFunction(MPRModifiableValue dropChance, List<MPRCondition> conditions) {
        super(conditions);
        this.dropChance = dropChance;
    }

    @Override
    protected boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot) {
        if (!(living instanceof Mob mob))
            return false;
        mob.setDropChance(slot, (float) this.dropChance.getValue(mob));
        return true;
    }

    public static class Serializer implements JsonDeserializer<MPRSetDropChanceItemFunction>, JsonSerializer<MPRSetDropChanceItemFunction> {
        @Override
        public MPRSetDropChanceItemFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = json.getAsJsonObject();
            return new MPRSetDropChanceItemFunction(context.deserialize(jObject.get("drop_chance"), MPRModifiableValue.class), MPRCondition.deserializeConditions(jObject, context));
        }

        @Override
        public JsonElement serialize(MPRSetDropChanceItemFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jObject = new JsonObject();
            jObject.add("drop_chance", context.serialize(src.dropChance));
            return src.endSerialization(jObject, context);
        }
    }
}
