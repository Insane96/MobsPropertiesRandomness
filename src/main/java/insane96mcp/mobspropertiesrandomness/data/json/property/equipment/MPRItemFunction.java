package insane96mcp.mobspropertiesrandomness.data.json.property.equipment;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.util.MPRLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class MPRItemFunction extends MPRConditionable {
    public MPRItemFunction(List<MPRCondition> conditions) {
        super(conditions);
    }
    public final boolean tryApply(LivingEntity livingEntity, ItemStack stack, EquipmentSlot slot) {
        if (!MPRCondition.conditionsApply(this.conditions, livingEntity))
            return false;
        return apply(livingEntity, stack, slot);
    }

    protected abstract boolean apply(LivingEntity living, ItemStack stack, EquipmentSlot slot);

    public static List<MPRItemFunction> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        List<MPRItemFunction> itemFunctions = new ArrayList<>();
        if (!jObject.has(memberName))
            return itemFunctions;
        JsonArray aFunctions = GsonHelper.getAsJsonArray(jObject, memberName);
        for (JsonElement jsonElement : aFunctions) {
            JsonObject jObjectItemFunction = jsonElement.getAsJsonObject();
            ResourceLocation itemFunctionId = MPR.locationFrom(GsonHelper.getAsString(jObjectItemFunction, "function"));
            Type itemFunctionType = ItemFunctionsRegistry.get(itemFunctionId);
            if (itemFunctionType == null) {
                MPRLogger.warn("item function %s does not exist. Skipping".formatted(itemFunctionId));
                continue;
            }
            itemFunctions.add(context.deserialize(jObjectItemFunction, itemFunctionType));
        }
        return itemFunctions;
    }
}
