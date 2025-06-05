package insane96mcp.mobspropertiesrandomness.data.json.properties.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import insane96mcp.mobspropertiesrandomness.MPR;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRCondition;
import insane96mcp.mobspropertiesrandomness.data.json.condition.MPRConditionable;
import insane96mcp.mobspropertiesrandomness.data.json.properties.PropertiesRegistry;
import insane96mcp.mobspropertiesrandomness.util.Logger;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class MPREvent extends MPRConditionable {
    public Target target;
    public CommandFunction.CacheableFunction function;

    public MPREvent(List<MPRCondition> conditions) {
        super(conditions);
    }

    public final boolean tryApply(LivingEntity livingEntity) {
        if (!MPRCondition.conditionsApply(this.conditions, livingEntity))
            return false;
        return apply(livingEntity);
    }

    protected abstract boolean apply(LivingEntity living);

    public static List<MPREvent> deserializeList(JsonObject jObject, String memberName, JsonDeserializationContext context) {
        List<MPREvent> events = new ArrayList<>();
        if (!jObject.has(memberName))
            return events;
        JsonArray aEvents = GsonHelper.getAsJsonArray(jObject, memberName);
        for (JsonElement jsonElement : aEvents) {
            JsonObject jObjectProperty = jsonElement.getAsJsonObject();
            ResourceLocation eventId = MPR.locationFrom(GsonHelper.getAsString(jObjectProperty, "event"));
            Type eventType = PropertiesRegistry.get(eventId);
            if (eventType == null) {
                Logger.warn("event %s does not exist. Skipping".formatted(eventId));
                continue;
            }
            events.add(context.deserialize(jObjectProperty, eventType));
        }
        return events;
    }
}
